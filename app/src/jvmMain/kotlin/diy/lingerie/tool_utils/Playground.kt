package diy.lingerie.tool_utils

import dev.toolkt.geometry.BoundingBox
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Size
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.geometry.svg.toSvgPath
import dev.toolkt.math.algebra.sample
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.QuadraticBezierBinomial
import diy.lingerie.simple_dom.px
import diy.lingerie.simple_dom.svg.SvgCircle
import diy.lingerie.simple_dom.svg.SvgGraphicsElements
import diy.lingerie.simple_dom.svg.SvgGroup
import diy.lingerie.simple_dom.svg.SvgLine
import diy.lingerie.simple_dom.svg.SvgMarker
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.simple_dom.svg.SvgShape
import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.dom.pure.PureColor
import java.nio.file.Files
import java.nio.file.Path

data class Playground(
    val items: List<Item>,
) {
    sealed class Item {
        abstract val color: PureColor

        abstract fun toSvgElement(): SvgGraphicsElements

        abstract fun findBoundingBox(): BoundingBox
    }

    sealed class OpenCurveItem : Item() {
        final override fun findBoundingBox(): BoundingBox = openCurve.findBoundingBox()

        abstract val openCurve: OpenCurve
    }

    data class BezierCurveItem(
        override val color: PureColor = PureColor.Companion.red,
        val bezierCurve: BezierCurve,
    ) : OpenCurveItem() {
        companion object {
            private const val extendedCurveSampleCount = 1024
            private val extendedCurveSampleRange = (-2.0)..(2.0)
        }

        override val openCurve: OpenCurve
            get() = bezierCurve

        override fun toSvgElement(): SvgGraphicsElements = SvgGroup(
            children = listOfNotNull(
                toSvgControlRubber(
                    anchor = bezierCurve.start,
                    handle = bezierCurve.firstControl,
                ),
                toSvgControlRubber(
                    anchor = bezierCurve.end,
                    handle = bezierCurve.secondControl,
                ),
//                toExtendedSvgPath(),
                toPrimarySvgPath(),
            ),
        )

        private fun toPrimarySvgPath(): SvgPath = bezierCurve.toSvgPath(
            stroke = SvgShape.Stroke(
                color = color,
                width = 0.5,
            ),
        ).copy(
            markerEndId = triangleMarkerId,
        )

        private fun toExtendedSvgPath(): SvgPath? {
            val samples = bezierCurve.basisFunction.sample(
                linSpace = LinSpace(
                    range = extendedCurveSampleRange,
                    sampleCount = extendedCurveSampleCount,
                ),
            )

            val points = samples.map { Point(pointVector = it.b) }

            return SvgPath.Companion.polyline(
                stroke = SvgShape.Stroke(
                    color = PureColor.Companion.lightGray,
                    width = 0.25,
                ),
                points = points,
            )
        }

        private fun toSvgControlRubber(
            anchor: Point,
            handle: Point,
        ): SvgGroup = SvgGroup(
            children = listOf(
                toSvgControlLine(
                    start = anchor, end = handle
                ),
                toSvgHandle(
                    point = handle,
                ),
            )
        )

        private fun toSvgHandle(
            point: Point,
        ) = SvgCircle(
            center = point,
            radius = 1.0,
            stroke = null,
            fill = SvgShape.Fill.Specified(
                color = PureColor.Companion.lightGray,
            ),
        )

        private fun toSvgControlLine(
            start: Point,
            end: Point,
        ) = SvgLine(
            start = start,
            end = end,
            stroke = SvgShape.Stroke(
                color = PureColor.Companion.lightGray,
                width = 1.0,
            ),
        )
    }

    data class QuadraticBezierBinomialItem(
        override val color: PureColor = PureColor.Companion.red,
        val quadraticBezierBinomial: QuadraticBezierBinomial,
    ) : Item() {
        companion object {
            private const val extendedCurveSampleCount = 1024
            private val extendedCurveSampleRange = (-2.0)..(2.0)
        }

        override fun toSvgElement(): SvgGraphicsElements = SvgGroup(
            children = listOfNotNull(
                toSvgControlShape(),
                toPrimarySvgPath(),
            ),
        )

        override fun findBoundingBox(): BoundingBox {
            TODO("Not yet implemented")
        }

        private fun toPrimarySvgPath(): SvgPath = quadraticBezierBinomial.toSvgPath(
            stroke = SvgShape.Stroke(
                color = color,
                width = 0.4,
            ),
        ).copy(
            markerEndId = triangleMarkerId,
        )

        private fun toSvgControlShape(): SvgGroup = SvgGroup(
            children = listOf(
                toSvgControlLine(
                    start = Point(pointVector = quadraticBezierBinomial.point0),
                    end = Point(pointVector = quadraticBezierBinomial.point1),
                ),
                toSvgControlLine(
                    start = Point(pointVector = quadraticBezierBinomial.point1),
                    end = Point(pointVector = quadraticBezierBinomial.point2),
                ),
            ),
        )

        private fun toSvgControlLine(
            start: Point,
            end: Point,
        ) = SvgLine(
            start = start,
            end = end,
            stroke = SvgShape.Stroke(
                color = PureColor.Companion.lightGray,
                width = 0.25,
            ),
        )
    }

    data class LineSegmentItem(
        override val color: PureColor = PureColor.Companion.red,
        val lineSegment: LineSegment,
    ) : OpenCurveItem() {
        companion object {
            private const val extendedCurveSampleCount = 1024
            private val extendedCurveSampleRange = (-2.0)..(2.0)
        }

        override val openCurve: OpenCurve
            get() = lineSegment

        override fun toSvgElement(): SvgGraphicsElements = SvgLine(
            start = lineSegment.start,
            end = lineSegment.end,
            stroke = SvgShape.Stroke(
                color = color,
                width = 0.5,
            ),
        ).copy(
            markerEndId = triangleMarkerId,
        )
    }

    data class PointItem(
        override val color: PureColor = PureColor.Companion.black,
        val point: Point,
    ) : Item() {
        override fun toSvgElement(): SvgCircle = SvgCircle(
            center = point,
            radius = 1.0,
            stroke = null,
            fill = SvgShape.Fill.Specified(
                color = color,
            ),
        )

        override fun findBoundingBox(): BoundingBox = BoundingBox.Companion.of(
            pointA = point,
            pointB = point,
        )
    }

    companion object {
        internal const val triangleMarkerId = "triangle"

        private const val triangleMarkerSize = 4.0

        private val triangleMarker = SvgMarker(
            id = triangleMarkerId,
            size = Size(
                width = triangleMarkerSize,
                height = triangleMarkerSize,
            ),
            ref = Point(
                x = triangleMarkerSize / 2,
                y = triangleMarkerSize / 2,
            ),
            path = SvgPath(
                stroke = null,
                fill = null,
                segments = listOf(
                    SvgPath.Segment.MoveTo(
                        targetPoint = Point.Companion.origin,
                    ),
                    SvgPath.Segment.LineTo(
                        finalPoint = Point(
                            x = triangleMarkerSize,
                            y = triangleMarkerSize / 2,
                        ),
                    ),
                    SvgPath.Segment.LineTo(
                        finalPoint = Point(
                            x = 0.0,
                            y = triangleMarkerSize,
                        ),
                    ),
                    SvgPath.Segment.ClosePath,
                ),
            ),
        )
    }

    fun toSvgRoot(): SvgRoot {
        val viewBox = SvgRoot.ViewBox(
            x = 0.0,
            y = 0.0,
            width = 1024.0,
            height = 768.0,
        )

        val elements = items.map { it.toSvgElement() }

        return SvgRoot(
            viewBox = viewBox,
            defs = listOf(
                triangleMarker,
            ),
            width = viewBox.width.px,
            height = viewBox.height.px,
            graphicsElements = elements,
        )
    }

    private fun findTrueBoundingBox(): BoundingBox = BoundingBox.Companion.unionAll(
        items.map { it.findBoundingBox() },
    )

    fun writeToFile(
        filePath: Path,
    ) {
        toSvgRoot().writeToFile(filePath = filePath)
    }

    fun writeToTmpFile(
        fileBaseName: String,
    ) {
        val tempFilePath = Files.createTempFile(fileBaseName, ".svg")
        writeToFile(filePath = tempFilePath)
        println(tempFilePath)
    }
}