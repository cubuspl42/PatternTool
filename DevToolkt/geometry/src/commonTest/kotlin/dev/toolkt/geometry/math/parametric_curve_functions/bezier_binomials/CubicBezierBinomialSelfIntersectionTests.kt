package dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials

import dev.toolkt.core.numeric.NumericTolerance
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
        val tolerance = NumericTolerance.Absolute.Default

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
        val tolerance = NumericTolerance.Absolute.Default

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
        val tolerance = NumericTolerance.Absolute.Default

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
        val tolerance = NumericTolerance.Absolute.Default

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
        val tolerance = NumericTolerance.Absolute.Default

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
        val tolerance = NumericTolerance.Absolute.Default

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
                t1 = 0.9175376647659079,
            ),
            expectedPoint = Vector2(530.7843327892789, 327.91010317869666),
            actualResult = selfIntersectionResult,
            tolerance = tolerance,
        )
    }

    /**
     * A cubic Bezier binomial that has self-intersection fully outside its primary (0.0 to 1.0) range
     */
    @Test
    fun testFindSelfIntersection_existing_extendedFully_1() {
        val tolerance = NumericTolerance.Absolute.Default

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

    /**
     * A cubic Bezier binomial that has self-intersection partially outside its primary (0.0 to 1.0) range
     * (one t-value is within 0.0 to 1.0, the other is outside)
     */
    @Test
    fun testFindSelfIntersection_existing_extendedPartially_1() {
        val tolerance = NumericTolerance.Absolute.Default

        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(a0 = 492.59773540496826, a1 = 197.3452272415161),
            point1 = Vector2(a0 = 393.3277416229248, a1 = 180.14210319519043),
            point2 = Vector2(a0 = 287.3950023651123, a1 = 260.3726043701172),
            point3 = Vector2(a0 = 671.4185047149658, a1 = 490.2051086425781)
        )

        val selfIntersectionResult = assertNotNull(
            cubicBezierBinomial.findSelfIntersection(
                tolerance = tolerance,
            ),
        )

        assertSelfIntersectionExists(
            cubicBezierBinomial = cubicBezierBinomial,
            expectedResult = SelfIntersectionResult.Existing(
                t0 = -0.7393528461432413,
                t1 = 0.8083924555183848,
            ),
            expectedPoint = Vector2(501.14355433959827, 374.2024184921395),
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
    tolerance: NumericTolerance,
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
        message = "The first self-intersection point does not match the expected point (expected: $expectedPoint, actual: $point0)",
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
