package diy.lingerie.math.alegebra.polynomials

import diy.lingerie.math.algebra.polynomials.ConstantPolynomial
import diy.lingerie.math.algebra.polynomials.times
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test

class ConstantPolynomialTests {
    @Test
    fun testPlus_constant() {
        val pa = ConstantPolynomial(
            a0 = 3.0,
        )

        val pb = ConstantPolynomial(
            a0 = 2.0,
        )

        val sum = pa + pb

        assertEqualsWithTolerance(
            expected = ConstantPolynomial(
                a0 = 5.0,
            ),
            actual = sum,
        )

        assertEqualsWithTolerance(
            expected = sum,
            actual = pb + pa,
        )
    }

    @Test
    fun testTimes_constant() {
        val pa = ConstantPolynomial(
            a0 = 3.0,
        )

        val pb = ConstantPolynomial(
            a0 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = ConstantPolynomial(
                a0 = 6.0,
            ),
            actual = product,
        )

        assertEqualsWithTolerance(
            expected = product,
            actual = pb * pa,
        )
    }
}
