package diy.lingerie.geometry_sandbox_tool

import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgLineElement
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.effect.MomentContext
import kotlinx.browser.document
import org.w3c.dom.svg.SVGLineElement

data class ReactiveLineSegment(
    val start: Cell<Point>,
    val end: Cell<Point>,
) : ReactiveCurve<LineSegment>() {
    companion object {
        fun diff(
            lineSegmentCell: Cell<LineSegment>,
        ): ReactiveLineSegment = ReactiveLineSegment(
            start = lineSegmentCell.map { it.start }.calm(),
            end = lineSegmentCell.map { it.end }.calm(),
        )
    }

    val lineSegment: Cell<LineSegment> = Cell.Companion.map2(
        cell1 = start,
        cell2 = end,
    ) { startNow, endNow ->
        LineSegment(
            start = startNow,
            end = endNow,
        )
    }

    override val primitiveCurve: Cell<LineSegment>
        get() = lineSegment

    context(momentContext: MomentContext) fun createReactiveSvgLineElement(
        style: ReactiveStyle,
    ): SVGLineElement = document.createReactiveSvgLineElement(
        style = style,
        start = start,
        end = end,
    )
}
