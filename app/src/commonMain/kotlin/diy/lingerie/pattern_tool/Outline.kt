package diy.lingerie.pattern_tool

import diy.lingerie.geometry.Line
import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.iterable.mapCarrying
import diy.lingerie.utils.iterable.shiftLeft
import diy.lingerie.utils.iterable.splitBefore
import diy.lingerie.utils.iterable.uncons
import diy.lingerie.utils.iterable.withNextCyclic

data class Outline(
    val links: List<Link>,
) {
    data class EdgeMetadata(
        val seamAllowance: SeamAllowance,
    )

    sealed class Joint {
        data class ControlSegment(
            val anchorPosition: Point,
            val handlePosition: Point,
        )

        /**
         * An unconstrained (typically sharp) joint
         */
        data class Free(
            override val rearHandlePosition: Point,
            override val anchorPosition: Point,
            override val frontHandlePosition: Point
        ) : Joint()

        /**
         * A smooth joint
         */
        data class Smooth(
            /**
             * The control line segment going from the rear to the front handle
             */
            val controlLineSegment: LineSegment,
            /**
             * The coordinate of the anchor point on the control line segment
             */
            val anchorCoord: SegmentCurve.Coord,
        ) : Joint() {
            override val anchorPosition: Point
                get() = controlLineSegment.evaluate(coord = anchorCoord)

            override val rearHandlePosition: Point
                get() = controlLineSegment.start

            override val frontHandlePosition: Point
                get() = controlLineSegment.end
        }

        val rearControlSegment: ControlSegment
            get() = ControlSegment(
                anchorPosition = anchorPosition,
                handlePosition = rearHandlePosition,
            )

        val frontControlSegment: ControlSegment
            get() = ControlSegment(
                anchorPosition = anchorPosition,
                handlePosition = frontHandlePosition,
            )


        abstract val rearHandlePosition: Point

        abstract val anchorPosition: Point

        abstract val frontHandlePosition: Point
    }

    data class Edge(
        val curveEdge: SegmentCurve.Edge,
        val metadata: EdgeMetadata,
    ) {
        val seamAllowance: SeamAllowance
            get() = metadata.seamAllowance
    }

    data class Link(
        val start: Point,
        val edge: Edge,
    ) {
        fun bind(
            end: Point,
        ): Segment = Segment(
            start = start,
            edge = edge,
            end = end,
        )

        fun transformBy(
            transformation: Transformation,
        ): Link = Link(
            start = start.transformBy(transformation = transformation),
            edge = edge.copy(
                curveEdge = edge.curveEdge.transformBy(
                    transformation = transformation,
                ),
            ),
        )
    }

    data class Segment(
        val start: Point,
        val edge: Edge,
        val end: Point,
    ) {
        companion object {
            fun construct(
                edgeCurve: SegmentCurve,
                edgeMetadata: EdgeMetadata,
            ): Segment = Segment(
                start = edgeCurve.start,
                edge = Edge(
                    curveEdge = edgeCurve.edge,
                    metadata = edgeMetadata,
                ),
                end = edgeCurve.end,
            )
        }

        val edgeCurve: SegmentCurve
            get() = edge.curveEdge.bind(
                start = start,
                end = end,
            )

        fun splitAt(
            edgeCoord: SegmentCurve.Coord,
        ): Pair<Segment, Segment> {
            val (firstSubCurve, secondSubCurve) = edgeCurve.splitAt(coord = edgeCoord)

            return Pair(
                Segment.construct(
                    edgeCurve = firstSubCurve,
                    edgeMetadata = edge.metadata,
                ),
                Segment.construct(
                    edgeCurve = secondSubCurve,
                    edgeMetadata = edge.metadata,
                ),
            )
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
        val edgeCoord: SegmentCurve.Coord,
    )

    companion object {
        fun connect(
            segments: List<Segment>,
        ): Outline = Outline(
            links = segments.withNextCyclic().mapIndexed { index, (segment, nextSegment) ->
                if (segment.end != nextSegment.start) {
                    throw IllegalArgumentException("A segment #$index does not connect to the next segment")
                }

                Link(
                    start = segment.start,
                    edge = segment.edge,
                )
            },
        )

        fun reverse(
            links: List<Link>,
            end: Point,
        ): List<Link> {
            val (reversedLinks, _) = links.reversed().mapCarrying(
                initialCarry = end,
            ) { joint, link ->
                Pair(
                    Link(
                        start = joint,
                        edge = link.edge,
                    ),
                    link.start,
                )
            }

            return reversedLinks
        }
    }

    init {
        require(links.size >= 2)
    }

    val segments: List<Segment>
        get() = links.withNextCyclic().map { (link, nextLink) ->
            Segment(
                start = link.start,
                edge = link.edge,
                end = nextLink.start,
            )
        }

    val edgeCurves: List<SegmentCurve>
        get() = segments.map { it.edgeCurve }

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

        val (frontSegments, centralSegments, backSegments) = segments.splitBefore(
            firstIndex = firstEdgeIndex,
            secondIndex = secondEdgeIndex,
        )

        val (firstCentralSegment, trailingCentralSegments) = centralSegments.uncons()!!
        val (firstBackSegment, trailingBackSegments) = backSegments.uncons()!!

        val (firstCentralSegment0, firstCentralSegment1) = firstCentralSegment.splitAt(edgeCoord = firstEdgeCoord)
        val (firstBackSegment0, firstBackSegment1) = firstBackSegment.splitAt(edgeCoord = secondEdgeCoord)

        val firstSubOutline = closeSegments(
            segments = listOf(firstCentralSegment1) + trailingCentralSegments + firstBackSegment0,
            closingEdgeMetadata = splitEdgeMetadata,
        )

        val secondSubOutline = closeSegments(
            segments = listOf(firstBackSegment1) + trailingBackSegments + frontSegments + firstCentralSegment0,
            closingEdgeMetadata = splitEdgeMetadata,
        )

        return Pair(
            firstSubOutline,
            secondSubOutline,
        )
    }

    private fun closeSegments(
        segments: List<Segment>,
        closingEdgeMetadata: EdgeMetadata,
    ): Outline {
        val firstSegment = segments.first()
        val lastSegment = segments.last()

        val closingSegment = Segment.construct(
            edgeCurve = LineSegment(
                start = lastSegment.end,
                end = firstSegment.start,
            ),
            edgeMetadata = closingEdgeMetadata,
        )

        return Outline.connect(
            segments = segments + closingSegment,
        )
    }

    /**
     * Mirrors the outline against the edge at [edgeIndex].
     */
    fun mirror(
        edgeIndex: Int,
    ): Outline {
        val shiftedLinks = links.shiftLeft(edgeIndex)

        val (removedLink, remainingLinks) = shiftedLinks.uncons()!!

        if (removedLink.edge.curveEdge !is LineSegment.Edge) {
            throw IllegalArgumentException("Cannot mirror against a non-line edge")
        }

        val mirrorPoint0 = removedLink.start
        val mirrorPoint1 = remainingLinks.first().start
        val mirrorLine = Line.throughPoints(mirrorPoint0, mirrorPoint1)
            ?: throw IllegalArgumentException("Cannot mirror against a zero-length line")

        val reversedLinks = reverse(
            links = remainingLinks,
            end = removedLink.start,
        )

        val mirroredLinks = reversedLinks.map {
            it.transformBy(
                transformation = Transformation.ReflectionOverLine(
                    line = mirrorLine,
                ),
            )
        }

        return Outline(
            links = remainingLinks + mirroredLinks,
        )
    }
}
