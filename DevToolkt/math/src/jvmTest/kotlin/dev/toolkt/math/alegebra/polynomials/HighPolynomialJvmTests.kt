package dev.toolkt.math.alegebra.polynomials

import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.math.algebra.polynomials.HighPolynomial
import dev.toolkt.math.algebra.polynomials.QuadraticPolynomial
import dev.toolkt.math.algebra.polynomials.findRootsExternally
import org.apache.commons.math3.exception.TooManyEvaluationsException
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertIs

class HighPolynomialJvmTests {
    @Test
    fun testFindRootsExternally_trivial() {
        // (x - 2)(x + 2) = x^2 - 4
        val quadraticPolynomial = QuadraticPolynomial(
            a0 = -4.0,
            a1 = 0.0,
            a2 = 1.0,
        )

        val roots = quadraticPolynomial.findRootsExternally(
            guessedRoot = 0.0,
            tolerance = NumericTolerance.Absolute.Default,
        )

        assertEqualsWithTolerance(
            expected = listOf(-2.0, 2.0),
            actual = roots.sorted(),
        )
    }

    @Test
    fun testFindRootsExternally_nearlyFlat() {
        // A polynomial that's nearly flat
        val highPolynomial = HighPolynomial(
            19109474.12548828,
            -21240755.47631836,
            -173992658.13415527,
            216950597.48016357,
            -97923301.45442963,
            14563506.357002258,
            1382754.9785599709,
            -377021.60081863403,
            5.890038728713989,
            0.00009073317050933838,
        )

        val roots = highPolynomial.findRootsExternally(
            guessedRoot = 0.5,
            tolerance = NumericTolerance.Absolute.Default,
        ).filter { it in 0.0..1.0 }

        assertEqualsWithTolerance(
            expected = listOf(
                0.32803571347803123,
            ),
            actual = roots.sorted(),
        )
    }


    @Test
    fun testFindRootsExternally_doubleRoot_1() {
        // A polynomial with a (kind-of?) double root near t=1/3
        val highPolynomial = HighPolynomial(
            20078984020.030273,
            -194221035551.48682,
            731430538900.0114,
            -1388361496042.3926,
            1461775490027.8342,
            -884725256790.8938,
            311671019285.91235,
            -64884190611.010315,
            7649378521.630905,
            -454959157.46955943,
        )

        val roots = highPolynomial.findRootsExternally(
            guessedRoot = 0.5,
            tolerance = NumericTolerance.Absolute.Default,
        )

        assertEqualsWithTolerance(
            expected = listOf(
                0.323772312142875,
                0.3523093492271627,
                0.8887242188621922,
            ),
            actual = roots.sorted(),
        )
    }

    @Test
    fun testFindRootsExternally_doubleRoot_2() {
        // A polynomial with a nearly perfect double root
        val highPolynomial = HighPolynomial(
            -9676322958.109375,
            267297832473.50488,
            -2192107356852.289,
            4889287438971.579,
            -4585568530790.822,
            2003421283334.5103,
            -410408942109.96747,
            70023103686.45605,
            -37272903508.40637,
            4962870355.709473,
        )

        val roots = highPolynomial.findRootsExternally(
            guessedRoot = 0.5,
            tolerance = NumericTolerance.Absolute.Default,
        )

        assertEqualsWithTolerance(
            expected = listOf(
                0.08002436540083643,
                0.08120060962551975,
                6.221066214781461,
            ),
            actual = roots.sorted(),
        )
    }

    @Test
    @Ignore // This test is very slow, it can be manually un-ignored when necessary
    fun testFindRootsExternally_tricky_badGuess() {
        val highPolynomial = HighPolynomial(
            -1.0, 0.0, 0.0, 0.0, 1.0
        )

        assertIs<TooManyEvaluationsException>(
            assertFails {
                highPolynomial.findRootsExternally(
                    // With this guessed value, the algorithm doesn't converge
                    guessedRoot = 0.0,
                    tolerance = NumericTolerance.Absolute.Default,
                )
            },
        )
    }

    @Test
    fun testFindRootsExternally_tricky_betterGuess() {
        val highPolynomial = HighPolynomial(
            -1.0, 0.0, 0.0, 0.0, 1.0
        )

        val roots = highPolynomial.findRootsExternally(
            // With this guessed value, the algorithm does converge
            guessedRoot = 0.5,
            tolerance = NumericTolerance.Absolute.Default,
        )

        assertEqualsWithTolerance(
            expected = listOf(
                -1.0,
                1.0,
            ),
            actual = roots.sorted(),
        )
    }
}