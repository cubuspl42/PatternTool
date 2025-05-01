package diy.lingerie.pattern_tool

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.equalsWithTolerance
import diy.lingerie.algebra.equalsWithToleranceOrNull
import diy.lingerie.geometry.Line
import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.curves.bezier.BezierCurve
import diy.lingerie.geometry.curves.bezier.PolyBezierCurve
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.iterable.shiftLeft
import diy.lingerie.utils.iterable.splitBefore
import diy.lingerie.utils.iterable.uncons
import diy.lingerie.utils.iterable.withNextCyclic
import diy.lingerie.utils.iterable.withPreviousCyclic
import kotlin.jvm.JvmInline

/**
 * Pattern piece outline. All coordinates are in millimeters.
 */
data class Outline(
    val links: List<Link>,
) : NumericObject {
    data class EdgeMetadata(
        val seamAllowance: SeamAllowance,
    )

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
            tolerance: NumericObject.Tolerance,
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
            tolerance: NumericObject.Tolerance,
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
                tolerance: NumericObject.Tolerance,
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
            companion object {
                fun reconstruct(
                    bezierJoint: BezierCurve.Joint,
                ): Smooth = Smooth(
                    rearHandle = Handle(
                        position = bezierJoint.rearControl,
                    ),
                    anchorCoord = bezierJoint.coord,
                    frontHandle = Handle(
                        position = bezierJoint.frontControl,
                    ),
                )
            }

            /**
             * The control line segment going from the rear to the front handle
             */
            private val controlLineSegment: LineSegment
                get() = LineSegment(
                    start = rearHandle.position,
                    end = frontHandle.position,
                )

            fun toBezierJoint() = BezierCurve.Joint(
                rearControl = rearHandle.position,
                coord = anchorCoord,
                frontControl = frontHandle.position,
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
                tolerance: NumericObject.Tolerance,
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
            fun reconstruct(
                curveEdge: BezierCurve.Edge,
                metadata: EdgeMetadata,
            ): Edge = Edge(
                startHandle = Handle(
                    position = curveEdge.firstControl,
                ),
                intermediateJoints = curveEdge.joints.map {
                    Joint.Smooth.reconstruct(bezierJoint = it)
                },
                endHandle = Handle(
                    position = curveEdge.lastControl,
                ),
                metadata = metadata,
            )

            fun reconstruct(
                bezierCurve: BezierCurve,
                metadata: EdgeMetadata,
            ): Edge = reconstruct(
                curveEdge = bezierCurve.edge,
                metadata = metadata,
            )

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
            other: NumericObject, tolerance: NumericObject.Tolerance
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
        companion object {
            fun reconstruct(
                splineLink: Spline.Link,
                edgeMetadata: EdgeMetadata,
            ): Link = Link(
                edge = when (val edge = splineLink.edge) {
                    is BezierCurve.Edge -> Edge.reconstruct(
                        curveEdge = edge,
                        metadata = edgeMetadata,
                    )

                    is LineSegment.Edge -> Edge.line(
                        metadata = edgeMetadata,
                    )

                    else -> throw IllegalArgumentException("The edge is not a supported type")
                },
                endAnchor = Anchor(
                    position = splineLink.end,
                ),
            )
        }

        fun bind(
            startAnchor: Anchor,
        ): Verge = Verge(
            startAnchor = startAnchor,
            edge = edge,
            endAnchor = endAnchor,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
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
    ) {
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
                edgeCurve: BezierCurve,
                edgeMetadata: EdgeMetadata,
            ): Verge = Verge(
                startAnchor = Anchor(
                    position = edgeCurve.start,
                ),
                edge = Edge.reconstruct(
                    bezierCurve = edgeCurve,
                    metadata = edgeMetadata,
                ),
                endAnchor = Anchor(
                    position = edgeCurve.end,
                ),
            )
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

        val curveEdge: PolyBezierCurve.Edge
            get() = PolyBezierCurve.Edge(
                firstControl = edge.startHandlePosition ?: startAnchorPosition,
                joints = edge.intermediateJoints.map {
                    it.toBezierJoint()
                },
                lastControl = edge.endHandlePosition ?: endAnchorPosition,
            )

        val curve: BezierCurve
            get() = curveEdge.bind(
                start = startAnchorPosition,
                end = endAnchorPosition,
            )

        val innerSplineLink: Spline.Link
            get() = Spline.Link(
                edge = curveEdge,
                end = endAnchorPosition,
            )

        val startAnchorPosition: Point
            get() = startAnchor.position

        val endAnchorPosition: Point
            get() = endAnchor.position

        val seamOffsetCurve: PrimitiveCurve
            get() = curve.findOffsetCurve(
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
            val (firstSubCurve, secondSubCurve) = curve.splitAt(coord = edgeCoord)

            return Pair(
                reconstruct(
                    edgeCurve = firstSubCurve,
                    edgeMetadata = edge.metadata,
                ),
                reconstruct(
                    edgeCurve = secondSubCurve,
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
        fun connect(
            verges: List<Verge>,
        ): Outline = Outline(
            links = verges.withNextCyclic().mapIndexed { index, (verge, nextSegment) ->
                if (verge.endAnchorPosition != nextSegment.startAnchorPosition) {
                    throw IllegalArgumentException("Verge #$index does not connect to the next verge")
                }

                Link(
                    edge = verge.edge,
                    endAnchor = verge.endAnchor,
                )
            },
        )

        /**
         * @param closedSpline - Closed spline to reconstruct the outline from.
         * Must be expressed in millimeters.
         */
        fun reconstruct(
            closedSpline: ClosedSpline,
            edgeMetadata: EdgeMetadata,
        ): Outline = Outline(
            links = closedSpline.cyclicLinks.map { link ->
                Link.reconstruct(
                    splineLink = link,
                    edgeMetadata = edgeMetadata,
                )
            },
        )

        private fun closeVerges(
            verges: List<Verge>,
            closingEdgeMetadata: EdgeMetadata,
        ): Outline {
            val firstVerge = verges.first()
            val lastVerge = verges.last()

            val closingVerge = Verge.line(
                startAnchor = lastVerge.endAnchor,
                edgeMetadata = closingEdgeMetadata,
                endAnchor = firstVerge.startAnchor,
            )

            return connect(
                verges = verges + closingVerge,
            )
        }
    }

    init {
        require(links.size >= 2)
    }

    val verges: List<Verge>
        get() = links.withPreviousCyclic().map { (prevLink, link) ->
            link.bind(
                startAnchor = prevLink.endAnchor,
            )
        }

    val edgeCurves: List<PrimitiveCurve>
        get() = verges.map { it.curve }

    val innerSpline: ClosedSpline
        get() = ClosedSpline.positionallyContinuous(
            links = verges.map { verge ->
                verge.innerSplineLink
            },
        )

    fun findSeamContour(): ClosedSpline = ClosedSpline.fuse(
        edgeCurves = verges.map {
            it.seamOffsetCurve
        },
    )

    fun cut(
        lineSegment: LineSegment,
        cutEdgeMetadata: EdgeMetadata,
    ): Pair<Outline, Outline> {
        val intersectionCoords = findIntersectionCoords(lineSegment = lineSegment)

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
        val intersections = edgeCurve.findIntersections(other = lineSegment)

        if (intersections.isEmpty()) return@mapNotNull null

        val intersection = intersections.singleOrNull()
            ?: throw IllegalArgumentException("The given line segment intersects edge #$edgeIndex at multiple points")

        Coord(
            edgeIndex = edgeIndex,
            edgeCoord = intersection.coord,
        )
    }

    private fun splitAt(
        firstSplitCoord: Coord,
        secondSplitCoord: Coord,
        splitEdgeMetadata: EdgeMetadata,
    ): Pair<Outline, Outline> {
        val (firstEdgeIndex, firstEdgeCoord) = firstSplitCoord
        val (secondEdgeIndex, secondEdgeCoord) = secondSplitCoord

        val (frontVerges, centralVerges, backVerge) = verges.splitBefore(
            firstIndex = firstEdgeIndex,
            secondIndex = secondEdgeIndex,
        )

        val (firstCentralVerge, trailingCentralVerges) = centralVerges.uncons()!!
        val (firstBackVerge, trailingBackVerges) = backVerge.uncons()!!

        val (firstCentralVerge0, firstCentralVerge1) = firstCentralVerge.splitAt(edgeCoord = firstEdgeCoord)
        val (firstBackVerge0, firstBackVerge1) = firstBackVerge.splitAt(edgeCoord = secondEdgeCoord)

        val firstSubOutline = closeVerges(
            verges = listOf(firstCentralVerge1) + trailingCentralVerges + firstBackVerge0,
            closingEdgeMetadata = splitEdgeMetadata,
        )

        val secondSubOutline = closeVerges(
            verges = listOf(firstBackVerge1) + trailingBackVerges + frontVerges + firstCentralVerge0,
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
                transformation = Transformation.ReflectionOverLine(
                    line = reflectionLine,
                ),
            )
        }

        return connect(
            verges = remainingVerges + mirroredVerges,
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is Outline -> false
        !links.equalsWithTolerance(other.links, tolerance = tolerance) -> false
        else -> true
    }
}
