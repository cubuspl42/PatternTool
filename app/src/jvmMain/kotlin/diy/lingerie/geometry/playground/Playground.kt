package diy.lingerie.geometry.playground

import diy.lingerie.geometry.BoundingBox
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.bezier.BezierCurve
import diy.lingerie.geometry.toSvgPath
import diy.lingerie.simple_dom.SimpleColor
import diy.lingerie.simple_dom.mm
import diy.lingerie.simple_dom.svg.SvgCircle
import diy.lingerie.simple_dom.svg.SvgElement
import diy.lingerie.simple_dom.svg.SvgGroup
import diy.lingerie.simple_dom.svg.SvgLine
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.simple_dom.svg.SvgShape
import java.nio.file.Files
import java.nio.file.Path

data class Playground(
    val items: List<Item>,
) {
    sealed class Item {
        abstract fun toSvgElement(): SvgElement

        abstract fun findBoundingBox(): BoundingBox
    }

    sealed class OpenCurveItem : Item() {
        final override fun findBoundingBox(): BoundingBox = openCurve.findBoundingBox()

        abstract val openCurve: OpenCurve
    }

    data class BezierCurveItem(
        val bezierCurve: BezierCurve,
    ) : OpenCurveItem() {
        override val openCurve: OpenCurve
            get() = bezierCurve

        override fun toSvgElement(): SvgElement = SvgGroup(
            children = listOf(
                bezierCurve.toSvgPath(
                    stroke = SvgShape.Stroke(
                        color = SimpleColor.red,
                        width = 0.5,
                    ),
                ),
                toSvgControlRubber(
                    anchor = bezierCurve.start,
                    handle = bezierCurve.firstControl,
                ),
                toSvgControlRubber(
                    anchor = bezierCurve.end,
                    handle = bezierCurve.secondControl,
                ),
            ),
        )

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
            fill = SvgShape.Fill(
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
        val point: Point,
    ) : Item() {
        override fun toSvgElement(): SvgElement = SvgCircle(
            center = point,
            radius = 1.0,
            stroke = null,
            fill = SvgShape.Fill(
                color = SimpleColor.black,
            ),
        )

        override fun findBoundingBox(): BoundingBox = BoundingBox.of(
            pointA = point,
            pointB = point,
        )
    }

    fun toSvgRoot(): SvgRoot {
        val boundingBox = BoundingBox.unionAll(
            items.map { it.findBoundingBox() },
        ).expand(
            bleed = 128.0,
        )

        val viewBox = SvgRoot.ViewBox(
            x = boundingBox.xMin,
            y = boundingBox.yMin,
            width = boundingBox.width,
            height = boundingBox.height,
        )

        return SvgRoot(
            viewBox = viewBox,
            width = boundingBox.width.mm,
            height = boundingBox.height.mm,
            children = items.map { it.toSvgElement() },
        )
    }

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
