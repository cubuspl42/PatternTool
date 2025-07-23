package diy.lingerie.web_tool

import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.geometry.curves.PrimitiveCurve
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList

data class UserCurveSystem(
    val userCurve1: UserCurve<*>,
    val userCurve2: UserCurve<*>,
) {
    data class IntersectionInfo(
        val intersections: Set<OpenCurve.Intersection>,
        val intersectionPolynomial1: Polynomial,
        val intersectionPolynomial2: Polynomial,
    )

    val intersectionInfo: Cell<IntersectionInfo> = Cell.map2(
        cell1 = userCurve1.primitiveCurve,
        cell2 = userCurve2.primitiveCurve,
    ) { curve1, curve2 ->
        val intersections = PrimitiveCurve.findIntersectionsByEquationSolving(
            simpleSubjectCurve = curve1,
            complexObjectCurve = curve2,
            tolerance = NumericTolerance.Absolute.Default,
        )

        val intersectionPolynomial1 = curve1.basisFunction.findIntersectionPolynomial(
            other = curve2.basisFunction,
        )

        val intersectionPolynomial2 = curve2.basisFunction.findIntersectionPolynomial(
            other = curve1.basisFunction,
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
