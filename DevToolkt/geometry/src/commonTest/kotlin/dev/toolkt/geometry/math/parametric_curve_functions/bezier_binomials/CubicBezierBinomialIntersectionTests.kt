package dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricCurveFunction.Companion.primaryTRange
import dev.toolkt.math.algebra.linear.vectors.Vector2
import kotlin.test.Test

class CubicBezierBinomialIntersectionTests {
    @Test
    fun testSolveIntersections_cubicBezierBinomials_threeIntersections_1() {
        val firstCubicBezierBinomial = CubicBezierBinomial(
            Vector2(1547.0, 893.0),
            Vector2(964.0, 592.0),
            Vector2(1044.0, 207.0),
            Vector2(1829.0, 625.0),
        )

        val secondCubicBezierBinomial = CubicBezierBinomial(
            Vector2(1407.0, 904.0),
            Vector2(2176.0, 201.0),
            Vector2(1018.0, 402.0),
            Vector2(1707.0, 855.0),
        )

        val tolerance = NumericObject.Tolerance.Default

        val tValues = firstCubicBezierBinomial.solveIntersectionEquation(
            other = secondCubicBezierBinomial,
            tRange = primaryTRange,
            tolerance = tolerance,
        ).sorted()

        assertEqualsWithTolerance(
            expected = listOf(
                0.04905890129108739,
                0.8442338406224985,
                0.9320151200357478,
            ),
            actual = tValues.filter { it in OpenCurve.Coord.tRange },
            tolerance = tolerance,
        )
    }

    /**
     * This is a tricky setup with one curve shaped like a C and one loop. There
     * are two clear intersection points, including one that nearly overlaps
     * with the loop's self-intersection.
     */
    @Test
    fun testSolveIntersections_cubicBezierBinomials_c_loop_multipleIntersections_1() {
        val firstCubicBezierBinomial = CubicBezierBinomial(
            Vector2(1547.0, 893.0),
            Vector2(964.0, 592.0),
            Vector2(1044.0, 207.0),
            Vector2(1621.0, 797.0),
        )

        val secondCubicBezierBinomial = CubicBezierBinomial(
            Vector2(1407.0, 904.0),
            Vector2(2176.0, 201.0),
            Vector2(1018.0, 402.0),
            Vector2(1707.0, 855.0),
        )

        val tolerance = NumericObject.Tolerance.Absolute(
            absoluteTolerance = 1e-5,
        )

        val tValues = firstCubicBezierBinomial.solveIntersectionEquation(
            other = secondCubicBezierBinomial,
            tRange = primaryTRange,
            tolerance = tolerance,
        ).sorted()

        assertEqualsWithTolerance(
            expected = listOf(
                // FIXME: One of these intersections is obviously correct, the other might be acceptable, but one
                //  obviously correct intersection is definitely missing
                0.049060821533203125,
                0.9793510437011719,
            ),
            actual = tValues.filter { it in OpenCurve.Coord.tRange },
            tolerance = tolerance,
        )
    }

    /**
     * A very similar setup, but now both obvious intersections are found.
     */
    @Test
    fun testSolveIntersections_cubicBezierBinomials_c_loop_multipleIntersections_2() {
        val firstCubicBezierBinomial = CubicBezierBinomial(
            Vector2(1547.0, 893.0),
            Vector2(964.0, 592.0),
            Vector2(1044.0, 207.0),
            Vector2(1621.0, 797.0),
        )

        val secondCubicBezierBinomial = CubicBezierBinomial(
            Vector2(1407.0, 904.0),
            Vector2(2176.0, 201.0),
            Vector2(1018.0, 402.0),
            Vector2(1705.0, 855.0),
        )

        val tolerance = NumericObject.Tolerance.Absolute(
            absoluteTolerance = 1e-5,
        )

        val tValues = firstCubicBezierBinomial.solveIntersectionEquation(
            other = secondCubicBezierBinomial,
            tRange = primaryTRange,
            tolerance = tolerance,
        ).sorted()

        assertEqualsWithTolerance(
            expected = listOf(
                // This the first obvious intersection
                0.049060821533203125,
                // This is a "bonus" but reasonable intersection (overlap)
                0.9576683044433594,
                // This the second obvious intersection
                0.9669990539550781,
                // This is another "bonus" but reasonable intersection (overlap)
                0.9886283874511719,
            ),
            actual = tValues.filter { it in OpenCurve.Coord.tRange },
            tolerance = tolerance,
        )
    }

    /**
     * At least one intersection should be found
     */
    @Test
    fun testSolveIntersections_cubicBezierBinomials_c_loop_multipleIntersections_3() {
        val firstCubicBezierBinomial = CubicBezierBinomial(
            Vector2(516.0, 340.0),
            Vector2(522.0, 400.0),
            Vector2(466.0, 494.0),
            Vector2(369.0, 614.0),
        )

        val secondCubicBezierBinomial = CubicBezierBinomial(
            Vector2(264.0, 538.0),
            Vector2(1078.0, 235.0),
            Vector2(53.0, 59.0),
            Vector2(626.0, 564.0),
        )

        val tolerance = NumericObject.Tolerance.Absolute(
            absoluteTolerance = 1e-5,
        )

        val tValues = firstCubicBezierBinomial.solveIntersectionEquation(
            other = secondCubicBezierBinomial,
            tRange = primaryTRange,
            tolerance = tolerance,
        ).sorted()

        assertEqualsWithTolerance(
            // FIXME: Find _any_ intersections!
            expected = emptyList(),
            actual = tValues.filter { it in OpenCurve.Coord.tRange },
            tolerance = tolerance,
        )
    }

    @Test
    fun testSolveIntersections_cubicBezierBinomials_threeIntersections_2() {
        val firstCubicBezierBinomial = CubicBezierBinomial(
            Vector2(1547.0, 893.0),
            Vector2(964.0, 592.0),
            Vector2(1044.0, 207.0),
            Vector2(1830.0, 624.0),
        )

        val secondCubicBezierBinomial = CubicBezierBinomial(
            Vector2(1407.0, 904.0),
            Vector2(2176.0, 201.0),
            Vector2(1018.0, 402.0),
            Vector2(1707.0, 855.0),
        )

        val tolerance = NumericObject.Tolerance.Absolute(
            absoluteTolerance = 1e-5,
        )

        val tValues = firstCubicBezierBinomial.solveIntersectionEquation(
            other = secondCubicBezierBinomial,
            tRange = primaryTRange,
            tolerance = tolerance,
        ).sorted()

        assertEqualsWithTolerance(
            expected = listOf(
                0.049059,
                0.844232,
                0.931743,
            ),
            actual = tValues.filter { it in OpenCurve.Coord.tRange },
            tolerance = tolerance,
        )
    }

    @Test
    fun testSolveIntersections_cubicBezierBinomials_nineIntersections() {
        val firstCubicBezierBinomial = CubicBezierBinomial(
            Vector2(273.80049324035645, 489.08709716796875),
            Vector2(1068.5394763946533, 253.16610717773438),
            Vector2(-125.00849723815918, 252.71710205078125),
            Vector2(671.4185047149658, 490.2051086425781),
        )

        val secondCubicBezierBinomial = CubicBezierBinomial(
            Vector2(372.6355152130127, 191.58710479736328),
            Vector2(496.35252571105957, 852.5531311035156),
            Vector2(442.4235095977783, -54.72489929199219),
            Vector2(569.3854846954346, 487.569091796875),
        )

        val tValues = firstCubicBezierBinomial.solveIntersectionEquation(
            other = secondCubicBezierBinomial,
            tRange = primaryTRange,
            tolerance = NumericObject.Tolerance.Default,
        ).sorted()

        // For the most complex case, the equation solving finds all intersections
        // nearly perfectly and gives no false positives
        assertEqualsWithTolerance(
            expected = listOf(
                0.08361584060373373,
                0.10193180513525768,
                0.19350344745951725,
                0.42505456557773347,
                0.46681258537835646,
                0.638175514633884,
                0.8267616607759748,
                0.8785849663620957,
                0.9472752305695555,
            ),
            actual = tValues,
        )
    }

    @Test
    fun testSolveIntersections_cubicBezierBinomial_selfIntersection() {
        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(233.92449010844575, 500.813035986871),
            point1 = Vector2(422.77519184542564, 441.5255275486571),
            point2 = Vector2(482.0980368984025, 387.5853838361354),
            point3 = Vector2(486.0476425340348, 351.778389940191),
        )

        val tValues = cubicBezierBinomial.solveIntersectionEquation(
            other = cubicBezierBinomial,
            tRange = primaryTRange,
            tolerance = NumericObject.Tolerance.Default,
        )

        // The equation solving doesn't seem to behave reasonably when
        // finding intersections of a curve with itself
        assertEqualsWithTolerance(
            expected = listOf(
                // This t-value is withing the [0, 1] range, but otherwise it
                // appears random
                0.25925509193554785,
            ),
            actual = tValues,
        )
    }

    @Test
    fun testSolveIntersections_cubicBezierBinomial_loop() {
        val firstCubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(233.92449010844575, 500.813035986871),
            point1 = Vector2(422.77519184542564, 441.5255275486571),
            point2 = Vector2(482.0980368984025, 387.5853838361354),
            point3 = Vector2(486.0476425340348, 351.778389940191),
        )

        val secondCubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(382.2960291124364, 335.5675928528492),
            point1 = Vector2(370.41409366476535, 370.845949740462),
            point2 = Vector2(402.03174182196125, 441.30516989916543),
            point3 = Vector2(551.3035908506827, 559.7310384198445),
        )

        // These two cubic curves, although they have clearly distinct control
        // points and intersect exactly once in the [0, 1] range, in the Real
        // range they are a single self-intersecting loop curve
        if (!firstCubicBezierBinomial.isFullyOverlapping(secondCubicBezierBinomial)) {
            throw AssertionError("The curves were assumed to be overlapping")
        }

        assertEqualsWithTolerance(
            actual = firstCubicBezierBinomial.normalize(),
            expected = secondCubicBezierBinomial.normalize(),
        )

        val tValues = firstCubicBezierBinomial.solveIntersectionEquation(
            other = secondCubicBezierBinomial,
            tRange = primaryTRange,
            tolerance = NumericObject.Tolerance.Default,
        )

        // The equation solving doesn't seem to be capable of finding the
        // intersection of these curves
        assertEqualsWithTolerance(
            expected = emptyList(),
            actual = tValues,
        )
    }

    @Test
    fun testSolveIntersections_line() {

    }
}

