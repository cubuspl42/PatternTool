package diy.lingerie.web_tool

import dev.toolkt.geometry.SpatialObject
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList

data class UserCurveSystem(
    val userBezierCurve1: UserBezierCurve,
    val userBezierCurve2: UserBezierCurve,
) {
    val intersections = ReactiveList.Companion.diff(
        Cell.Companion.map2(
            cell1 = userBezierCurve1.bezierCurve,
            cell2 = userBezierCurve2.bezierCurve,
        ) { bezierCurve1, bezierCurve2 ->
            BezierCurve.Companion.findIntersections(
                subjectBezierCurve = bezierCurve1,
                objectBezierCurve = bezierCurve2,
                tolerance = SpatialObject.SpatialTolerance.default
            ).toList()
        },
    )
}
