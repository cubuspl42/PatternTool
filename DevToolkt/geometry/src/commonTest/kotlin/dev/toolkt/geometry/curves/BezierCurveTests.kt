package dev.toolkt.geometry.curves

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Rectangle
import kotlin.test.Test
import kotlin.test.assertTrue

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

    @Test
    fun testFindBoundingBox() {
        val bezierCurve = BezierCurve(
            start = Point(273.80049324035645, 489.08709716796875),
            firstControl = Point(1068.5394763946533, 253.16610717773438),
            secondControl = Point(-125.00849723815918, 252.71710205078125),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        val boundingBox = bezierCurve.findBoundingBox()

        assertEqualsWithTolerance(
            actual = boundingBox,
            expected = Rectangle(
                origin = Point(273.80049324035645, 312.1176405539444),
                width = 397.6180114746094,
                height = 178.08746808863373,
            ),
        )
    }

    @Test
    fun testTrim() {
        val bezierCurve = BezierCurve(
            start = Point(233.92449010844575, 500.813035986871),
            firstControl = Point(863.426829231712, 303.18800785949134),
            secondControl = Point(53.73076075494464, 164.97814335091425),
            end = Point(551.3035908506827, 559.7310384198445),
        )

        val startCoord = OpenCurve.Coord.of(t = 0.2)!!
        val endCoord = OpenCurve.Coord.of(t = 0.8)!!

        val subCurve = bezierCurve.trim(
            coordRange = startCoord..endCoord,
        )

        assertEqualsWithTolerance(
            expected = bezierCurve.evaluate(startCoord),
            actual = subCurve.start,
        )

        assertEqualsWithTolerance(
            expected = bezierCurve.evaluate(endCoord),
            actual = subCurve.end,
        )

        subCurve.basisFunction.sample(16).forEach { sample ->
            assertTrue(
                bezierCurve.containsPoint(
                    Point(pointVector = sample.point),
                ),
            )
        }
    }
}
