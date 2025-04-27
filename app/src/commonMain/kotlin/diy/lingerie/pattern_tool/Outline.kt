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
    data class Edge(
        val curveEdge: SegmentCurve.Edge,
        val seamAllowance: SeamAllowance,
    )

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
            fun of(
                segmentCurve: SegmentCurve,
                seamAllowance: SeamAllowance,
            ): Segment = Segment(
                start = segmentCurve.start,
                edge = Edge(
                    curveEdge = segmentCurve.edge,
                    seamAllowance = seamAllowance,
                ),
                end = segmentCurve.end,
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
                Segment.of(
                    segmentCurve = firstSubCurve,
                    seamAllowance = edge.seamAllowance,
                ),
                Segment.of(
                    segmentCurve = secondSubCurve,
                    seamAllowance = edge.seamAllowance,
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
        cutSeamAllowance: SeamAllowance,
    ): Pair<Outline, Outline> {
        val intersectionCoords = findIntersectionCoords(lineSegment = lineSegment)

        if (intersectionCoords.size != 2) {
            throw IllegalArgumentException("The given line segment does not intersect the outline at exactly two points")
        }

        val (firstIntersectionCoord, secondIntersectionCoord) = intersectionCoords

        return splitAt(
            firstSplitCoord = firstIntersectionCoord,
            secondSplitCoord = secondIntersectionCoord,
            splitSeamAllowance = cutSeamAllowance,
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
        splitSeamAllowance: SeamAllowance,
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
            closingSeamAllowance = splitSeamAllowance,
        )

        val secondSubOutline = closeSegments(
            segments = listOf(firstBackSegment1) + trailingBackSegments + frontSegments + firstCentralSegment0,
            closingSeamAllowance = splitSeamAllowance,
        )

        return Pair(
            firstSubOutline,
            secondSubOutline,
        )
    }

    private fun closeSegments(
        segments: List<Segment>,
        closingSeamAllowance: SeamAllowance,
    ): Outline {
        val firstSegment = segments.first()
        val lastSegment = segments.last()

        val closingSegment = Segment.of(
            segmentCurve = LineSegment(
                start = lastSegment.end,
                end = firstSegment.start,
            ),
            seamAllowance = closingSeamAllowance,
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
