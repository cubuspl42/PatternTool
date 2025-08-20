package diy.lingerie.geometry_sandbox_tool

import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.effect.MomentContext

class UserLineSegment private constructor(
    val start: PropertyCell<Point>,
    val end: PropertyCell<Point>,
) : UserCurve<LineSegment>() {
    companion object {
        context(momentContext: MomentContext) fun create(
            initialStart: Point,
            initialEnd: Point,
        ): UserLineSegment = UserLineSegment(
            start = PropertyCell.create(
                initialValue = initialStart,
            ),
            end = PropertyCell.create(
                initialValue = initialEnd,
            ),
        )
    }

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
