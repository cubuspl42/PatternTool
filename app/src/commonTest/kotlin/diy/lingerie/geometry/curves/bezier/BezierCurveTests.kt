package diy.lingerie.geometry.curves.bezier

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test

class BezierCurveTests {
    @Test
    fun testPath() {
        val start = Point(272.7262878417969, 159.526123046875)
        val end = Point(425.1772003173828, 304.31105041503906)

        val bezierCurve = BezierCurve(
            start = start,
            firstControl = Point(339.06092071533203, 513.923095703125),
            secondControl = Point(376.43798065185547, 373.21461486816406),
            end = end,
        )

        assertEqualsWithTolerance(
            expected = start,
            actual = bezierCurve.path.start,
        )

        assertEqualsWithTolerance(
            expected = Point(309.3747166748048, 317.28693518066416),
            actual = bezierCurve.path.evaluate(
                coord = OpenCurve.Coord(t = 0.2),
            ),
        )

        assertEqualsWithTolerance(
            expected = Point(341.0086751708984, 383.43413623046877),
            actual = bezierCurve.path.evaluate(
                coord = OpenCurve.Coord(t = 0.4),
            ),
        )

        assertEqualsWithTolerance(
            expected = Point(369.5635104980469, 385.17942395019537),
            actual = bezierCurve.path.evaluate(
                coord = OpenCurve.Coord(t = 0.6),
            ),
        )

        assertEqualsWithTolerance(
            expected = end,
            actual = bezierCurve.path.end,
        )
    }
}
