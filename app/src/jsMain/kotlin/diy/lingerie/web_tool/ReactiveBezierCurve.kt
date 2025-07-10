package diy.lingerie.web_tool

import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgPathElement
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.extra.svg.SVGPathSegment
import org.w3c.dom.svg.SVGPathElement

data class ReactiveBezierCurve(
    val start: Cell<Point>,
    val firstControl: Cell<Point>,
    val secondControl: Cell<Point>,
    val end: Cell<Point>,
) {
    companion object {
        fun diff(
            curveCell: Cell<BezierCurve>,
        ): ReactiveBezierCurve = ReactiveBezierCurve(
            start = curveCell.map { it.start }.calm(),
            firstControl = curveCell.map { it.firstControl }.calm(),
            secondControl = curveCell.map { it.secondControl }.calm(),
            end = curveCell.map { it.end }.calm(),
        )
    }

    val bezierCurve: Cell<BezierCurve> = Cell.map4(
        cell1 = start,
        cell2 = firstControl,
        cell3 = secondControl,
        cell4 = end,
    ) { startNow, firstControlNow, secondControlNow, endNow ->
        BezierCurve(
            start = startNow,
            firstControl = firstControlNow,
            secondControl = secondControlNow,
            end = endNow,
        )
    }

    fun createReactiveSvgPathElement(
        style: ReactiveStyle,
    ): SVGPathElement = document.createReactiveSvgPathElement(
        style = style,
        pathSegments = ReactiveList.fuse(
            start.map {
                SVGPathSegment(
                    type = "M",
                    values = it.toArray(),
                )
            },
            Cell.map3(
                cell1 = firstControl,
                cell2 = secondControl,
                cell3 = end,
            ) { firstControlNow, secondControlNow, lastControlNow ->
                SVGPathSegment(
                    type = "C",
                    values = firstControlNow.toArray() + secondControlNow.toArray() + lastControlNow.toArray(),
                )
            },
        ),
    )
}

private fun Point.toArray(): Array<Number> = arrayOf(x, y)