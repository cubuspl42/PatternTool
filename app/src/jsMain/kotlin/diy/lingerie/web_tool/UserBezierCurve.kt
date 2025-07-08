package diy.lingerie.web_tool

import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.PropertyCell

data class UserBezierCurve(
    val start: PropertyCell<Point>,
    val firstControl: PropertyCell<Point>,
    val secondControl: PropertyCell<Point>,
    val end: PropertyCell<Point>,
) {
    val reactiveBezierCurve: ReactiveBezierCurve
        get() = ReactiveBezierCurve(
            start = start,
            firstControl = firstControl,
            secondControl = secondControl,
            end = end,
        )
}
