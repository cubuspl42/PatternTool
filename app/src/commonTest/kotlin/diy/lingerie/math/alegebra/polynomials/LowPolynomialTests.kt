package diy.lingerie.math.alegebra.polynomials

import diy.lingerie.math.algebra.polynomials.CubicPolynomial
import diy.lingerie.math.algebra.polynomials.LinearPolynomial
import diy.lingerie.math.algebra.polynomials.LowPolynomial
import diy.lingerie.math.algebra.polynomials.QuadraticPolynomial
import diy.lingerie.math.algebra.polynomials.project
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LowPolynomialTests {
    /**
     * Starting from a normal polynomial, project it in an arbitrary way,
     * then normalize. The result should be the same as the original, yielding
     * the used projection.
     */
    private fun testNormalProjectionForward(
        normalPolynomial: LowPolynomial,
        projection: LowPolynomial.Projection,
    ) {
        val projectedPolynomial = normalPolynomial.project(projection)

        val (normalizedPolynomial, normalProjection) = assertNotNull(
            projectedPolynomial.normalize(),
        )

        val renormalizedPolynomial = projectedPolynomial.project(
            normalProjection.invert(),
        )

        assertEqualsWithTolerance(
            expected = projection,
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
        expectedNormalProjection: LowPolynomial.Projection,
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

        val recoveredPolynomial = normalizedPolynomial.project(normalProjection)

        assertEqualsWithTolerance(
            expected = polynomial,
            actual = recoveredPolynomial,
        )

        val renormalizedPolynomial = polynomial.project(
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
            projection = LowPolynomial.Projection(
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
            expectedNormalProjection = LowPolynomial.Projection(
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
            projection = LowPolynomial.Projection(
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
            expectedNormalProjection = LowPolynomial.Projection(
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
            projection = LowPolynomial.Projection(
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
            expectedNormalProjection = LowPolynomial.Projection(
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
            expectedNormalProjection = LowPolynomial.Projection(
                shift = -0.2522443617254215,
                dilation = 2.7526691000419197,
            ),
        )
    }
}
