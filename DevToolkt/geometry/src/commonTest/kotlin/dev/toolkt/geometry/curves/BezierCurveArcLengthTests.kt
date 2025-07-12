package dev.toolkt.geometry.curves

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.geometry.Point
import kotlin.test.Test
import kotlin.test.assertNotNull

class BezierCurveArcLengthTests {
    private val arcLengthVerificationTolerance = NumericObject.Tolerance.Relative(
        // 0.5 %
        relativeTolerance = 0.005,
    )

    private val arcLengthLocationTolerance = NumericObject.Tolerance.Absolute(
        absoluteTolerance = 1e-3,
    )

    @Test
    fun testArcLength_1() {
        val bezierCurve = BezierCurve(
            start = Point(135.79737730246416, 439.6728622250703),
            firstControl = Point(978.1268209204347, 41.5872294779947),
            secondControl = Point(172.74402314583494, 556.2205326485109),
            end = Point(816.1805894519998, 252.34111123735875),
        )

        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    @Test
    fun testArcLength_2() {
        val bezierCurve = BezierCurve(
            start = Point(214.4750734145391, 580.1582697318154),
            firstControl = Point(459.06654332802054, 410.5514167308793),
            secondControl = Point(494.8961346271062, 709.426164755474),
            end = Point(493.69262277805865, 493.46849289090096),
        )


        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    @Test
    fun testArcLength_3() {
        val bezierCurve = BezierCurve(
            start = Point(115.117470341419, 240.87622627478413),
            firstControl = Point(394.08377235035323, 288.222150751084),
            secondControl = Point(386.29593303733736, 113.4945184882381),
            end = Point(121.65293879167893, 286.28303378710916),
        )

        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    @Test
    fun testArcLength_4() {
        val bezierCurve = BezierCurve(
            start = Point(530.4940814557458, 241.67258884541297),
            firstControl = Point(961.5421048139829, 329.8998894073666),
            secondControl = Point(236.94848087916398, 6.644895194057426),
            end = Point(476.141458980871, 199.36056001431643),
        )

        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    @Test
    fun testArcLength_5() {
        val bezierCurve = BezierCurve(
            start = Point(545.4055741418306, 371.4355422090739),
            firstControl = Point(1039.2189040443727, 552.7224200932396),
            secondControl = Point(204.85189832277592, 301.5071262030401),
            end = Point(622.1634621546991, 544.4475351119727),
        )

        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    @Test
    fun testArcLength_6() {
        val bezierCurve = BezierCurve(
            start = Point(770.4512185977392, 659.8671607807919),
            firstControl = Point(423.98515008070535, 350.565207789301),
            secondControl = Point(748.4946922271342, 426.77022558430326),
            end = Point(424.87641671667825, 682.1364161784095),
        )

        testPrimaryArcLength(
            bezierCurve = bezierCurve,
        )

        testPartialArcLength(
            bezierCurve = bezierCurve,
            endCoord = OpenCurve.Coord.of(t = 0.5)!!,
        )
    }

    private fun testPrimaryArcLength(
        bezierCurve: BezierCurve,
    ) {
        val expectedArcLength = bezierCurve.basisFunction.primaryArcLengthNearlyExact

        val actualArcLength = bezierCurve.totalArcLength

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = actualArcLength,
            tolerance = arcLengthVerificationTolerance,
        )

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = bezierCurve.calculateArcLengthUpTo(
                endCoord = OpenCurve.Coord.end,
            ),
            tolerance = arcLengthVerificationTolerance,
        )

        val locatedCoord = assertNotNull(
            bezierCurve.locateArcLength(
                arcLength = actualArcLength,
                tolerance = NumericObject.Tolerance.Absolute(
                    absoluteTolerance = 1e-2,
                ),
            ),
        )

        assertEqualsWithTolerance(
            expected = OpenCurve.Coord.end,
            actual = locatedCoord,
            tolerance = arcLengthLocationTolerance,
        )
    }

    private fun testPartialArcLength(
        bezierCurve: BezierCurve,
        endCoord: OpenCurve.Coord,
    ) {
        val expectedArcLength = bezierCurve.basisFunction.calculatePrimaryArcLengthBruteForce(
            range = 0.0..endCoord.t,
        )

        val actualArcLength = bezierCurve.calculateArcLengthUpTo(
            endCoord = endCoord,
        )

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = actualArcLength,
            tolerance = arcLengthVerificationTolerance,
        )

        assertEqualsWithTolerance(
            expected = expectedArcLength,
            actual = bezierCurve.trimTo(endCoord = endCoord).totalArcLength,
            tolerance = arcLengthVerificationTolerance,
        )

        val locatedCoord = assertNotNull(
            bezierCurve.locateArcLength(
                arcLength = actualArcLength,
                tolerance = arcLengthLocationTolerance,
            ),
        )

        assertEqualsWithTolerance(
            expected = endCoord,
            actual = locatedCoord,
            tolerance = arcLengthLocationTolerance,
        )
    }
}
