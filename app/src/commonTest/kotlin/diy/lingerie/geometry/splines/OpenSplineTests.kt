package diy.lingerie.geometry.splines

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.BezierCurve
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenSplineTests {
    @Test
    fun testPath() {
        val start = Point(272.7262878417969, 159.526123046875)

        val edge0 = BezierCurve.Edge(
            firstControl = Point(339.06092071533203, 513.923095703125),
            secondControl = Point(376.43798065185547, 373.21461486816406),
        )

        val joint0 = Point(425.1772003173828, 304.31105041503906)

        val edge1 = BezierCurve.Edge(
            firstControl = Point(456.03489112854004, 260.68693923950195),
            secondControl = Point(515.4947204589844, 215.17504119873047),
        )

        val joint1 = Point(563.93896484375, 241.8033905029297)

        val edge2 = BezierCurve.Edge(
            firstControl = Point(634.254035949707, 280.4534797668457),
            secondControl = Point(611.4755859375, 340.7131118774414),
        )

        val joint2 = Point(663.8353042602539, 387.37701416015625)

        val edge3 = BezierCurve.Edge(
            firstControl = Point(753.6513977050781, 467.42271423339844),
            secondControl = Point(864.3053665161133, 61.7249755859375),
        )

        val end = Point(832.1429672241211, 483.6293258666992)

        val openSpline = OpenSpline(
            firstCurve = edge0.bind(
                start,
                joint0,
            ),
            trailingSequentialLinks = listOf(
                Spline.Link(
                    edge = edge1,
                    end = joint1,
                ),
                Spline.Link(
                    edge = edge2,
                    end = joint2,
                ),
                Spline.Link(
                    edge = edge3,
                    end = end,
                ),
            ),
        )

        assertEquals(
            expected = start,
            actual = openSpline.pathFunction.start,
        )

        assertEqualsWithTolerance(
            expected = Point(309.3747166748048, 317.28693518066416),
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 0.2 / 4.0),
            ),
        )

        assertEquals(
            expected = joint0,
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 0.25),
            ),
        )

        assertEqualsWithTolerance(
            expected = Point(459.6020209140777, 266.53840482711786),
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 1.3 / 4.0),
            ),
        )

        assertEquals(
            expected = joint1,
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 0.5),
            ),
        )

        assertEqualsWithTolerance(
            expected = Point(614.3989881591797, 296.3029407348632),
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 2.4 / 4.0),
            ),
        )

        assertEquals(
            expected = joint2,
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 0.75),
            ),
        )

        assertEqualsWithTolerance(
            expected = Point(793.7310705184937, 307.3061761856079),
            actual = openSpline.pathFunction.evaluate(
                coord = OpenCurve.Coord(t = 3.5 / 4.0),
            ),
        )

        assertEquals(
            expected = end,
            actual = openSpline.pathFunction.end,
        )
    }
}
