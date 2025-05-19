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

    @Test
    fun testRaise() {
        val quadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(0.0, 0.0),
            point1 = Vector2(300.0, 300.0),
            point2 = Vector2(700.0, 100.0),
        )

        val cubicBezierBinomial = quadraticBezierBinomial.raise()

        if (!cubicBezierBinomial.equalsWithTolerance(
                CubicBezierBinomial(
                    point0 = Vector2(0.0, 0.0),
                    point1 = Vector2(200.0, 200.0),
                    point2 = Vector2(433.3333333333333, 233.33333333333331),
                    point3 = Vector2(700.0, 100.0),
                ),
            )
        ) {
            throw AssertionError()
        }

        assertEqualsWithTolerance(
            expected = quadraticBezierBinomial.point0,
            actual = cubicBezierBinomial.point0,
        )

        assertEqualsWithTolerance(
            expected = quadraticBezierBinomial.point2,
            actual = cubicBezierBinomial.point3,
        )

        cubicBezierBinomial.sample(16).forEach { (t, point) ->
            assertEqualsWithTolerance(
                expected = quadraticBezierBinomial.apply(t),
                actual = point,
            )
        }

        val loweredQuadraticBezierBinomial = cubicBezierBinomial.lower()

        assertEqualsWithTolerance(
            expected = quadraticBezierBinomial,
            actual = loweredQuadraticBezierBinomial,
        )
    }

    @Test
    fun testLower_close() {
        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(0.0, 0.0),
            point1 = Vector2(200.123, 200.123),
            point2 = Vector2(433.345, 233.345),
            point3 = Vector2(700.11, 100.22),
        )

        val loweredQuadraticBezierBinomial = cubicBezierBinomial.lower()

        val expectedQuadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(0.0, 0.0),
            point1 = Vector2(300.0, 300.0),
            point2 = Vector2(700.0, 100.0),
        )

        assertEqualsWithTolerance(
            expected = expectedQuadraticBezierBinomial,
            actual = loweredQuadraticBezierBinomial,
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 0.5,
            ),
        )
    }
}
