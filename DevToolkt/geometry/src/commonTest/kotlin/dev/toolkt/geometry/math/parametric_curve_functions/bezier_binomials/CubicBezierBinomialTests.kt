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
    fun testLocatePointByInversion() {
        val tolerance = NumericTolerance.Absolute.Default

        val looseTolerance = NumericTolerance.Absolute(absoluteTolerance = 1e-3)

        // A curve with a self-intersection (outside its [0, 1] range!)
        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(a0 = 492.59773540496826, a1 = 197.3452272415161),
            point1 = Vector2(a0 = 393.3277416229248, a1 = 180.14210319519043),
            point2 = Vector2(a0 = 287.3950023651123, a1 = 260.3726043701172),
            point3 = Vector2(a0 = 671.4185047149658, a1 = 490.2051086425781)
        )

        val selfIntersectionResult = assertNotNull(
            cubicBezierBinomial.findSelfIntersection(tolerance = tolerance),
        )

        assertEqualsWithTolerance(
            expected = -0.7393528461432413,
            actual = selfIntersectionResult.t0,
        )

        assertEqualsWithTolerance(
            expected = 0.8083924555183848,
            actual = selfIntersectionResult.t1,
        )

        val selfIntersectionPoint = cubicBezierBinomial.apply(selfIntersectionResult.t0)

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

        assertEqualsWithTolerance(
            expected = selfIntersectionPoint,
            actual = selfIntersectionPoint0,
            tolerance = looseTolerance,
        )

        // Location works fine
        testCorrectPointLocation(
            cubicBezierBinomial = cubicBezierBinomial,
            point = selfIntersectionPoint0,
        )

        // A good approximation of the self intersection point, extremely close to the self-intersection
        // Distance to the curve: 2.965446658236352E-8
        // Distance to the self-intersection point: 1.2517558197816879E-7
        // The ratio denominator is _slightly_ above the tolerance threshold (~6e-6)
        val selfIntersectionPoint1 = Vector2(501.14355433959827, 374.2024184921395)

        assertEqualsWithTolerance(
            expected = selfIntersectionPoint,
            actual = selfIntersectionPoint1,
            tolerance = looseTolerance,
        )

        assertNull(
            actual = cubicBezierBinomial.locatePointByInversion(
                point = selfIntersectionPoint1,
            ),
        )

        // Another good approximation of the self intersection point, very close to the self-intersection
        // Distance to the curve: 1.5141784118304349E-4 (Isn't this too far? It's more than the tolerance)
        // Distance to the self-intersection point: 2.5690823589665505E-4
        val selfIntersectionPoint2 = Vector2(501.1438111319996, 374.2024184921395)

        assertNull(
            cubicBezierBinomial.locatePointByInversion(
                point = selfIntersectionPoint2,
            ),
        )
    }

    @Test
    fun testBuildInvertedRationalFunction() {
        val cubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(a0 = 492.59773540496826, a1 = 197.3452272415161),
            point1 = Vector2(a0 = 393.3277416229248, a1 = 180.14210319519043),
            point2 = Vector2(a0 = 287.3950023651123, a1 = 260.3726043701172),
            point3 = Vector2(a0 = 671.4185047149658, a1 = 490.2051086425781)
        )

        val invertedPolynomial = assertNotNull(
            cubicBezierBinomial.buildInvertedRationalFunction(),
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
    fun testFindImage_xFromLoop1() {
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
     * self-intersecting original curve
     */
    @Test
    fun testFindImage_xFromLoop2() {
        val tolerance = NumericTolerance.Relative(
            relativeTolerance = 1e-12,
        )

        val firstCubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(a0 = 233.92449010844575, a1 = 500.813035986871),
            point1 = Vector2(a0 = 422.77519184542564, a1 = 441.5255275486571),
            point2 = Vector2(a0 = 482.0980368984025, a1 = 387.5853838361354),
            point3 = Vector2(a0 = 486.0476425340348, a1 = 351.778389940191),
        )

        val secondCubicBezierBinomial = CubicBezierBinomial(
            point0 = Vector2(a0 = 382.2960291124364, a1 = 335.5675928528492),
            point1 = Vector2(a0 = 370.41409366476535, a1 = 370.845949740462),
            point2 = Vector2(a0 = 402.03174182196125, a1 = 441.30516989916543),
            point3 = Vector2(a0 = 551.3035908506827, a1 = 559.7310384198445),
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
            expected = -2.3333333333333437,
            actual = image.t0,
        )

        assertEqualsWithTolerance(
            expected = -1.3333333333333333,
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
