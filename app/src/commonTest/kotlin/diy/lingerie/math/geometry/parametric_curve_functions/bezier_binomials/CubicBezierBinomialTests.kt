package diy.lingerie.math.geometry.parametric_curve_functions.bezier_binomials

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.bezier.BezierCurve
import diy.lingerie.math.algebra.map
import diy.lingerie.math.algebra.sample
import diy.lingerie.utils.iterable.LinSpace
import kotlin.test.Test

class CubicBezierBinomialTests {
    @Test
    fun testLocatePoint() {
        val bezierCurve = BezierCurve(
            start = Point(401.6960144042969, 102.31400299072266),
            firstControl = Point(525.4130249023438, 763.280029296875),
            secondControl = Point(471.4840087890625, -143.9980010986328),
            end = Point(598.4459838867188, 398.2959899902344),
        )

        bezierCurve.startTangent

        val cubicBezierBinomial = bezierCurve.basisFunction as CubicBezierBinomial

        val derivative = cubicBezierBinomial.findDerivative()

        val distance = 10.0

        val points = derivative.map { foo ->
            foo.normalize() * distance
        }.sample(
            linSpace = LinSpace(n = 10),
        )

        println(points)
    }

    @Test
    fun testProjectPoint() {

    }

    @Test
    fun testSolveIntersections_cubicCurve() {

    }

    @Test
    fun testSolveIntersections_line() {

    }
}
