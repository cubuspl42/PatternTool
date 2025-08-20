package diy.lingerie.geometry_sandbox_tool

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.effect.MomentContext

class UserBezierCurve private constructor(
    val start: PropertyCell<Point>,
    val firstControl: PropertyCell<Point>,
    val secondControl: PropertyCell<Point>,
    val end: PropertyCell<Point>,
) : UserCurve<BezierCurve>() {
    companion object {
        context(momentContext: MomentContext) fun create(
            initialStart: Point,
            initialFirstControl: Point,
            initialSecondControl: Point,
            initialEnd: Point,
        ): UserBezierCurve = UserBezierCurve(
            start = PropertyCell.create(
                initialValue = initialStart,
            ),
            firstControl = PropertyCell.create(
                initialValue = initialFirstControl,
            ),
            secondControl = PropertyCell.create(
                initialValue = initialSecondControl,
            ),
            end = PropertyCell.create(
                initialValue = initialEnd,
            ),
        )
    }

    val reactiveBezierCurve: ReactiveBezierCurve
        get() = ReactiveBezierCurve(
            start = start,
            firstControl = firstControl,
            secondControl = secondControl,
            end = end,
        )

    override val reactiveCurve: ReactiveCurve<BezierCurve>
        get() = reactiveBezierCurve

    val bezierCurve: Cell<BezierCurve>
        get() = reactiveBezierCurve.bezierCurve
}