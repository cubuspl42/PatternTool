package diy.lingerie.pattern_tool

import diy.lingerie.geometry.Line
import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.geometry.curves.bezier.BezierCurve
import diy.lingerie.geometry.curves.bezier.PolyBezierCurve
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.iterable.shiftLeft
import diy.lingerie.utils.iterable.splitBefore
import diy.lingerie.utils.iterable.uncons
import diy.lingerie.utils.iterable.withNextCyclic
import kotlin.jvm.JvmInline

data class Outline(
    val links: List<Link>,
) {
    data class EdgeMetadata(
        val seamAllowance: SeamAllowance,
    )

    sealed class Joint {
        abstract fun transformBy(
            transformation: Transformation,
        ): Joint

        @JvmInline
        value class Anchor(
            val position: Point,
        ) {
            fun transformBy(
                transformation: Transformation,
            ): Anchor = Anchor(
                position = position.transformBy(transformation = transformation),
            )
        }

        @JvmInline
        value class Handle(
            val position: Point,
        ) {
            fun transformBy(
                transformation: Transformation,
            ): Handle = Handle(
                position = position.transformBy(transformation = transformation),
            )
        }

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
        }

        /**
         * A smooth joint
         */
        data class Smooth(
            override val rearHandle: Handle,
            /**
             * The coordinate of the anchor point on the control line segment
             */
            val anchorCoord: SegmentCurve.Coord,
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
        }

        abstract val rearHandle: Handle?

        abstract val anchor: Anchor

        abstract val frontHandle: Handle?
    }

    data class Edge(
        val startHandle: Joint.Handle?,
        val intermediateJoints: List<Joint.Smooth>,
        val endHandle: Joint.Handle?,
        val metadata: EdgeMetadata,
    ) {
        companion object {
            fun reconstruct(
                edgeCurve: BezierCurve,
                edgeMetadata: EdgeMetadata,
            ): Edge = Edge(
                startHandle = Joint.Handle(
                    position = edgeCurve.edge.firstControl,
                ),
                intermediateJoints = edgeCurve.joints.map {
                    Joint.Smooth.reconstruct(bezierJoint = it)
                },
                endHandle = Joint.Handle(
                    position = edgeCurve.edge.lastControl,
                ),
                metadata = edgeMetadata,
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

        fun isLine() = when {
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
        ): Edge {
            return Edge(
                startHandle = startHandle?.transformBy(transformation = transformation),
                intermediateJoints = intermediateJoints.map {
                    it.transformBy(transformation = transformation)
                },
                endHandle = endHandle?.transformBy(transformation = transformation),
                metadata = metadata,
            )
        }

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
    }

    data class Link(
        val startAnchor: Joint.Anchor,
        val edge: Edge,
    ) {
        fun bind(
            endAnchor: Joint.Anchor,
        ): Verge = Verge(
            startAnchor = startAnchor,
            edge = edge,
            endAnchor = endAnchor,
        )
    }

    data class Verge(
        val startAnchor: Joint.Anchor,
        val edge: Edge,
        val endAnchor: Joint.Anchor,
    ) {
        companion object {
            fun line(
                startAnchor: Joint.Anchor,
                edgeMetadata: EdgeMetadata,
                endAnchor: Joint.Anchor,
            ): Verge = Verge(
                startAnchor = startAnchor,
                edge = Edge.line(metadata = edgeMetadata),
                endAnchor = endAnchor,
            )

            fun reconstruct(
                edgeCurve: BezierCurve,
                edgeMetadata: EdgeMetadata,
            ): Verge = Verge(
                startAnchor = Joint.Anchor(
                    position = edgeCurve.start,
                ),
                edge = Edge.reconstruct(
                    edgeCurve = edgeCurve,
                    edgeMetadata = edgeMetadata,
                ),
                endAnchor = Joint.Anchor(
                    position = edgeCurve.end,
                ),
            )
        }

        val edgeCurve: BezierCurve
            get() = PolyBezierCurve(
                start = startAnchorPosition,
                firstControl = edge.startHandlePosition ?: startAnchorPosition,
                joints = edge.intermediateJoints.map {
                    it.toBezierJoint()
                },
                lastControl = edge.endHandlePosition ?: endAnchorPosition,
                end = endAnchorPosition,
            )

        val startAnchorPosition: Point
            get() = startAnchor.position

        val endAnchorPosition: Point
            get() = endAnchor.position

        fun reversed(): Verge = Verge(
            startAnchor = endAnchor,
            edge = edge.reversed(),
            endAnchor = startAnchor,
        )

        fun splitAt(
            edgeCoord: SegmentCurve.Coord,
        ): Pair<Verge, Verge> {
            val (firstSubCurve, secondSubCurve) = edgeCurve.splitAt(coord = edgeCoord)

            return Pair(
                Verge.reconstruct(
                    edgeCurve = firstSubCurve,
                    edgeMetadata = edge.metadata,
                ),
                Verge.reconstruct(
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
        val edgeCoord: SegmentCurve.Coord,
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
                    startAnchor = verge.startAnchor,
                    edge = verge.edge,
                )
            },
        )
    }

    init {
        require(links.size >= 2)
    }

    val verges: List<Verge>
        get() = links.withNextCyclic().map { (link, nextLink) ->
            link.bind(
                endAnchor = nextLink.startAnchor,
            )
        }

    val edgeCurves: List<SegmentCurve>
        get() = verges.map { it.edgeCurve }

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

        val (frontSegments, centralSegments, backSegments) = verges.splitBefore(
            firstIndex = firstEdgeIndex,
            secondIndex = secondEdgeIndex,
        )

        val (firstCentralSegment, trailingCentralSegments) = centralSegments.uncons()!!
        val (firstBackSegment, trailingBackSegments) = backSegments.uncons()!!

        val (firstCentralSegment0, firstCentralSegment1) = firstCentralSegment.splitAt(edgeCoord = firstEdgeCoord)
        val (firstBackSegment0, firstBackSegment1) = firstBackSegment.splitAt(edgeCoord = secondEdgeCoord)

        val firstSubOutline = closeSegments(
            verges = listOf(firstCentralSegment1) + trailingCentralSegments + firstBackSegment0,
            closingEdgeMetadata = splitEdgeMetadata,
        )

        val secondSubOutline = closeSegments(
            verges = listOf(firstBackSegment1) + trailingBackSegments + frontSegments + firstCentralSegment0,
            closingEdgeMetadata = splitEdgeMetadata,
        )

        return Pair(
            firstSubOutline,
            secondSubOutline,
        )
    }

    private fun closeSegments(
        verges: List<Verge>,
        closingEdgeMetadata: EdgeMetadata,
    ): Outline {
        val firstSegment = verges.first()
        val lastSegment = verges.last()

        val closingVerge = Verge.line(
            startAnchor = lastSegment.endAnchor,
            edgeMetadata = closingEdgeMetadata,
            endAnchor = firstSegment.startAnchor,
        )

        return Outline.connect(
            verges = verges + closingVerge,
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

        if (!removedVerge.edge.isLine()) {
            throw IllegalArgumentException("Cannot mirror over a non-line verge")
        }

        val mirrorLine = Line.throughPoints(
            removedVerge.startAnchorPosition,
            removedVerge.endAnchorPosition,
        ) ?: throw IllegalArgumentException("Cannot mirror over a zero-length line")

        val reversedVerges = remainingVerges.map {
            it.reversed()
        }

        val mirroredVerges = reversedVerges.map {
            it.transformBy(
                transformation = Transformation.ReflectionOverLine(
                    line = mirrorLine,
                ),
            )
        }

        return Outline.connect(
            remainingVerges + mirroredVerges,
        )
    }
}
