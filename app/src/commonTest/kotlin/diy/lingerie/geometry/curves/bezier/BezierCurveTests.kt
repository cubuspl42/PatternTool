package diy.lingerie.geometry.curves.bezier

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.math.algebra.NumericObject
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
            actual = bezierCurve.pathFunction.start,
        )

        val t0 = OpenCurve.Coord(t = 0.2)
        val p0 = Point(309.3747166748048, 317.28693518066416)

        assertEqualsWithTolerance(
            expected = p0,
            actual = bezierCurve.pathFunction.evaluate(coord = t0),
        )

        val t1 = OpenCurve.Coord(t = 0.4)
        val p1 = Point(341.0086751708984, 383.43413623046877)

        assertEqualsWithTolerance(
            expected = p1,
            actual = bezierCurve.pathFunction.evaluate(coord = t1),
        )

        val t2 = OpenCurve.Coord(t = 0.6)
        val p2 = Point(369.5635104980469, 385.17942395019537)

        assertEqualsWithTolerance(
            expected = p2,
            actual = bezierCurve.pathFunction.evaluate(coord = t2),
        )

        assertEqualsWithTolerance(
            expected = end,
            actual = bezierCurve.pathFunction.end,
        )
    }

    @Test
    fun testSplitAt() {
        val originalCurve = BezierCurve(
            start = Point(677.1705641585395, 615.752499524604),
            firstControl = Point(655.0464850886674, 157.66163210658488),
            secondControl = Point(406.05454162416845, 128.84363265872344),
            end = Point(321.3376393038343, 341.3936380645191),
        )

        val splitCurve0 = BezierCurve(
            start = Point(677.1705641585395, 615.752499524604),
            firstControl = Point(667.5507169938228, 416.5577304081198),
            secondControl = Point(615.0344199646079, 298.5320748564445),
            end = Point(551.779651601908, 246.22334843757017),
        )

        val splitCurve1 = BezierCurve(
            start = Point(551.779651601908, 246.22334843757017),
            firstControl = Point(469.56664612041277, 178.2382728472403),
            secondControl = Point(369.2163240151058, 221.2681598941017),
            end = Point(321.3376393038343, 341.3936380645191),
        )

        val (curve0, curve1) = originalCurve.splitAt(
            coord = OpenCurve.Coord(t = 0.43483),
        )

        val tolerance = NumericObject.Tolerance.Absolute(
            absoluteTolerance = 1e-2,
        )

        assertEqualsWithTolerance(
            expected = splitCurve0,
            actual = curve0, tolerance,
        )

        assertEqualsWithTolerance(
            expected = splitCurve1,
            actual = curve1, tolerance,
        )
    }
}
