package dev.toolkt.math.geometry.parametric_curve_functions.bezier_binomials

import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.QuadraticBezierBinomial
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test

class QuadraticBezierBinomialJvmTests {
    @Test
    fun testPrimaryArcLengthGauss() {
        val quadraticBezierBinomial = QuadraticBezierBinomial(
            point0 = Vector2(0.0, 0.0),
            point1 = Vector2(100.0, 200.0),
            point2 = Vector2(200.0, 0.0),
        )

        assertEqualsWithTolerance(
            expected = quadraticBezierBinomial.primaryArcLengthNearlyExact,
            actual = quadraticBezierBinomial.primaryArcLengthGauss,
            tolerance = NumericTolerance.Absolute(
                absoluteTolerance = 1e-2,
            ),
        )
    }
}
