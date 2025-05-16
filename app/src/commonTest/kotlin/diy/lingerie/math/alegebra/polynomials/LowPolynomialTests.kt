package diy.lingerie.math.alegebra.polynomials

import diy.lingerie.math.algebra.polynomials.CubicPolynomial
import diy.lingerie.math.algebra.polynomials.LinearPolynomial
import diy.lingerie.math.algebra.polynomials.LowPolynomial
import diy.lingerie.math.algebra.polynomials.LowPolynomial.Dilation
import diy.lingerie.math.algebra.polynomials.LowPolynomial.Modulation
import diy.lingerie.math.algebra.polynomials.LowPolynomial.Shift
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
                dilation = Dilation(
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
                shift = Shift(
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
                modulation = Modulation(
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
    private fun testNormalModulationForward(
        normalPolynomial: LowPolynomial,
        modulation: Modulation,
    ) {
        val projectedPolynomial = normalPolynomial.modulate(modulation)

        val (normalizedPolynomial, normalModulation) = assertNotNull(
            projectedPolynomial.normalize(),
        )

        val renormalizedPolynomial = projectedPolynomial.modulate(
            normalModulation.invert(),
        )

        assertEqualsWithTolerance(
            expected = modulation,
            actual = normalModulation,
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
    private fun testNormalModulationBackward(
        polynomial: LowPolynomial,
        expectedNormalModulation: Modulation,
    ) {
        val (normalizedPolynomial, normalModulation) = assertNotNull(
            polynomial.normalizeBySymmetry(),
        )

        assertTrue(
            normalizedPolynomial.isNormalized,
        )

        assertEqualsWithTolerance(
            expected = expectedNormalModulation,
            actual = normalModulation,
        )

        val recoveredPolynomial = normalizedPolynomial.modulate(normalModulation)

        assertEqualsWithTolerance(
            expected = polynomial,
            actual = recoveredPolynomial,
        )

        val renormalizedPolynomial = polynomial.modulate(
            normalModulation.invert(),
        )

        assertEqualsWithTolerance(
            expected = normalizedPolynomial,
            actual = renormalizedPolynomial,
        )
    }

    @Test
    fun testNormalModulationLinear() {
        testNormalModulationForward(
            normalPolynomial = LinearPolynomial(
                a0 = 0.0,
                a1 = 1.0,
            ),
            modulation = Modulation(
                shift = 1.1,
                dilation = 2.2,
            ),
        )
    }

    @Test
    fun testReverseNormalModulationLinear() {
        testNormalModulationBackward(
            polynomial = LinearPolynomial(
                a0 = 1.234,
                a1 = 2.345,
            ),
            expectedNormalModulation = Modulation(
                dilation = Dilation(dilation = 0.42643923240938164),
                shift = Shift(shift = 0.0),
            ),
        )
    }

    @Test
    fun testNormalModulationQuadratic() {
        testNormalModulationForward(
            normalPolynomial = QuadraticPolynomial(
                a0 = 1.234,
                a1 = 0.0,
                a2 = 1.0,
            ),
            modulation = Modulation(
                shift = 1.1,
                dilation = 2.2,
            ),
        )
    }

    @Test
    fun testReverseNormalModulationQuadratic() {
        testNormalModulationBackward(
            polynomial = QuadraticPolynomial(
                a0 = 1.234,
                a1 = 2.345,
                a2 = 3.456,
            ),
            expectedNormalModulation = Modulation(
                shift = -0.33926504629629634,
                dilation = 0.537914353639919,
            ),
        )
    }

    @Test
    fun testNormalUpModulationCubic() {
        testNormalModulationForward(
            normalPolynomial = CubicPolynomial(
                a0 = 1.234,
                a1 = 1.0,
                a2 = 0.0,
                a3 = 1.0,
            ),
            modulation = Modulation(
                shift = 1.1,
                dilation = 1.0,
            ),
        )
    }

    @Test
    fun testReverseNormalModulationCubicSimple() {
        testNormalModulationBackward(
            polynomial = CubicPolynomial(
                a0 = 1.234,
                a1 = -2.345,
                a2 = 0.0,
                a3 = 4.567,
            ),
            expectedNormalModulation = Modulation(
                dilation = Dilation(dilation = 0.6027302605741011),
                shift = Shift(shift = -0.0),
            ),
        )
    }

    @Test
    @Ignore
    fun testReverseNormalModulationCubic() {
        testNormalModulationBackward(
            polynomial = CubicPolynomial(
                a0 = 1.234,
                a1 = 2.345,
                a2 = 3.456,
                a3 = 4.567,
            ),
            expectedNormalModulation = Modulation(
                shift = -0.2522443617254215,
                dilation = 2.7526691000419197,
            ),
        )
    }
}
