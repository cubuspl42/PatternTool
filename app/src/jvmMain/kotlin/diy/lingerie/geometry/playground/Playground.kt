package diy.lingerie.geometry.playground

import diy.lingerie.geometry.BoundingBox
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.Size
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.BezierCurve
import diy.lingerie.geometry.svg.toSvgPath
import diy.lingerie.math.algebra.sample
import diy.lingerie.simple_dom.SimpleColor
import diy.lingerie.simple_dom.px
import diy.lingerie.simple_dom.svg.SvgCircle
import diy.lingerie.simple_dom.svg.SvgGraphicsElements
import diy.lingerie.simple_dom.svg.SvgGroup
import diy.lingerie.simple_dom.svg.SvgLine
import diy.lingerie.simple_dom.svg.SvgMarker
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.simple_dom.svg.SvgShape
import diy.lingerie.utils.iterable.LinSpace
import java.nio.file.Files
import java.nio.file.Path

data class Playground(
    val items: List<Item>,
) {
    sealed class Item {
        abstract val color: SimpleColor

        abstract fun toSvgElement(): SvgGraphicsElements

        abstract fun findBoundingBox(): BoundingBox
    }

    sealed class OpenCurveItem : Item() {
        final override fun findBoundingBox(): BoundingBox = openCurve.findBoundingBox()

        abstract val openCurve: OpenCurve
    }

    data class BezierCurveItem(
        override val color: SimpleColor = SimpleColor.red,
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
                toExtendedSvgPath(),
                toPrimarySvgPath(),
            ),
        )

        private fun toPrimarySvgPath(): SvgPath = bezierCurve.toSvgPath(
            stroke = SvgShape.Stroke(
                color = color,
                width = 0.5,
            ),
        ).copy(
            markerEndId = Playground.triangleMarkerId,
        )

        private fun toExtendedSvgPath(): SvgPath? {
            val samples = bezierCurve.basisFunction.sample(
                linSpace = LinSpace(
                    range = extendedCurveSampleRange,
                    n = extendedCurveSampleCount,
                ),
            )

            val points = samples.map { Point(pointVector = it.b) }

            return SvgPath.polyline(
                stroke = SvgShape.Stroke(
                    color = SimpleColor.lightGray,
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
                color = SimpleColor.lightGray,
            ),
        )

        private fun toSvgControlLine(
            start: Point,
            end: Point,
        ) = SvgLine(
            start = start,
            end = end,
            stroke = SvgShape.Stroke(
                color = SimpleColor.lightGray,
                width = 1.0,
            ),
        )
    }

    data class PointItem(
        override val color: SimpleColor = SimpleColor.black,
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

        override fun findBoundingBox(): BoundingBox = BoundingBox.of(
            pointA = point,
            pointB = point,
        )
    }

    companion object {
        internal const val triangleMarkerId = "triangle"

        private val triangleMarkerSize = 6.0

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
                        targetPoint = Point.origin,
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

    private fun findTrueBoundingBox(): BoundingBox = BoundingBox.unionAll(
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
