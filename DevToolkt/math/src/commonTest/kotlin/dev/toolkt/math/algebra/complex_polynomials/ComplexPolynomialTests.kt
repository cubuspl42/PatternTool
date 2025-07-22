package dev.toolkt.math.algebra.complex_polynomials

import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.math.algebra.Complex
import dev.toolkt.math.algebra.toComplex
import kotlin.test.Ignore
import kotlin.test.Test

class ComplexPolynomialTests {
    @Test
    fun testFindRoots_1() {
        val complexPolynomial = ComplexPolynomial(
            7.0.toComplex(),
            Complex(5.0, -1.0),
            Complex(2.0, 3.0),
            1.0.toComplex(),
        )

        val actualRoots = complexPolynomial.findRoots()

        assertEqualsWithTolerance(
            expected = listOf(
                Complex(-0.869, -0.600),
                Complex(0.372, 1.53),
                Complex(-1.50, -3.93),
            ),
            actual = actualRoots,
            tolerance = NumericTolerance.Absolute(
                absoluteTolerance = 0.01,
            ),
        )
    }

    @Test
    @Ignore // TODO: Throw this away?
    fun testFindRoots_2() {
        val complexPolynomial = ComplexPolynomial.normalized(
            listOf(
                3.8334113022231994E19,
                1.1793555015861604E19,
                -1.5391888998561504E20,
                -4.1905315718014566E17,
                2.0573425073610706E20,
                -1.3132101840405886E20,
                4.39142932110886E19,
                -5.474884235995861E18,
                -1.0372432085308752E19,
                5.175541418768466E17,
            ),
        )

        val actualRoots = complexPolynomial.findRoots()

        assertEqualsWithTolerance(
            expected = listOf(
                Complex(-1.688588, 0.495362),
                Complex(-1.688588, -0.495362),
                1.310484.toComplex(),
                0.931743.toComplex(),
                0.844232.toComplex(),
                Complex(0.124265, 0.508584),
                Complex(0.124265, -0.508584),
                (-0.314524).toComplex(),
                0.049059.toComplex(),
            ),
            actual = actualRoots,
            tolerance = NumericTolerance.Absolute(
                absoluteTolerance = 0.01,
            ),
        )
    }
}
