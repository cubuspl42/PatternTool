package diy.lingerie.web_tool

import dev.toolkt.geometry.SpatialObject
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList

data class UserCurveSystem(
    val userBezierCurve1: UserBezierCurve,
    val userBezierCurve2: UserBezierCurve,
) {
    data class IntersectionInfo(
        val intersections: Set<OpenCurve.Intersection>,
        val intersectionPolynomial1: Polynomial,
        val intersectionPolynomial2: Polynomial,
    )

    val intersectionInfo: Cell<IntersectionInfo> = Cell.map2(
        cell1 = userBezierCurve1.bezierCurve,
        cell2 = userBezierCurve2.bezierCurve,
    ) { bezierCurve1, bezierCurve2 ->
        val intersections = BezierCurve.findIntersectionsByEquationSolving(
            subjectBezierCurve = bezierCurve1,
            objectBezierCurve = bezierCurve2,
        )

        val intersectionPolynomial1 = bezierCurve1.basisFunction.findIntersectionPolynomial(
            other = bezierCurve2.basisFunction,
        )

        val intersectionPolynomial2 = bezierCurve2.basisFunction.findIntersectionPolynomial(
            other = bezierCurve1.basisFunction,
        )

        IntersectionInfo(
            intersections = intersections,
            intersectionPolynomial1 = intersectionPolynomial1,
            intersectionPolynomial2 = intersectionPolynomial2,
        )
    }

    val intersections: ReactiveList<OpenCurve.Intersection> = ReactiveList.diff(
        intersectionInfo.map { it.intersections.toList() },
    )
}
