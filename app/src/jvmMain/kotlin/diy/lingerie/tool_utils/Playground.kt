package diy.lingerie.tool_utils


import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.svg.PureSvgCircle
import dev.toolkt.dom.pure.svg.PureSvgGraphicsElement
import dev.toolkt.dom.pure.svg.PureSvgGroup
import dev.toolkt.dom.pure.svg.PureSvgLine
import dev.toolkt.dom.pure.svg.PureSvgMarker
import dev.toolkt.dom.pure.svg.PureSvgPath
import dev.toolkt.dom.pure.svg.PureSvgRoot
import dev.toolkt.dom.pure.svg.PureSvgShape
import dev.toolkt.geometry.Rectangle
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Size
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.QuadraticBezierBinomial
import dev.toolkt.math.algebra.sample
import diy.lingerie.geometry.svg_utils.toSvgPath
import java.nio.file.Files
import java.nio.file.Path

data class Playground(
    val items: List<Item>,
) {
    sealed class Item {
        abstract val color: PureColor

        abstract fun toSvgElement(): PureSvgGraphicsElement

        abstract fun findBoundingBox(): Rectangle
    }

    sealed class OpenCurveItem : Item() {
        final override fun findBoundingBox(): Rectangle = openCurve.findBoundingBox()

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

        override fun toSvgElement(): PureSvgGraphicsElement = PureSvgGroup(
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

        private fun toPrimarySvgPath(): PureSvgPath = bezierCurve.toSvgPath(
            stroke = PureSvgShape.Stroke(
                color = color,
                width = 0.5,
            ),
        ).copy(
            markerEndId = triangleMarkerId,
        )

        private fun toExtendedSvgPath(): PureSvgPath? {
            val samples = bezierCurve.basisFunction.sample(
                linSpace = LinSpace(
                    range = extendedCurveSampleRange,
                    sampleCount = extendedCurveSampleCount,
                ),
            )

            val points = samples.map { Point(pointVector = it.b) }

            return PureSvgPath.Companion.polyline(
                stroke = PureSvgShape.Stroke(
                    color = PureColor.Companion.lightGray,
                    width = 0.25,
                ),
                points = points,
            )
        }

        private fun toSvgControlRubber(
            anchor: Point,
            handle: Point,
        ): PureSvgGroup = PureSvgGroup(
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
        ) = PureSvgCircle(
            center = point,
            radius = 1.0,
            stroke = null,
            fill = PureSvgShape.Fill.Specified(
                color = PureColor.Companion.lightGray,
            ),
        )

        private fun toSvgControlLine(
            start: Point,
            end: Point,
        ) = PureSvgLine(
            start = start,
            end = end,
            stroke = PureSvgShape.Stroke(
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

        override fun toSvgElement(): PureSvgGraphicsElement = PureSvgGroup(
            children = listOfNotNull(
                toSvgControlShape(),
                toPrimarySvgPath(),
            ),
        )

        override fun findBoundingBox(): Rectangle {
            TODO("Not yet implemented")
        }

        private fun toPrimarySvgPath(): PureSvgPath = quadraticBezierBinomial.toSvgPath(
            stroke = PureSvgShape.Stroke(
                color = color,
                width = 0.4,
            ),
        ).copy(
            markerEndId = triangleMarkerId,
        )

        private fun toSvgControlShape(): PureSvgGroup = PureSvgGroup(
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
        ) = PureSvgLine(
            start = start,
            end = end,
            stroke = PureSvgShape.Stroke(
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

        override fun toSvgElement(): PureSvgGraphicsElement = PureSvgLine(
            start = lineSegment.start,
            end = lineSegment.end,
            stroke = PureSvgShape.Stroke(
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
        override fun toSvgElement(): PureSvgCircle = PureSvgCircle(
            center = point,
            radius = 1.0,
            stroke = null,
            fill = PureSvgShape.Fill.Specified(
                color = color,
            ),
        )

        override fun findBoundingBox(): Rectangle = Rectangle.Companion.of(
            pointA = point,
            pointB = point,
        )
    }

    companion object {
        internal const val triangleMarkerId = "triangle"

        private const val triangleMarkerSize = 4.0

        private val triangleMarker = PureSvgMarker(
            id = triangleMarkerId,
            size = Size(
                width = triangleMarkerSize,
                height = triangleMarkerSize,
            ),
            ref = Point(
                x = triangleMarkerSize / 2,
                y = triangleMarkerSize / 2,
            ),
            path = PureSvgPath(
                stroke = null,
                fill = null,
                segments = listOf(
                    PureSvgPath.Segment.MoveTo(
                        targetPoint = Point.Companion.origin,
                    ),
                    PureSvgPath.Segment.LineTo(
                        finalPoint = Point(
                            x = triangleMarkerSize,
                            y = triangleMarkerSize / 2,
                        ),
                    ),
                    PureSvgPath.Segment.LineTo(
                        finalPoint = Point(
                            x = 0.0,
                            y = triangleMarkerSize,
                        ),
                    ),
                    PureSvgPath.Segment.ClosePath,
                ),
            ),
        )
    }

    fun toSvgRoot(): PureSvgRoot {
        val viewBox = PureSvgRoot.ViewBox(
            x = 0.0,
            y = 0.0,
            width = 1024.0,
            height = 768.0,
        )

        val elements = items.map { it.toSvgElement() }

        return PureSvgRoot(
            viewBox = viewBox,
            defs = listOf(
                triangleMarker,
            ),
            width = viewBox.width.px,
            height = viewBox.height.px,
            graphicsElements = elements,
        )
    }

    private fun findTrueBoundingBox(): Rectangle = Rectangle.Companion.unionAll(
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