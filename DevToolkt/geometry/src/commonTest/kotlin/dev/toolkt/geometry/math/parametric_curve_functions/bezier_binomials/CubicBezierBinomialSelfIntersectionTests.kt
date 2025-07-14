package dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial.SelfIntersectionResult
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.times
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CubicBezierBinomialSelfIntersectionTests {
    /**
     * A cubic Bezier binomial that degenerates to a point
     */
    @Test
    fun testFindSelfIntersection_degenerate_point() {
        val tolerance = NumericObject.Tolerance.Default

        val point = Vector2(
            a0 = 492.59773540496826,
            a1 = 197.3452272415161,
        )

        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = point,
            point1 = point,
            point2 = point,
            point3 = point,
        )

        assertNull(
            cubicBezierBinomial.findSelfIntersection(
                tolerance = tolerance,
            ),
        )
    }

    /**
     * A cubic Bezier binomial that degenerates to a line
     */
    @Test
    fun testFindSelfIntersection_degenerate_line() {
        val tolerance = NumericObject.Tolerance.Default

        val origin = Vector2(
            a0 = 492.59773540496826,
            a1 = 197.3452272415161,
        )

        val delta = Vector2(10.0, 0.0)

        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = origin,
            point1 = origin + 1.0 * delta,
            point2 = origin + 2.0 * delta,
            point3 = origin + 3.0 * delta,
        )

        assertNull(
            cubicBezierBinomial.findSelfIntersection(
                tolerance = tolerance,
            ),
        )
    }

    /**
     * A cubic Bezier binomial that degenerates to a quadratic curve
     */
    @Test
    fun testFindSelfIntersection_degenerate_quadratic() {
        val tolerance = NumericObject.Tolerance.Default

        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(100.0, 100.0),
            point1 = Vector2(200.0, 200.0),
            point2 = Vector2(300.0, 200.0),
            point3 = Vector2(400.0, 100.0),
        )

        assertNull(
            cubicBezierBinomial.findSelfIntersection(
                tolerance = tolerance,
            ),
        )
    }

    /**
     * A proper cubic Bezier binomial that does not have self-intersection at all
     */
    @Test
    @Ignore // FIXME: Figure this out (there's an issue in the paper?)
    fun testFindSelfIntersection_nonExisting_quasiDegenerate() {
        val tolerance = NumericObject.Tolerance.Default

        // This curve is actually a proper cubic curve (it doesn't degenerate
        // to a quadratic curve; it has an inflection point), yet it results
        // in a A1 matrix with determinant equal to zero
        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(0.0, 200.0),
            point1 = Vector2(100.0, 0.0),
            point2 = Vector2(200.0, 200.0),
            point3 = Vector2(300.0, 0.0),
        )

        val selfIntersectionResult = cubicBezierBinomial.findSelfIntersection(
            tolerance = tolerance,
        )

        assertEquals(
            expected = SelfIntersectionResult.NonExisting,
            actual = selfIntersectionResult,
        )
    }

    /**
     * A proper cubic Bezier binomial that does not have self-intersection at all
     */
    @Test
    fun testFindSelfIntersection_nonExisting_simple() {
        val tolerance = NumericObject.Tolerance.Default

        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(11.3, 200.0),
            point1 = Vector2(100.0, 12.3),
            point2 = Vector2(200.0, 200.0),
            point3 = Vector2(300.0, 11.22),
        )

        val selfIntersectionResult = cubicBezierBinomial.findSelfIntersection(
            tolerance = tolerance,
        )

        assertEquals(
            expected = SelfIntersectionResult.NonExisting,
            actual = selfIntersectionResult,
        )
    }

    /**
     * A cubic Bezier binomial that has self-intersection in its primary (0.0 to 1.0) range
     */
    @Test
    fun testFindSelfIntersection_existing_proper_1() {
        val tolerance = NumericObject.Tolerance.Default

        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(401.23199462890625, 505.39300537109375),
            point1 = Vector2(845.5590209960938, 54.729000091552734),
            point2 = Vector2(82.95800018310547, 74.0530014038086),
            point3 = Vector2(643.9940185546875, 402.84600830078125),
        )

        val selfIntersectionResult = assertNotNull(
            cubicBezierBinomial.findSelfIntersection(
                tolerance = tolerance,
            ),
        )

        assertSelfIntersectionExists(
            cubicBezierBinomial = cubicBezierBinomial,
            expectedResult = SelfIntersectionResult.Existing(
                t0 = 0.1562984990421814,
                t1 = 0.9175376647659079
            ),
            expectedPoint = Vector2(530.7843327892789, 327.91010317869666),
            actualResult = selfIntersectionResult,
            tolerance = tolerance,
        )
    }

    /**
     * A cubic Bezier binomial that has self-intersection outside of its primary (0.0 to 1.0) range
     */
    @Test
    fun testFindSelfIntersection_existing_extended_1() {
        val tolerance = NumericObject.Tolerance.Default

        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(455.3070068359375, 417.5899963378906),
            point1 = Vector2(511.3800048828125, 359.9530029296875),
            point2 = Vector2(540.6959838867188, 311.3819885253906),
            point3 = Vector2(552.6610107421875, 270.9620056152344),
        )

        val selfIntersectionResult = assertNotNull(
            cubicBezierBinomial.findSelfIntersection(
                tolerance = tolerance,
            ),
        )

        assertSelfIntersectionExists(
            cubicBezierBinomial = cubicBezierBinomial,
            expectedResult = SelfIntersectionResult.Existing(
                t0 = 2.5780884122264855,
                t1 = 5.495637186468656,
            ),
            expectedPoint = Vector2(516.6420774390026, 136.90441263698267),
            actualResult = selfIntersectionResult,
            tolerance = tolerance,
        )
    }
}

/**
 * Asserts that the [expectedResult] self-intersection result is equal to the [actualResult] within the given [tolerance].
 * Verifies that the points at the intersection parameters `t0` and `t1` are equal within the given [tolerance] and
 * that they match the [expectedPoint].
 */
private fun assertSelfIntersectionExists(
    cubicBezierBinomial: CubicBezierBinomial,
    expectedResult: SelfIntersectionResult.Existing,
    expectedPoint: Vector2,
    actualResult: SelfIntersectionResult,
    tolerance: NumericObject.Tolerance,
) {
    val actualExisting = assertIs<SelfIntersectionResult.Existing>(
        actualResult,
    )

    val point0 = cubicBezierBinomial.apply(actualExisting.t0)
    val point1 = cubicBezierBinomial.apply(actualExisting.t0)

    assertEqualsWithTolerance(
        expected = point1,
        actual = point0,
        tolerance = tolerance,
        message = "Self-intersection points do not match",
    )

    assertEqualsWithTolerance(
        expected = expectedPoint,
        actual = point0,
        tolerance = tolerance,
        message = "The first self-intersection point does not match the expected point",
    )

    assertEqualsWithTolerance(
        expected = expectedPoint,
        actual = point1,
        tolerance = tolerance,
        message = "The second self-intersection point does not match the expected point",
    )

    assertEquals(
        expected = expectedResult,
        actual = actualExisting,
        message = "Self-intersection result does not match expected",
    )
}
