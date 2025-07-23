package dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials

import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.core.range.ClosedFloatingPointRangeUtils
import dev.toolkt.geometry.Direction
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.RelativeAngle
import dev.toolkt.geometry.Span
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricCurveFunction.InvertedCurveFunction.InversionResult
import dev.toolkt.math.algebra.linear.vectors.Vector2
import kotlin.math.nextDown
import kotlin.math.nextUp
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CubicBezierBinomialInversionFuzzyTests {
    @Test
    fun testInversion_aroundSelfIntersection_fuzzy() {
        val random = Random(0)

        // A curve with a self-intersection (partially outside its [0, 1] range)
        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(a0 = 492.59773540496826, a1 = 197.3452272415161),
            point1 = Vector2(a0 = 393.3277416229248, a1 = 180.14210319519043),
            point2 = Vector2(a0 = 287.3950023651123, a1 = 260.3726043701172),
            point3 = Vector2(a0 = 671.4185047149658, a1 = 490.2051086425781)
        )

        val computationTolerance = NumericTolerance.Absolute(
            absoluteTolerance = 1e-6,
        )

        // Close to the self-intersection the numerical accuracy of the found
        // t-values is acceptable, but not impressive
        val testTolerance = NumericTolerance.Absolute(
            absoluteTolerance = 1e-3,
        )

        val invertedCurveFunction = cubicBezierBinomial.buildInvertedFunction(
            tolerance = computationTolerance,
        )

        val selfIntersectionResult = cubicBezierBinomial.findSelfIntersection(
            tolerance = computationTolerance,
        ) as CubicBezierBinomial.SelfIntersectionResult

        /**
         * Test the t-values that are as very close (but not extremely close) to
         * the intersection t-value, ensuring that most of them gives a t-value
         * approximation. If they do, it should be a good approximation.
         */
        fun testParamsRoughlyAroundSelfIntersection(
            tS: Double,
        ) {
            val count = 1024 // An arbitrary value
            val width = 1e-9 // A hand-picked value

            val selfIntersectionPoint = cubicBezierBinomial.apply(tS)

            assertEquals(
                expected = InversionResult.SelfIntersection,
                actual = invertedCurveFunction.apply(selfIntersectionPoint),
            )

            // Count the numer of specific (non-self-intersection results within
            // the narrow range)
            val specificResultCount = LinSpace(
                range = ClosedFloatingPointRangeUtils.around(
                    x0 = tS,
                    width = width,
                ),
                sampleCount = count,
            ).generate().count { t ->
                val nearlySelfIntersectionPoint = cubicBezierBinomial.apply(t)

                val fuzzedPoint = random.nextPointCloseTo(
                    point = Point(nearlySelfIntersectionPoint),
                    // If we move away further, the t-values aren't within the
                    // tolerance (which is not very strict).
                    maxDistance = 1e-12,
                ).pointVector

                val specificResult = invertedCurveFunction.apply(
                    fuzzedPoint,
                ) as? InversionResult.Specific ?: return@count false

                assertEqualsWithTolerance(
                    expected = t,
                    actual = specificResult.t,
                    tolerance = testTolerance,
                )

                return@count true
            }

            assertTrue(
                actual = specificResultCount > 768, // A hand-picked value
                message = "Expected nearly exclusively specific results within $width around self-intersection, but got only $specificResultCount out of $count",
            )
        }

        /**
         * Test the t-values that are as numerically close to the intersection
         * t-value as possible, ensuring that they all result in the
         * "self-intersection" classification.
         */
        fun testParamsStrictlyAroundSelfIntersection(
            tS: Double,
        ) {
            val count = 1024

            generateSequence(seed = tS) { it.nextUp() }.take(count).forEach { t ->
                val upSelfIntersectionPoint = cubicBezierBinomial.apply(t)

                assertEquals(
                    expected = InversionResult.SelfIntersection,
                    actual = invertedCurveFunction.apply(upSelfIntersectionPoint),
                )
            }

            generateSequence(seed = tS) { it.nextDown() }.take(count).forEach { t ->
                val downSelfIntersectionPoint = cubicBezierBinomial.apply(t)

                assertEquals(
                    expected = InversionResult.SelfIntersection,
                    actual = invertedCurveFunction.apply(downSelfIntersectionPoint),
                )
            }
        }

        /**
         * Test the t-values that are around the self-intersection (both extremely
         * close and somewhat close).
         */
        fun testParamsAroundSelfIntersection(
            tS: Double,
        ) {
            testParamsRoughlyAroundSelfIntersection(
                tS = tS,
            )

            testParamsStrictlyAroundSelfIntersection(
                tS = tS,
            )
        }

        testParamsAroundSelfIntersection(
            tS = selfIntersectionResult.t0,
        )

        testParamsAroundSelfIntersection(
            tS = selfIntersectionResult.t1,
        )
    }
}

private fun Random.nextPointCloseTo(
    point: Point,
    maxDistance: Double,
): Point {
    val relativeAngle = RelativeAngle.fractional(nextDouble())

    return point.translateByDistance(
        direction = Direction.fromAngle(relativeAngle),
        distance = Span.of(nextDouble() * maxDistance),
    )
}
