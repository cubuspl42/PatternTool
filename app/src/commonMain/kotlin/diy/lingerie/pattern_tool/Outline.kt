package diy.lingerie.pattern_tool

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.numeric.equalsWithToleranceOrNull
import dev.toolkt.geometry.Line
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.splines.ClosedSpline
import dev.toolkt.geometry.splines.OpenSpline
import dev.toolkt.geometry.splines.Spline
import dev.toolkt.geometry.transformations.ReflectionOverLine
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.iterable.crackAt
import dev.toolkt.core.iterable.mapCarrying
import dev.toolkt.core.iterable.shiftLeft
import dev.toolkt.core.iterable.uncons
import dev.toolkt.core.iterable.withNextBy
import dev.toolkt.core.iterable.withNextCyclic
import dev.toolkt.core.iterable.withPreviousCyclic
import dev.toolkt.core.numeric.NumericTolerance
import kotlin.jvm.JvmInline

/**
 * Pattern piece outline. All coordinates are in millimeters.
 */
data class Outline(
    val cyclicLinks: List<Link>,
) : NumericObject {
    data class EdgeMetadata(
        val seamAllowance: SeamAllowance,
    )

    data class EdgeMetadataMap(
        val edgeMetadataByEdgeIndex: Map<Int, EdgeMetadata>,
        val defaultEdgeMetadata: EdgeMetadata,
    ) {
        fun getEdgeMetadata(
            edgeIndex: Int,
        ): EdgeMetadata = edgeMetadataByEdgeIndex[edgeIndex] ?: defaultEdgeMetadata
    }

    data class Arm(
        val anchor: Anchor,
        val handle: Handle?,
    ) {
        val effectiveHandlePosition: Point
            get() = handle?.position ?: anchor.position
    }

    @JvmInline
    value class Anchor(
        val position: Point,
    ) : NumericObject {
        fun transformBy(
            transformation: Transformation,
        ): Anchor = Anchor(
            position = position.transformBy(transformation = transformation),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericTolerance,
        ): Boolean = when {
            other !is Anchor -> false
            !position.equalsWithTolerance(other.position, tolerance) -> false
            else -> true
        }
    }

    @JvmInline
    value class Handle(
        val position: Point,
    ) : NumericObject {
        fun transformBy(
            transformation: Transformation,
        ): Handle = Handle(
            position = position.transformBy(transformation = transformation),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericTolerance,
        ): Boolean = when {
            other !is Handle -> false
            !position.equalsWithTolerance(other.position, tolerance) -> false
            else -> true
        }
    }

    sealed class Joint : NumericObject {
        abstract fun transformBy(
            transformation: Transformation,
        ): Joint

        val rearArm: Arm
            get() = Arm(
                anchor = anchor,
                handle = rearHandle,
            )

        val frontArm: Arm
            get() = Arm(
                anchor = anchor,
                handle = frontHandle,
            )

        /**
         * An unconstrained (typically sharp) joint
         */
        data class Free(
            override val rearHandle: Handle?,
            override val anchor: Anchor,
            override val frontHandle: Handle?,
        ) : Joint() {
            override fun transformBy(
                transformation: Transformation,
            ): Free = Free(
                rearHandle = rearHandle?.transformBy(transformation = transformation),
                anchor = anchor.transformBy(transformation = transformation),
                frontHandle = frontHandle?.transformBy(transformation = transformation),
            )

            override fun equalsWithTolerance(
                other: NumericObject,
                tolerance: NumericTolerance,
            ): Boolean = when {
                other !is Free -> false

                !rearHandle.equalsWithToleranceOrNull(
                    other.rearHandle,
                    tolerance = tolerance,
                ) -> false

                !anchor.equalsWithTolerance(other.anchor, tolerance) -> false

                !frontHandle.equalsWithToleranceOrNull(
                    other.frontHandle,
                    tolerance = tolerance,
                ) -> false

                else -> true
            }
        }

        /**
         * A smooth joint
         */
        data class Smooth(
            override val rearHandle: Handle,
            /**
             * The coordinate of the anchor point on the control line segment
             */
            val anchorCoord: OpenCurve.Coord,
            override val frontHandle: Handle,
        ) : Joint() {
            /**
             * The control line segment going from the rear to the front handle
             */
            private val controlLineSegment: LineSegment
                get() = LineSegment(
                    start = rearHandle.position,
                    end = frontHandle.position,
                )

            fun reversed(): Smooth = Smooth(
                rearHandle = frontHandle,
                anchorCoord = anchorCoord.complement,
                frontHandle = rearHandle,
            )

            override fun transformBy(
                transformation: Transformation,
            ): Smooth = Smooth(
                rearHandle = rearHandle.transformBy(transformation = transformation),
                anchorCoord = anchorCoord,
                frontHandle = frontHandle.transformBy(transformation = transformation),
            )

            override val anchor: Anchor
                get() = Anchor(
                    position = controlLineSegment.evaluate(coord = anchorCoord),
                )

            override fun equalsWithTolerance(
                other: NumericObject,
                tolerance: NumericTolerance,
            ): Boolean = when {
                other !is Smooth -> false
                !rearHandle.equalsWithTolerance(other.rearHandle, tolerance) -> false
                !anchorCoord.equalsWithTolerance(other.anchorCoord, tolerance) -> false
                !frontHandle.equalsWithTolerance(other.frontHandle, tolerance) -> false
                else -> true
            }
        }

        abstract val rearHandle: Handle?

        abstract val anchor: Anchor

        abstract val frontHandle: Handle?
    }

    data class Edge(
        val startHandle: Handle?,
        val intermediateJoints: List<Joint.Smooth>,
        val endHandle: Handle?,
        val metadata: EdgeMetadata,
    ) : NumericObject {
        companion object {
            fun line(
                metadata: EdgeMetadata,
            ): Edge = Edge(
                startHandle = null,
                intermediateJoints = emptyList(),
                endHandle = null,
                metadata = metadata,
            )
        }

        fun isStraight() = when {
            startHandle != null -> false
            endHandle != null -> false
            intermediateJoints.isNotEmpty() -> false
            else -> true
        }

        val startHandlePosition: Point?
            get() = startHandle?.position

        val endHandlePosition: Point?
            get() = endHandle?.position

        fun transformBy(
            transformation: Transformation,
        ): Edge = Edge(
            startHandle = startHandle?.transformBy(transformation = transformation),
            intermediateJoints = intermediateJoints.map {
                it.transformBy(transformation = transformation)
            },
            endHandle = endHandle?.transformBy(transformation = transformation),
            metadata = metadata,
        )

        fun reversed(): Edge = Edge(
            startHandle = endHandle,
            intermediateJoints = intermediateJoints.reversed().map {
                it.reversed()
            },
            endHandle = startHandle,
            metadata = metadata,
        )

        val seamAllowance: SeamAllowance
            get() = metadata.seamAllowance

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericTolerance,
        ): Boolean = when {
            other !is Edge -> false

            !startHandle.equalsWithToleranceOrNull(
                other.startHandle,
                tolerance = tolerance,
            ) -> false

            !intermediateJoints.equalsWithTolerance(other.intermediateJoints, tolerance) -> false

            !endHandle.equalsWithToleranceOrNull(
                other.endHandle,
                tolerance = tolerance,
            ) -> false

            else -> true
        }
    }

    /**
     * A link in the chain describing the outline fully. Together with the next
     * adjacent link, it can be used to construct a verge.
     */
    data class Link(
        val edge: Edge,
        val endAnchor: Anchor,
    ) : NumericObject {
        fun bind(
            startAnchor: Anchor,
        ): Verge = Verge(
            startAnchor = startAnchor,
            edge = edge,
            endAnchor = endAnchor,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericTolerance,
        ): Boolean = when {
            other !is Link -> false
            !edge.equalsWithTolerance(other.edge, tolerance) -> false
            !endAnchor.equalsWithTolerance(other.endAnchor, tolerance) -> false
            else -> true
        }
    }

    data class Verge(
        val startAnchor: Anchor,
        val edge: Edge,
        val endAnchor: Anchor,
    ) : NumericObject {
        companion object {
            fun line(
                startAnchor: Anchor,
                edgeMetadata: EdgeMetadata,
                endAnchor: Anchor,
            ): Verge = Verge(
                startAnchor = startAnchor,
                edge = Edge.line(metadata = edgeMetadata),
                endAnchor = endAnchor,
            )

            fun reconstruct(
                bezierCurve: BezierCurve,
                edgeMetadata: EdgeMetadata,
            ): Verge = Outline.Verge(
                startAnchor = Outline.Anchor(
                    position = bezierCurve.start,
                ),
                edge = Outline.Edge(
                    startHandle = Outline.Handle(
                        position = bezierCurve.firstControl,
                    ),
                    intermediateJoints = emptyList(),
                    endHandle = Outline.Handle(
                        position = bezierCurve.lastControl,
                    ),
                    metadata = edgeMetadata,
                ),
                endAnchor = Outline.Anchor(
                    position = bezierCurve.end,
                ),
            )

            fun reconstruct(
                smoothCurve: OpenCurve,
                edgeMetadata: EdgeMetadata,
            ): Verge {
                val (firstCurve, trailingCurves) = smoothCurve.subCurves.uncons()
                    ?: throw AssertionError("List of smooth curves must not be empty")

                val (intermediateJoints, lastCurve) = trailingCurves.mapCarrying(
                    initialCarry = firstCurve,
                ) { previousCurve, curve ->
                    val previousBezierCurve = previousCurve as? BezierCurve
                        ?: throw IllegalArgumentException("Cannot construct a smooth joint from non-bezier curves")

                    val bezierCurve = curve as? BezierCurve
                        ?: throw IllegalArgumentException("Cannot construct a smooth joint from non-bezier curves")

                    val rearHandlePosition = previousBezierCurve.secondControl

                    val anchorPosition = bezierCurve.start
                    val frontHandlePosition = bezierCurve.firstControl

                    val anchorDistance = Point.distanceBetween(
                        rearHandlePosition,
                        anchorPosition,
                    )

                    val controlSegmentLength = Point.distanceBetween(
                        rearHandlePosition,
                        frontHandlePosition,
                    )

                    val t = anchorDistance / controlSegmentLength

                    val joint = Joint.Smooth(
                        rearHandle = Outline.Handle(position = rearHandlePosition),
                        anchorCoord = OpenCurve.Coord(t = t),
                        frontHandle = Outline.Handle(position = frontHandlePosition),
                    )

                    Pair(joint, bezierCurve)
                }

                return Outline.Verge(
                    startAnchor = Outline.Anchor(
                        position = firstCurve.start,
                    ),
                    edge = Outline.Edge(
                        startHandle = (firstCurve as? BezierCurve)?.let {
                            Outline.Handle(position = it.firstControl)
                        },
                        intermediateJoints = intermediateJoints,
                        endHandle = (lastCurve as? BezierCurve)?.let {
                            Outline.Handle(position = it.secondControl)
                        },
                        metadata = edgeMetadata,
                    ),
                    endAnchor = Outline.Anchor(
                        position = lastCurve.end,
                    ),
                )
            }
        }

        val startArm: Arm
            get() = Arm(
                anchor = startAnchor,
                handle = edge.startHandle,
            )

        val endArm: Arm
            get() = Arm(
                anchor = endAnchor,
                handle = edge.endHandle,
            )

        val curveSpline: OpenSpline
            get() {
                val intermediateJoints = edge.intermediateJoints

                val firstJoint = intermediateJoints.firstOrNull() ?: return OpenSpline(
                    firstCurve = BezierCurve(
                        start = startArm.anchor.position,
                        firstControl = startArm.effectiveHandlePosition,
                        secondControl = endArm.effectiveHandlePosition,
                        end = endArm.anchor.position,
                    ),
                    trailingSequentialLinks = emptyList(),
                )

                return OpenSpline(
                    firstCurve = BezierCurve(
                        start = startArm.anchor.position,
                        firstControl = startArm.effectiveHandlePosition,
                        secondControl = firstJoint.rearHandle.position,
                        end = firstJoint.anchor.position,
                    ),
                    trailingSequentialLinks = intermediateJoints.withNextBy(
                        outerRight = endArm,
                        selector = Joint.Smooth::rearArm,
                    ).map { (joint, nextJoinRearArm) ->
                        val frontArm = joint.frontArm

                        Spline.Link(
                            edge = BezierCurve.Edge(
                                firstControl = frontArm.effectiveHandlePosition,
                                secondControl = nextJoinRearArm.effectiveHandlePosition,
                            ),
                            end = nextJoinRearArm.anchor.position,
                        )
                    },
                )
            }

        val startAnchorPosition: Point
            get() = startAnchor.position

        val endAnchorPosition: Point
            get() = endAnchor.position

        val seamOffsetCurve: OpenCurve
            get() = curveSpline.findOffsetCurve(
                offset = edge.seamAllowance.allowanceMm,
            )

        fun reversed(): Verge = Verge(
            startAnchor = endAnchor,
            edge = edge.reversed(),
            endAnchor = startAnchor,
        )

        fun splitAt(
            edgeCoord: OpenCurve.Coord,
        ): Pair<Verge, Verge> {
            val (firstSubCurve, secondSubCurve) = curveSpline.splitAt(coord = edgeCoord)

            return Pair(
                reconstruct(
                    smoothCurve = firstSubCurve,
                    edgeMetadata = edge.metadata,
                ),
                reconstruct(
                    smoothCurve = secondSubCurve,
                    edgeMetadata = edge.metadata,
                ),
            )
        }

        fun transformBy(
            transformation: Transformation,
        ): Verge = Verge(
            startAnchor = startAnchor.transformBy(transformation = transformation),
            edge = edge.transformBy(
                transformation = transformation,
            ),
            endAnchor = endAnchor.transformBy(transformation = transformation),
        )

        override fun equalsWithTolerance(
            other: NumericObject, tolerance: NumericTolerance
        ): Boolean = when {
            other !is Verge -> false
            !startAnchor.equalsWithTolerance(other.startAnchor, tolerance) -> false
            !edge.equalsWithTolerance(other.edge, tolerance) -> false
            !endAnchor.equalsWithTolerance(other.endAnchor, tolerance) -> false
            else -> true
        }
    }

    data class Coord(
        /**
         * The index of the edge
         */
        val edgeIndex: Int,
        /**
         * The local coord on the edge at [edgeIndex]
         */
        val edgeCoord: OpenCurve.Coord,
    )

    companion object {
        /**
         * @param cyclicSmoothCurves a list of cyclic smooth curves
         * @return an outline with edges constructed from respective curves from
         * [cyclicSmoothCurves]
         */
        fun reconstruct(
            cyclicSmoothCurves: List<OpenCurve>,
            edgeMetadataMap: EdgeMetadataMap,
        ): Outline = connect(
            cyclicSmoothCurves.mapIndexed { edgeIndex, smoothCurve ->
                val edgeMetadata = edgeMetadataMap.getEdgeMetadata(edgeIndex = edgeIndex)

                Verge.reconstruct(
                    smoothCurve = smoothCurve,
                    edgeMetadata = edgeMetadata,
                )
            },
        )

        fun connect(
            cyclicVerges: List<Verge>,
        ): Outline = Outline(
            cyclicLinks = cyclicVerges.withNextCyclic().mapIndexed { index, (verge, nextVerge) ->
                if (verge.endAnchorPosition != nextVerge.startAnchorPosition) {
                    throw IllegalArgumentException("Verge #$index does not connect to the next verge")
                }

                Link(
                    edge = verge.edge,
                    endAnchor = verge.endAnchor,
                )
            },
        )

        private fun closeOpenOutline(
            sequentialVerges: List<Verge>,
            closingEdgeMetadata: EdgeMetadata,
        ): Outline {
            val firstVerge = sequentialVerges.first()
            val lastVerge = sequentialVerges.last()

            val closingVerge = Verge.line(
                startAnchor = lastVerge.endAnchor,
                edgeMetadata = closingEdgeMetadata,
                endAnchor = firstVerge.startAnchor,
            )

            return connect(
                cyclicVerges = sequentialVerges + closingVerge,
            )
        }
    }

    init {
        require(cyclicLinks.size >= 2)
    }

    val verges: List<Verge>
        get() = cyclicLinks.withPreviousCyclic().map { (prevLink, link) ->
            link.bind(
                startAnchor = prevLink.endAnchor,
            )
        }

    val edgeCurves: List<OpenCurve>
        get() = verges.map { it.curveSpline }

    val innerSpline: ClosedSpline
        get() = ClosedSpline.connect(
            cyclicCurves = verges.map { verge ->
                verge.curveSpline
            },
        )

    fun findSeamContour(): ClosedSpline = ClosedSpline.interconnect(
        separatedCurves = verges.map {
            it.seamOffsetCurve
        },
    )

    fun cut(
        cutLineSegment: LineSegment,
        cutEdgeMetadata: EdgeMetadata,
    ): Pair<Outline, Outline> {
        val intersectionCoords = findIntersectionCoords(lineSegment = cutLineSegment)

        if (intersectionCoords.size != 2) {
            throw IllegalArgumentException("The given line segment does not intersect the outline at exactly two points")
        }

        val (firstIntersectionCoord, secondIntersectionCoord) = intersectionCoords

        return splitAt(
            firstSplitCoord = firstIntersectionCoord,
            secondSplitCoord = secondIntersectionCoord,
            splitEdgeMetadata = cutEdgeMetadata,
        )
    }

    private fun findIntersectionCoords(
        lineSegment: LineSegment,
    ): List<Coord> = edgeCurves.withIndex().mapNotNull { (edgeIndex, edgeCurve) ->
        val intersections = OpenCurve.findIntersections(
            subjectOpenCurve = lineSegment,
            objectOpenCurve = edgeCurve,
        )

        if (intersections.isEmpty()) return@mapNotNull null

        val intersection = intersections.singleOrNull()
            ?: throw IllegalArgumentException("The given line segment intersects edge #$edgeIndex at multiple points")

        Coord(
            edgeIndex = edgeIndex,
            edgeCoord = intersection.subjectCoord,
        )
    }

    private fun splitAt(
        firstSplitCoord: Coord,
        secondSplitCoord: Coord,
        splitEdgeMetadata: EdgeMetadata,
    ): Pair<Outline, Outline> {
        val (firstSequentialVerges, secondSequentialVerges) = verges.crackAt(
            firstIndex = firstSplitCoord.edgeIndex,
            crackFirst = { it.splitAt(edgeCoord = firstSplitCoord.edgeCoord) },
            secondIndex = secondSplitCoord.edgeIndex,
            crackSecond = { it.splitAt(edgeCoord = secondSplitCoord.edgeCoord) },
        )

        val firstSubOutline = closeOpenOutline(
            sequentialVerges = firstSequentialVerges,
            closingEdgeMetadata = splitEdgeMetadata,
        )

        val secondSubOutline = closeOpenOutline(
            sequentialVerges = secondSequentialVerges,
            closingEdgeMetadata = splitEdgeMetadata,
        )

        return Pair(
            firstSubOutline,
            secondSubOutline,
        )
    }

    /**
     * Mirrors the outline over the edge at [edgeIndex].
     */
    fun mirror(
        edgeIndex: Int,
    ): Outline {
        val shiftedVerges = verges.shiftLeft(edgeIndex)

        val (removedVerge, remainingVerges) = shiftedVerges.uncons()!!

        if (!removedVerge.edge.isStraight()) {
            throw IllegalArgumentException("Cannot mirror over a non-straight verge")
        }

        val reflectionLine = Line.throughPoints(
            removedVerge.startAnchorPosition,
            removedVerge.endAnchorPosition,
        ) ?: throw IllegalArgumentException("Cannot mirror over a zero-length line")

        val mirroredVerges = remainingVerges.map {
            it.reversed().transformBy(
                transformation = ReflectionOverLine(
                    line = reflectionLine,
                ),
            )
        }

        return connect(
            cyclicVerges = remainingVerges + mirroredVerges,
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean = when {
        other !is Outline -> false
        !cyclicLinks.equalsWithTolerance(other.cyclicLinks, tolerance = tolerance) -> false
        else -> true
    }
}
