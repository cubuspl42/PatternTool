package diy.lingerie.math.alegebra.polynomials

import diy.lingerie.math.algebra.polynomials.CubicPolynomial
import diy.lingerie.math.algebra.polynomials.LinearPolynomial
import diy.lingerie.math.algebra.polynomials.LowPolynomial
import diy.lingerie.math.algebra.polynomials.QuadraticPolynomial
import diy.lingerie.math.algebra.polynomials.dilate
import diy.lingerie.math.algebra.polynomials.modulate
import diy.lingerie.math.algebra.polynomials.shift
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LowPolynomialTests {
    @Test
    fun testDilation() {
        val cubicPolynomial = CubicPolynomial(
            a0 = 1.234,
            a1 = 2.345,
            a2 = 3.456,
            a3 = 4.567,
        )

        assertEqualsWithTolerance(
            expected = CubicPolynomial(
                a0 = 1.234,
                a1 = 1.065909090909091,
                a2 = 0.7140495867768594,
                a3 = 0.4289068369646881,
            ), actual = cubicPolynomial.dilate(
                dilation = LowPolynomial.Dilation(
                    dilation = 2.2,
                ),
            )
        )
    }

    @Test
    fun testShift() {
        val cubicPolynomial = CubicPolynomial(
            a0 = 1.234,
            a1 = 2.345,
            a2 = 3.456,
            a3 = 4.567,
        )

        assertEqualsWithTolerance(
            expected = CubicPolynomial(
                a0 = -132.992939,
                a1 = 128.73928999999998,
                a2 = -41.757299999999994,
                a3 = 4.567,
            ),
            actual = cubicPolynomial.shift(
                shift = LowPolynomial.Shift(
                    shift = 3.3,
                ),
            ),
        )
    }

    @Test
    fun testProject() {
        val cubicPolynomial = CubicPolynomial(
            a0 = 1.234,
            a1 = 2.345,
            a2 = 3.456,
            a3 = 4.567,
        )

        assertEqualsWithTolerance(
            expected = CubicPolynomial(
                a0 = 0.35462499999999975,
                a1 = 1.0519318181818185,
                a2 = -0.7013429752066117,
                a3 = 0.4289068369646882,
            ),
            actual = cubicPolynomial.modulate(
                modulation = LowPolynomial.Modulation(
                    shift = 1.1,
                    dilation = 2.2,
                ),
            ),
        )
    }

    /**
     * Starting from a normal polynomial, project it in an arbitrary way,
     * then normalize. The result should be the same as the original, yielding
     * the used projection.
     */
    private fun testNormalProjectionForward(
        normalPolynomial: LowPolynomial,
        modulation: LowPolynomial.Modulation,
    ) {
        val projectedPolynomial = normalPolynomial.modulate(modulation)

        val (normalizedPolynomial, normalProjection) = assertNotNull(
            projectedPolynomial.normalize(),
        )

        val renormalizedPolynomial = projectedPolynomial.modulate(
            normalProjection.invert(),
        )

        assertEqualsWithTolerance(
            expected = modulation,
            actual = normalProjection,
        )

        assertEqualsWithTolerance(
            expected = normalPolynomial,
            actual = normalizedPolynomial,
        )

        assertEqualsWithTolerance(
            expected = normalPolynomial,
            actual = renormalizedPolynomial,
        )
    }

    /**
     * Starting from an arbitrary polynomial, normalize it and ensure that
     * the result is a normal polynomial.
     */
    private fun testNormalProjectionBackward(
        polynomial: LowPolynomial,
        expectedNormalModulation: LowPolynomial.Modulation,
    ) {
        val (normalizedPolynomial, normalProjection) = assertNotNull(
            polynomial.normalize(),
        )

        assertTrue(
            normalizedPolynomial.isNormalized,
        )

//        assertEqualsWithTolerance(
//            expected = expectedNormalProjection,
//            actual = normalProjection,
//        )

        val recoveredPolynomial = normalizedPolynomial.modulate(normalProjection)

        assertEqualsWithTolerance(
            expected = polynomial,
            actual = recoveredPolynomial,
        )

        val renormalizedPolynomial = polynomial.modulate(
            normalProjection.invert(),
        )

        assertEqualsWithTolerance(
            expected = normalizedPolynomial,
            actual = renormalizedPolynomial,
        )
    }

    @Test
    fun testNormalProjectionLinear() {
        testNormalProjectionForward(
            normalPolynomial = LinearPolynomial(
                a0 = 0.0,
                a1 = 1.0,
            ),
            modulation = LowPolynomial.Modulation(
                shift = 1.1,
                dilation = 2.2,
            ),
        )
    }

    @Test
    fun testReverseNormalProjectionLinear() {
        testNormalProjectionBackward(
            polynomial = LinearPolynomial(
                a0 = 1.234,
                a1 = 2.345,
            ),
            expectedNormalModulation = LowPolynomial.Modulation(
                shift = -0.5262260127931769,
                dilation = 0.42643923240938164,
            ),
        )
    }

    @Test
    fun testNormalProjectionQuadratic() {
        testNormalProjectionForward(
            normalPolynomial = QuadraticPolynomial(
                a0 = 1.234,
                a1 = 0.0,
                a2 = 1.0,
            ),
            modulation = LowPolynomial.Modulation(
                shift = 1.1,
                dilation = 2.2,
            ),
        )
    }

    @Test
    fun testReverseNormalProjectionQuadratic() {
        testNormalProjectionBackward(
            polynomial = QuadraticPolynomial(
                a0 = 1.234,
                a1 = 2.345,
                a2 = 3.456,
            ),
            expectedNormalModulation = LowPolynomial.Modulation(
                shift = -0.33926504629629634,
                dilation = 0.537914353639919,
            ),
        )
    }

    @Test
    fun testNormalUpProjectionCubic() {
        testNormalProjectionForward(
            normalPolynomial = CubicPolynomial(
                a0 = 1.234,
                a1 = 1.0,
                a2 = 0.0,
                a3 = 1.0,
            ),
            modulation = LowPolynomial.Modulation(
                shift = 1.1,
                dilation = 1.0,
            ),
        )
    }

    @Test
    @Ignore
    fun testReverseNormalProjectionCubicSimple() {
        testNormalProjectionBackward(
            polynomial = CubicPolynomial(
                a0 = 1.234,
                a1 = -2.345,
                a2 = 0.0,
                a3 = 4.567,
            ),
            expectedNormalModulation = LowPolynomial.Modulation(
                shift = -0.2522443617254215,
                dilation = 2.7526691000419197,
            ),
        )
    }

    @Test
    @Ignore
    fun testReverseNormalProjectionCubic() {
        testNormalProjectionBackward(
            polynomial = CubicPolynomial(
                a0 = 1.234,
                a1 = 2.345,
                a2 = 3.456,
                a3 = 4.567,
            ),
            expectedNormalModulation = LowPolynomial.Modulation(
                shift = -0.2522443617254215,
                dilation = 2.7526691000419197,
            ),
        )
    }
}
