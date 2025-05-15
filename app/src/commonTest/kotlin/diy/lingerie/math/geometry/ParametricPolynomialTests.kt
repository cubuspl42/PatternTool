package diy.lingerie.math.geometry

import diy.lingerie.math.algebra.polynomials.CubicPolynomial
import diy.lingerie.math.algebra.polynomials.LowPolynomial
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Ignore
import kotlin.test.Test

class ParametricPolynomialTests {
    @Test
    @Ignore // FIXME: Fix cubic normalization
    fun testNormalize() {
        val projection = LowPolynomial.Projection(
            shift = 1.1,
            dilation = 2.2,
        )

        val xPolynomial = CubicPolynomial(
            a0 = 1.234,
            a1 = 2.345,
            a2 = 3.456,
            a3 = 4.567,
        )

        val xPolynomialProjected = xPolynomial.project(projection)

        if (!xPolynomialProjected.equalsWithTolerance(
                CubicPolynomial(
                    a0 = 0.35462499999999975,
                    a1 = 1.0519318181818185,
                    a2 = -0.7013429752066117,
                    a3 = 0.4289068369646882,
                ),
            )
        ) {
            throw AssertionError()
        }

        val yPolynomial = CubicPolynomial(
            a0 = 5.678,
            a1 = 6.789,
            a2 = 7.890,
            a3 = 8.901,
        )

        val yPolynomialProjected = yPolynomial.project(projection)

        if (!yPolynomialProjected.equalsWithTolerance(
                CubicPolynomial(
                    a0 = 3.143375,
                    a1 = 2.5339772727272725,
                    a2 = -1.1284090909090907,
                    a3 = 0.8359316303531178,
                ),
            )
        ) {
            throw AssertionError()
        }

        val firstParametricPolynomial = ParametricPolynomial(
            xFunction = xPolynomial,
            yFunction = yPolynomial,
        )

        val secondParametricPolynomial = ParametricPolynomial(
            xFunction = xPolynomialProjected,
            yFunction = yPolynomialProjected,
        )

        val firstNormalized = firstParametricPolynomial.normalize()
        val secondNormalized = secondParametricPolynomial.normalize()

        assertEqualsWithTolerance(
            expected = firstNormalized,
            actual = secondNormalized,
        )
    }
}
