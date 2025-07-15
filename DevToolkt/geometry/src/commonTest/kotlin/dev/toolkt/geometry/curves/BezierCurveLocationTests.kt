package dev.toolkt.geometry.curves

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.SpatialObject
import dev.toolkt.geometry.curves.OpenCurve.Coord
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull

class BezierCurveLocationTests {
    @Test
    @Ignore // FIXME: The inversion function for curves degenerating to a quadratic is built, but doesn't work
    fun testLocatePointByInversion_degenerate_quadratic_1() {
        val start = Point(277.26681060791014, 236.5185116577148)
        val end = Point(663.6991928100585, 231.08415603637695)

        // A simple "smile" curve, but degenerating to a quadratic curve
        val bezierCurve = BezierCurve(
            start = start,
            firstControl = Point(414.2205947875977, 355.1834526062012),
            secondControl = Point(543.0313888549805, 353.3735542297363),
            end = end,
        )

        // Sanity check
        assertEqualsWithTolerance(
            expected = bezierCurve.basisFunction,
            actual = bezierCurve.basisFunction.lower().raise(),
        )

        assertEqualsWithTolerance(
            expected = OpenCurve.Coord.start,
            actual = assertNotNull(
                bezierCurve.locatePointByInversion(
                    point = start,
                    tolerance = SpatialObject.SpatialTolerance.default,
                ),
            ),
            tolerance = NumericObject.Tolerance.Default,
        )

        assertEqualsWithTolerance(
            expected = OpenCurve.Coord.end,
            actual = assertNotNull(
                bezierCurve.locatePointByInversion(
                    point = end,
                    tolerance = SpatialObject.SpatialTolerance.default,
                ),
            ),
            tolerance = NumericObject.Tolerance.Default,
        )

        assertEqualsWithTolerance(
            expected = OpenCurve.Coord.of(0.5)!!,
            actual = assertNotNull(
                bezierCurve.locatePointByInversion(
                    // A point at the middle of the curve
                    point = Point(476.5902442932129, 324.15987846374514),
                    tolerance = SpatialObject.SpatialTolerance.default,
                ),
            ),
            tolerance = NumericObject.Tolerance.Default,
        )
    }

    @Test
    fun testLocatePointByInversion_simple_1() {
        val start = Point(294.82501220703125, 235.177001953125)
        val end = Point(646.1409912109375, 232.42100524902344)

        // A simple "smile" curve
        val bezierCurve = BezierCurve(
            start = start,
            firstControl = Point(361.5459899902344, 359.1940002441406),
            secondControl = Point(595.7059936523438, 349.3630065917969),
            end = end,
        )

        assertEqualsWithTolerance(
            expected = OpenCurve.Coord.start,
            actual = assertNotNull(
                bezierCurve.locatePointByInversion(
                    point = start,
                    tolerance = SpatialObject.SpatialTolerance.default,
                ),
            ),
            tolerance = NumericObject.Tolerance.Default,
        )

        assertEqualsWithTolerance(
            expected = OpenCurve.Coord.end,
            actual = assertNotNull(
                bezierCurve.locatePointByInversion(
                    point = end,
                    tolerance = SpatialObject.SpatialTolerance.default,
                ),
            ),
            tolerance = NumericObject.Tolerance.Default,
        )

        assertEqualsWithTolerance(
            expected = OpenCurve.Coord.of(0.5)!!,
            actual = assertNotNull(
                bezierCurve.locatePointByInversion(
                    // A point at the middle of the curve
                    point = Point(476.5902442932129, 324.1586284637451),
                    tolerance = SpatialObject.SpatialTolerance.default,
                ),
            ),
            tolerance = NumericObject.Tolerance.Default,
        )
    }
}