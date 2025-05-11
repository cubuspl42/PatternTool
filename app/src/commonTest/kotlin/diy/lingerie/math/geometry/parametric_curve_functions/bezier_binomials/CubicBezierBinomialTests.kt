package diy.lingerie.math.geometry.parametric_curve_functions.bezier_binomials

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.BezierCurve
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertNull

class CubicBezierBinomialTests {
    @Test
    fun testLocatePoint() {
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
                cubicBezierBinomial.locatePoint(
                    point = sample.point,
                    tolerance = NumericObject.Tolerance.Default,
                ) ?: throw AssertionError("Cannot find t for point ${sample.point}")
            },
        )

        // A good approximation of the self intersection t-value
        val selfIntersectionT = 0.8083924553357065

        // An acceptable approximation of the self intersection point
        val selfIntersectionPoint0 = Vector2(501.14313780321595, 374.2020798247014)

        // For an acceptable (but not good) approximation of the self-intersection point, the located t-value is very good
        assertEqualsWithTolerance(
            expected = selfIntersectionT,
            actual = cubicBezierBinomial.locatePoint(
                point = selfIntersectionPoint0,
                tolerance = NumericObject.Tolerance.Default,
            ) ?: throw AssertionError("Cannot find t for the self-intersection point #0: $selfIntersectionPoint0"),
        )

        // A good approximation of the self intersection point
        val selfIntersectionPoint1 = Vector2(501.14355433959827, 374.2024184921395)

        // For some good approximations of the self-intersection point, the t-value can't be located
        assertNull(
            actual = cubicBezierBinomial.locatePoint(
                point = selfIntersectionPoint1,
                tolerance = NumericObject.Tolerance.Default,
            ),
        )

        // Another good approximation of the self intersection point
        val selfIntersectionPoint2 = Vector2(501.1438111319996, 374.2024184921395)

        // For some other good approximations of the self-intersection point, the t-value is extremely off
        assertEqualsWithTolerance(
            expected = -5.68379446238774,
            actual = cubicBezierBinomial.locatePoint(
                point = selfIntersectionPoint2,
                tolerance = NumericObject.Tolerance.Default,
            )
                ?: throw AssertionError("Cannot find t for the self-intersection point point #2: $selfIntersectionPoint2"),
        )
    }

    @Test
    fun testProjectPoint() {

    }

    @Test
    fun testSolveIntersections_cubicCurve() {

    }

    @Test
    fun testSolveIntersections_line() {

    }
}
