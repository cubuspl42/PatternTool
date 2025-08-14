package diy.lingerie.geometry_sandbox_tool

import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell

data class UserLineSegment(
    val start: PropertyCell<Point>,
    val end: PropertyCell<Point>,
) : UserCurve<LineSegment>() {
    val reactiveLineSegment: ReactiveLineSegment
        get() = ReactiveLineSegment(
            start = start,
            end = end,
        )

    override val reactiveCurve: ReactiveCurve<LineSegment>
        get() = reactiveLineSegment

    val lineSegment: Cell<LineSegment>
        get() = reactiveLineSegment.lineSegment
}
