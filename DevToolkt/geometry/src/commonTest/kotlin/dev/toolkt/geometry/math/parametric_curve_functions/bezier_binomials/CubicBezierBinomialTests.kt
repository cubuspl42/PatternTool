package dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials

import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Rectangle
import dev.toolkt.math.algebra.linear.vectors.Vector2
import kotlin.random.Random
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CubicBezierBinomialTests {
    private data class CloserPoint(
        val t: Double,
        val point: Vector2,
        val distance: Double,
    )

    private fun testCorrectPointLocation(
        cubicBezierBinomial: CubicBezierBinomial,
        point: Vector2,
    ) {
        val tValue = assertNotNull(
            cubicBezierBinomial.locatePointByInversion(
                point = point,
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
    @Ignore // FIXME: Figure out how things _should_ behave
    // This test started failing with the simplified numeric equation solving
    fun testLocatePointByInversion() {
        // A curve with a self-intersection (outside its [0, 1] range!)
        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(a0 = 492.59773540496826, a1 = 197.3452272415161),
            point1 = Vector2(a0 = 393.3277416229248, a1 = 180.14210319519043),
            point2 = Vector2(a0 = 287.3950023651123, a1 = 260.3726043701172),
            point3 = Vector2(a0 = 671.4185047149658, a1 = 490.2051086425781)
        )

        val samples = cubicBezierBinomial.sample(
            n = 1000,
        )

        // For the vast majority of points, the t-value is located without issues
        assertEqualsWithTolerance(
            expected = samples.map { sample ->
                sample.t
            },
            actual = samples.map { sample ->
                cubicBezierBinomial.locatePointByInversion(
                    point = sample.point,
                ) ?: throw AssertionError("Cannot find t for point ${sample.point}")
            },
        )

        // An acceptable approximation of the self intersection point, somewhat close to the self-intersection
        val selfIntersectionPoint0 = Vector2(501.14313780321595, 374.2020798247014)

        // Location works fine
        testCorrectPointLocation(
            cubicBezierBinomial = cubicBezierBinomial,
            point = selfIntersectionPoint0,
        )

        // A good approximation of the self intersection point, extremely close to the self-intersection
        val selfIntersectionPoint1 = Vector2(501.14355433959827, 374.2024184921395)

        // Too close to the self-intersection, triggers the 0/0 safeguard
        assertNull(
            actual = cubicBezierBinomial.locatePointByInversion(
                point = selfIntersectionPoint1,
            ),
        )

        // Another good approximation of the self intersection point, very close to the self-intersection
        val selfIntersectionPoint2 = Vector2(501.1438111319996, 374.2024184921395)

        // Doesn't trigger the 0/0 safeguard, gives a very bad approximation of t-value instead
        // (not even in the [0, 1] range)
        val badTValue = assertNotNull(
            cubicBezierBinomial.locatePointByInversion(
                point = selfIntersectionPoint2,
            ),
        )

        assertEqualsWithTolerance(
            expected = -5.68379446238774,
            actual = badTValue,
        )
    }

    @Test
    fun testInvertRational() {
        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(a0 = 492.59773540496826, a1 = 197.3452272415161),
            point1 = Vector2(a0 = 393.3277416229248, a1 = 180.14210319519043),
            point2 = Vector2(a0 = 287.3950023651123, a1 = 260.3726043701172),
            point3 = Vector2(a0 = 671.4185047149658, a1 = 490.2051086425781)
        )

        val invertedPolynomial = assertNotNull(
            cubicBezierBinomial.inverted,
        )

        val samples = cubicBezierBinomial.sample(n = 100)

        samples.forEachIndexed { index, sample ->
            val ratio = assertNotNull(
                invertedPolynomial.apply(sample.point),
            )

            assertEqualsWithTolerance(
                actual = ratio.value,
                expected = sample.t,
                tolerance = NumericTolerance.Default,
            )
        }
    }

    @Test
    @Ignore
    fun testProjectPointIteratively_randomPoints() {
        val range = -0.01..1.01
        val random = Random(0)

        val tolerance = NumericTolerance.Absolute(
            absoluteTolerance = 1e-2,
        )

        val verificationLinSpace = LinSpace(
            range = range,
            sampleCount = 128,
        )

        // The verification tolerance for arbitrary points within the bounding box
        // For points close to the curve, the tolerance could be narrower
        val verificationTolerance = NumericTolerance.Absolute(
            absoluteTolerance = 2e-1,
        )

        val rectangle = Rectangle(
            origin = Point(180.0, 250.0),
            width = 420.0,
            height = 350.0,
        )

        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(a0 = 233.92449010844575, a1 = 500.813035986871),
            point1 = Vector2(a0 = 863.426829231712, a1 = 303.18800785949134),
            point2 = Vector2(a0 = 53.73076075494464, a1 = 164.97814335091425),
            point3 = Vector2(a0 = 551.3035908506827, a1 = 559.7310384198445),
        )

        generateSequence {
            Vector2(
                random.nextDouble(rectangle.xRange),
                random.nextDouble(rectangle.yRange),
            )
        }.take(128).forEach { point ->
            val foundProjection = cubicBezierBinomial.projectPointIteratively(
                range = range,
                point = point,
                tolerance = tolerance,
            )

            val foundDistance = foundProjection?.distance ?: Double.MAX_VALUE

            val closerPoints = verificationLinSpace.generate().mapNotNull { t ->
                val otherPoint = cubicBezierBinomial.apply(t)

                val otherDistance = Vector2.distance(
                    otherPoint,
                    point,
                )

                when {
                    otherDistance < foundDistance && !otherDistance.equalsWithTolerance(
                        foundDistance,
                        tolerance = verificationTolerance,
                    ) -> CloserPoint(
                        t = t,
                        point = otherPoint,
                        distance = otherDistance,
                    )

                    else -> null
                }

            }.toSet()

            assertTrue(
                closerPoints.none { it.t in range },
            )
        }
    }

    @Test
    fun testFindDerivativeCurve_simple() {
        // A simple "smile" curve
        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(294.82501220703125, 235.177001953125),
            point1 = Vector2(361.5459899902344, 359.1940002441406),
            point2 = Vector2(595.7059936523438, 349.3630065917969),
            point3 = Vector2(646.1409912109375, 232.42100524902344),
        )

        val derivativeCurve: QuadraticBezierBinomial = cubicBezierBinomial.findDerivativeCurve()

        assertEqualsWithTolerance(
            expected = QuadraticBezierBinomial(
                point0 = Vector2(a0 = 200.16293334960938, a1 = 372.0509948730469),
                point1 = Vector2(a0 = 702.4800109863281, a1 = -29.49298095703125),
                point2 = Vector2(a0 = 151.30499267578125, a1 = -350.8260040283203),
            ),
            actual = derivativeCurve,
        )
    }

    /**
     * Two curves intersecting in an X-like way trimmed from the same
     * self-intersecting original curve
     */
    @Test
    fun testFindImage_xFromLoop() {
        val firstCubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(383.0995044708252, 275.80810546875),
            point1 = Vector2(435.23948860168457, 325.49310302734375),
            point2 = Vector2(510.3655261993408, 384.4371032714844),
            point3 = Vector2(614.6575183868408, 453.4740905761719),
        )

        val secondCubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(372.14351081848145, 439.6011047363281),
            point1 = Vector2(496.5914783477783, 370.8171081542969),
            point2 = Vector2(559.4554920196533, 307.91810607910156),
            point3 = Vector2(582.3854846954346, 253.8291015625),
        )

        // Currently, the accuracy is not impressive
        val tolerance = NumericTolerance.Relative(
            relativeTolerance = 0.01,
        )

        val image = assertNotNull(
            firstCubicBezierBinomial.findImage(
                target = secondCubicBezierBinomial,
                tolerance = tolerance,
            ),
        )

        assertTrue(
            image.overlap(
                source = firstCubicBezierBinomial,
                target = secondCubicBezierBinomial,
                tolerance = tolerance,
            ),
        )

        assertEqualsWithTolerance(
            expected = 5.295492,
            actual = image.t0,
        )

        assertEqualsWithTolerance(
            expected = 5.954922,
            actual = image.t1,
        )
    }

    /**
     * Two curves intersecting in an X-like way trimmed from the same
     * self-intersecting original curve, but one of the curve is moved by a
     * single unit on a single control point
     */
    @Test
    fun testFindImage_xFromLoop_alike() {
        val firstCubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(383.0995044708252, 275.80810546875),
            point1 = Vector2(435.23948860168457, 325.49310302734375),
            point2 = Vector2(510.3655261993408, 384.4371032714844),
            point3 = Vector2(614.6575183868408, 453.4740905761719),
        )

        val secondCubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(372.14351081848145, 439.6011047363281),
            point1 = Vector2(496.5914783477783, 370.8171081542969),
            point2 = Vector2(559.4554920196533, 307.91810607910156),
            point3 = Vector2(582.3854846954346, 254.8291015625),
        )

        val tolerance = NumericTolerance.Relative(
            relativeTolerance = 0.01,
        )

        assertNull(
            firstCubicBezierBinomial.findImage(
                target = secondCubicBezierBinomial,
                tolerance = tolerance,
            ),
        )
    }
}

private fun Random.nextDouble(
    range: ClosedFloatingPointRange<Double>,
): Double = this.nextDouble(
    range.start,
    range.endInclusive,
)
