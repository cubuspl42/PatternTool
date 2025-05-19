package diy.lingerie.math.geometry.parametric_curve_functions.bezier_binomials

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test

class QuadraticBezierBinomialTests {
    @Test
    fun testPrimaryArcLength() {
        val quadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(0.0, 0.0),
            point1 = Vector2(100.0, 200.0),
            point2 = Vector2(200.0, 0.0),
        )

        assertEqualsWithTolerance(
            expected = quadraticBezierBinomial.primaryArcLengthNearlyExact,
            actual = quadraticBezierBinomial.primaryArcLength,
        )
    }

    @Test
    fun testPrimaryArcLengthNearlyExact() {
        val quadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(0.0, 0.0),
            point1 = Vector2(100.0, 200.0),
            point2 = Vector2(200.0, 0.0),
        )

        assertEqualsWithTolerance(
            expected = quadraticBezierBinomial.primaryArcLengthNearlyExact,
            actual = quadraticBezierBinomial.primaryArcLengthApproximate,
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 1e-2,
            ),
        )
    }
}
