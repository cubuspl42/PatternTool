package diy.lingerie.math.alegebra.polynomials

import diy.lingerie.math.algebra.polynomials.ConstantPolynomial
import diy.lingerie.math.algebra.polynomials.LinearPolynomial
import diy.lingerie.math.algebra.polynomials.QuadraticPolynomial
import diy.lingerie.math.algebra.polynomials.times
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearPolynomialTests {
    @Test
    fun testNormalized() {
        assertEquals(
            expected = ConstantPolynomial(
                a0 = -11.9,
            ),
            actual = LinearPolynomial.normalized(
                a0 = -11.9,
                a1 = 0.0,
            ),
        )
    }

    @Test
    fun testTimes_scalar() {
        val pa = LinearPolynomial(
            a0 = -11.9,
            a1 = 12.3,
        )

        val s = 2.0

        val product = pa * s

        assertEqualsWithTolerance(
            expected = LinearPolynomial(
                a0 = -23.8,
                a1 = 24.6,
            ),
            actual = product,
        )

        assertEquals(
            expected = product,
            actual = s * pa,
        )
    }

    @Test
    fun testTimes_constant() {
        val pa = LinearPolynomial(
            a0 = -11.9,
            a1 = 12.3,
        )

        val pb = ConstantPolynomial(
            a0 = 10.9,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = LinearPolynomial(
                a0 = -129.71,
                a1 = 134.07,
            ),
            actual = product,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_linear() {
        val pa = LinearPolynomial(
            a0 = -11.9,
            a1 = 12.3,
        )

        val pb = LinearPolynomial(
            a0 = 10.9,
            a1 = -2.3,
        )

        /*
        Wolfram Alpha query:

        (10.9 - 2.3 * t) * (-11.9 + 12.3 * t)
         */

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = QuadraticPolynomial(
                a0 = -129.71,
                a1 = 161.44,
                a2 = -28.29,
            ),
            actual = product,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testFindRoots() {
        val pa = LinearPolynomial(
            a0 = 2.0,
            a1 = -3.0,
        )

        val roots = pa.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = listOf(0.66666),
            actual = roots,
        )
    }
}
