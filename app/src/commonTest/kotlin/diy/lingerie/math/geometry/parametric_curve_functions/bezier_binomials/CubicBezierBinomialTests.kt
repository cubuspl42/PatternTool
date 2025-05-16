package diy.lingerie.math.geometry.parametric_curve_functions.bezier_binomials

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.BezierCurve
import diy.lingerie.geometry.curves.ExpectedIntersection
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.curves.testBezierIntersectionsConsistentSymmetric
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CubicBezierBinomialTests {
    private fun testCorrectPointLocation(
        cubicBezierBinomial: CubicBezierBinomial,
        point: Vector2,
    ) {
        val tValue = assertNotNull(
            cubicBezierBinomial.locatePointByInversion(
                point = point,
                tolerance = NumericObject.Tolerance.Default,
            ),
        )

        assertTrue(
            tValue in 0.0..1.0,
        )

        val actualPoint = cubicBezierBinomial.apply(tValue)

        assertEqualsWithTolerance(
            expected = point,
            actual = actualPoint,
        )
    }

    @Test
    fun testLocatePointByInversion() {
        // A curve with a self-intersection (outside its [0, 1] range!)
        val bezierCurve = BezierCurve(
            start = Point(492.59773540496826, 197.3452272415161),
            firstControl = Point(393.3277416229248, 180.14210319519043),
            secondControl = Point(287.3950023651123, 260.3726043701172),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        val cubicBezierBinomial = bezierCurve.basisFunction

        val samples = cubicBezierBinomial.sample(
            n = 10000,
        )

        // For the vast majority of points, the t-value is located without issues
        assertEqualsWithTolerance(
            expected = samples.map { sample ->
                sample.t
            },
            actual = samples.map { sample ->
                cubicBezierBinomial.locatePointByInversion(
                    point = sample.point,
                    tolerance = NumericObject.Tolerance.Default,
                ) ?: throw AssertionError("Cannot find t for point ${sample.point}")
            },
        )

        testCorrectPointLocation(
            cubicBezierBinomial = cubicBezierBinomial,
            // An acceptable approximation of the self intersection point,
            // location works fine
            point = Vector2(501.14313780321595, 374.2020798247014),
        )

        assertNull(
            actual = cubicBezierBinomial.locatePointByInversion(
                // A good approximation of the self intersection point, too close
                // to the self-intersection, triggers the 0/0 safeguard
                point = Vector2(501.14355433959827, 374.2024184921395),
                tolerance = NumericObject.Tolerance.Default,
            ),
        )

        val badTValue = assertNotNull(
            cubicBezierBinomial.locatePointByInversion(
                // Another good approximation of the self intersection point,
                // very close to the self-intersection, but doesn't trigger
                // the 0/0 safeguard
                point = Vector2(501.1438111319996, 374.2024184921395),
                tolerance = NumericObject.Tolerance.Default,
            ),
        )

        assertEqualsWithTolerance(
            // A very bad approximation of t-value (not even in the [0, 1] range)
            expected = -5.68379446238774,
            actual = badTValue
        )
    }

    @Test
    fun testInvert() {
        val bezierCurve = BezierCurve(
            start = Point(492.59773540496826, 197.3452272415161),
            firstControl = Point(393.3277416229248, 180.14210319519043),
            secondControl = Point(287.3950023651123, 260.3726043701172),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        val cubicBezierBinomial = bezierCurve.basisFunction

        val invertedPolynomial = assertNotNull(
            cubicBezierBinomial.invert(),
        )

        val samples = cubicBezierBinomial.sample(n = 100)

        samples.forEachIndexed { index, sample ->
            val ratio = assertNotNull(
                invertedPolynomial.apply(sample.point),
            )

            assertEqualsWithTolerance(
                actual = ratio.value,
                expected = sample.t,
                tolerance = NumericObject.Tolerance.Default,
            )
        }
    }

    @Test
    fun testProjectPoint() {

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
