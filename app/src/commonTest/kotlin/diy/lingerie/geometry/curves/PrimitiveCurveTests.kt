package diy.lingerie.geometry.curves

import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.bezier.BezierCurve
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private data class ExpectedIntersection(
    val point: Point,
    val firstCoord: OpenCurve.Coord,
    val secondCoord: OpenCurve.Coord,
)

class PrimitiveCurveTests {
    @Test
    fun testFindIntersections_LineSegment_LineSegment_noIntersections_parallel() {
        val firstCurve = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(458.8733739397776, 334.2630461395838),
        )

        val secondCurve = LineSegment(
            start = Point(356.45270601450466, 503.7880075864232),
            end = Point(488.8733739397776, 364.2630461395838),
        )

        val intersections1 = firstCurve.findIntersections(
            objectCurve = secondCurve,
        )

        assertEquals(
            expected = 0,
            actual = intersections1.size,
        )

        val intersections2 = secondCurve.findIntersections(
            objectCurve = firstCurve,
        )

        assertEquals(
            expected = 0,
            actual = intersections2.size,
        )
    }

    @Test
    fun testFindIntersections_LineSegment_LineSegment_noIntersections_oneMissing() {
        val firstCurve = LineSegment(
            start = Point(476.7364224829216, 355.56234711426623),
            end = Point(610.9940051091198, 523.1189590812173),
        )

        val secondCurve = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(463.06982851106113, 271.56572992954716),
        )

        val intersections1 = firstCurve.findIntersections(
            objectCurve = secondCurve,
        )

        assertEquals(
            expected = 0,
            actual = intersections1.size,
        )

        val intersections2 = secondCurve.findIntersections(
            objectCurve = firstCurve,
        )

        assertEquals(
            expected = 0,
            actual = intersections2.size,
        )
    }

    @Test
    fun testFindIntersections_LineSegment_LineSegment_noIntersections_bothMissing() {
        val firstCurve = LineSegment(
            start = Point(575.9748502568091, 359.84856350754853),
            end = Point(610.9940051091198, 523.1189590812173),
        )

        val secondCurve = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(458.8733739397776, 334.2630461395838),
        )


        val intersections1 = firstCurve.findIntersections(
            objectCurve = secondCurve,
        )

        assertEquals(
            expected = 0,
            actual = intersections1.size,
        )

        val intersections2 = secondCurve.findIntersections(
            objectCurve = firstCurve,
        )

        assertEquals(
            expected = 0,
            actual = intersections2.size,
        )
    }

    @Test
    fun testFindIntersections_LineSegment_LineSegment_oneIntersection() {
        val firstCurve = LineSegment(
            start = Point(324.4306395542335, 242.63695647226996),
            end = Point(610.9940051091198, 523.1189590812173),
        )

        val secondCurve = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(463.06982851106113, 271.56572992954716),
        )

        val intersectionPoint = Point(419.650273, 335.835867)
        val firstIntersectionCoord = OpenCurve.Coord(t = 0.332281)
        val secondIntersectionCoord = OpenCurve.Coord(t = 0.682180)

        val singleIntersection1 = assertNotNull(
            firstCurve.findIntersections(
                objectCurve = secondCurve,
            ).singleOrNull(),
        )

        assertEqualsWithTolerance(
            expected = object : OpenCurve.Intersection() {
                override val point: Point = intersectionPoint

                override val subjectCoord: OpenCurve.Coord = firstIntersectionCoord

                override val objectCoord: OpenCurve.Coord = secondIntersectionCoord
            },
            actual = singleIntersection1,
        )

        val singleIntersection2 = assertNotNull(
            secondCurve.findIntersections(
                objectCurve = firstCurve,
            ).singleOrNull(),
        )

        assertEqualsWithTolerance(
            expected = object : OpenCurve.Intersection() {
                override val point: Point = intersectionPoint

                override val subjectCoord: OpenCurve.Coord = secondIntersectionCoord

                override val objectCoord: OpenCurve.Coord = firstIntersectionCoord
            },
            actual = singleIntersection2,
        )
    }

    @Test
    fun testFindIntersections_LineSegment_BezierCurve_noIntersections() {
        val firstCurve = LineSegment(
            start = Point(506.3340148925781, 185.1540069580078),
            end = Point(515.3410034179688, 470.50299072265625),
        )

        val secondCurve = BezierCurve(
            start = Point(378.7720947265625, 369.17799377441406),
            firstControl = Point(506.089111328125, 662.9309844970703),
            secondControl = Point(758.8061218261719, 543.6739959716797),
            end = Point(456.3341064453125, 661.4170074462891),
        )

        val intersections1 = firstCurve.findIntersections(
            objectCurve = secondCurve,
        )

        assertEquals(
            expected = 0,
            actual = intersections1.size,
        )

        val intersections2 = secondCurve.findIntersections(
            objectCurve = firstCurve,
        )

        assertEquals(
            expected = 0,
            actual = intersections2.size,
        )
    }

    @Test
    fun testFindIntersections_LineSegment_BezierCurve_oneIntersection() {
        val firstCurve = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(458.8733739397776, 334.2630461395838),
        )

        val secondCurve = BezierCurve(
            start = Point(233.924490, 500.813035),
            firstControl = Point(584.090705, 596.912517),
            secondControl = Point(479.786356, 425.215015),
            end = Point(321.3376393038343, 341.3936380645191),
        )

        val intersectionPoint = Point(401.375136, 394.846031)
        val firstIntersectionCoord = OpenCurve.Coord(t = 0.565791)
        val secondIntersectionCoord = OpenCurve.Coord(t = 0.814485)

        val singleIntersection1 = assertNotNull(
            firstCurve.findIntersections(
                objectCurve = secondCurve,
            ).singleOrNull(),
        )

        assertEqualsWithTolerance(
            expected = object : OpenCurve.Intersection() {
                override val point: Point = intersectionPoint

                override val subjectCoord: OpenCurve.Coord = firstIntersectionCoord

                override val objectCoord: OpenCurve.Coord = secondIntersectionCoord
            },
            actual = singleIntersection1,
        )

        val singleIntersection2 = assertNotNull(
            secondCurve.findIntersections(
                objectCurve = firstCurve,
            ).singleOrNull(),
        )

        assertEqualsWithTolerance(
            expected = object : OpenCurve.Intersection() {
                override val point: Point = intersectionPoint

                override val subjectCoord: OpenCurve.Coord = secondIntersectionCoord

                override val objectCoord: OpenCurve.Coord = firstIntersectionCoord
            },
            actual = singleIntersection2,
        )
    }

    @Test
    fun testFindIntersections_LineSegment_BezierCurve_threeIntersections() {
        val firstCurve = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(458.8733739397776, 334.2630461395838),
        )

        val secondCurve = BezierCurve(
            start = Point(269.9097848060901, 417.78063346119234),
            firstControl = Point(530.8903776607258, 532.365134061869),
            secondControl = Point(212.92561957028738, 357.7268375013964),
            end = Point(510.4081146117205, 360.39333834120225),
        )

        val intersectionPoint0 = Point(348.833366, 450.206637)
        val firstIntersectionCoord0 = OpenCurve.Coord(t = 0.169011)
        val secondIntersectionCoord0 = OpenCurve.Coord(t = 0.140208)

        val intersectionPoint1 = Point(374.210966, 423.467544)
        val firstIntersectionCoord1 = OpenCurve.Coord(t = 0.360656)
        val secondIntersectionCoord1 = OpenCurve.Coord(t = 0.542099)

        val intersectionPoint2 = Point(428.627571, 366.131519)
        val firstIntersectionCoord2 = OpenCurve.Coord(t = 0.771593)
        val secondIntersectionCoord2 = OpenCurve.Coord(t = 0.881570)

        val intersections1 = firstCurve.findIntersections(
            objectCurve = secondCurve,
        ).sortedBy { it.point.x }

        assertEqualsWithTolerance(
            expected = listOf(
                object : OpenCurve.Intersection() {
                    override val point: Point = intersectionPoint0

                    override val subjectCoord: OpenCurve.Coord = firstIntersectionCoord0

                    override val objectCoord: OpenCurve.Coord = secondIntersectionCoord0
                },
                object : OpenCurve.Intersection() {
                    override val point: Point = intersectionPoint1

                    override val subjectCoord: OpenCurve.Coord = firstIntersectionCoord1

                    override val objectCoord: OpenCurve.Coord = secondIntersectionCoord1
                },
                object : OpenCurve.Intersection() {
                    override val point: Point = intersectionPoint2

                    override val subjectCoord: OpenCurve.Coord = firstIntersectionCoord2

                    override val objectCoord: OpenCurve.Coord = secondIntersectionCoord2
                },
            ),
            actual = intersections1,
        )

        val intersections2 = secondCurve.findIntersections(
            objectCurve = firstCurve,
        ).sortedBy { it.point.x }

        assertEqualsWithTolerance(
            expected = listOf(
                object : OpenCurve.Intersection() {
                    override val point: Point = intersectionPoint0

                    override val subjectCoord: OpenCurve.Coord = secondIntersectionCoord0

                    override val objectCoord: OpenCurve.Coord = firstIntersectionCoord0
                },
                object : OpenCurve.Intersection() {
                    override val point: Point = intersectionPoint1

                    override val subjectCoord: OpenCurve.Coord = secondIntersectionCoord1

                    override val objectCoord: OpenCurve.Coord = firstIntersectionCoord1
                },
                object : OpenCurve.Intersection() {
                    override val point: Point = intersectionPoint2

                    override val subjectCoord: OpenCurve.Coord = secondIntersectionCoord2

                    override val objectCoord: OpenCurve.Coord = firstIntersectionCoord2
                },
            ),
            actual = intersections2,
        )
    }

    @Test
    fun testFindIntersections_BezierCurve_BezierCurve_nineIntersections() {
        val firstCurve = BezierCurve(
            start = Point(273.80049324035645, 489.08709716796875),
            firstControl = Point(1068.5394763946533, 253.16610717773438),
            secondControl = Point(-125.00849723815918, 252.71710205078125),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        val secondCurve = BezierCurve(
            start = Point(372.6355152130127, 191.58710479736328),
            firstControl = Point(496.35252571105957, 852.5531311035156),
            secondControl = Point(442.4235095977783, -54.72489929199219),
            end = Point(569.3854846954346, 487.569091796875),
        )

        val expectedIntersections = listOf(
            ExpectedIntersection(
                point = Point(400.0364120882783, 325.7513850441302),
                firstCoord = OpenCurve.Coord(t = 0.638175514633884),
                secondCoord = OpenCurve.Coord(t = 0.08321298331026583),
            ),
            ExpectedIntersection(
                point = Point(415.9864000101944, 388.18876477651054),
                firstCoord = OpenCurve.Coord(t = 0.8267616607759749),
                secondCoord = OpenCurve.Coord(t = 0.1435234395147944),
            ),
            ExpectedIntersection(
                point = Point(433.78055261270123, 434.84732656764527),
                firstCoord = OpenCurve.Coord(t = 0.08361584060373373),
                secondCoord = OpenCurve.Coord(t = 0.22787694792239874),
            ),
            ExpectedIntersection(
                point = Point(459.06587349145525, 424.28587808679634),
                firstCoord = OpenCurve.Coord(t = 0.10193180513525768),
                secondCoord = OpenCurve.Coord(t = 0.4025176966209491),
            ),
            ExpectedIntersection(
                point = Point(462.2096738267076, 414.2195778544469),
                firstCoord = OpenCurve.Coord(t = 0.8785849663620957),
                secondCoord = OpenCurve.Coord(t = 0.43011874468531425),
            ),
            ExpectedIntersection(
                point = Point(491.64500747999983, 312.8831093313188),
                firstCoord = OpenCurve.Coord(t = 0.46681258537835646),
                secondCoord = OpenCurve.Coord(t = 0.6822325289818921),
            ),
            ExpectedIntersection(
                point = Point(515.05453270079, 316.0676656534099),
                firstCoord = OpenCurve.Coord(t = 0.42505456557773347),
                secondCoord = OpenCurve.Coord(t = 0.8142156753154873),
            ),
            ExpectedIntersection(
                point = Point(540.6332704779081, 378.60112801527333),
                firstCoord = OpenCurve.Coord(t = 0.19350344745951725),
                secondCoord = OpenCurve.Coord(t = 0.9147383049342437),
            ),
            ExpectedIntersection(
                point = Point(561.4569242282377, 454.624570407085),
                firstCoord = OpenCurve.Coord(t = 0.9472752305695555),
                secondCoord = OpenCurve.Coord(t = 0.9785368635085525),
            ),
        )

        val intersections1 = firstCurve.findIntersections(
            objectCurve = secondCurve,
        ).sortedBy { it.point.x }

        assertEqualsWithTolerance(
            expected = expectedIntersections.map { intersection ->
                object : OpenCurve.Intersection() {
                    override val point: Point = intersection.point
                    override val subjectCoord: OpenCurve.Coord = intersection.firstCoord
                    override val objectCoord: OpenCurve.Coord = intersection.secondCoord
                }
            },
            actual = intersections1,
        )

        val intersections2 = secondCurve.findIntersections(
            objectCurve = firstCurve,
        ).sortedBy { it.point.x }

        assertEqualsWithTolerance(
            expected = expectedIntersections.map { intersection ->
                object : OpenCurve.Intersection() {
                    override val point: Point = intersection.point
                    override val subjectCoord: OpenCurve.Coord = intersection.secondCoord
                    override val objectCoord: OpenCurve.Coord = intersection.firstCoord
                }
            },
            actual = intersections2,
        )
    }

    @Test
    fun testFindIntersections_BezierCurve_BezierCurve_oneIntersection_overlapping() {
        // Although these curves look like two nearly-line-shaped innocent
        // curves crossing in the "X" shape, it's actually a single loop
        // curve cut to pieces

        val firstCurve = BezierCurve(
            start = Point(383.0995044708252, 275.80810546875),
            firstControl = Point(435.23948860168457, 325.49310302734375),
            secondControl = Point(510.3655261993408, 384.4371032714844),
            end = Point(614.6575183868408, 453.4740905761719),
        )

        val secondCurve = BezierCurve(
            start = Point(372.14351081848145, 439.6011047363281),
            firstControl = Point(496.5914783477783, 370.8171081542969),
            secondControl = Point(559.4554920196533, 307.91810607910156),
            end = Point(582.3854846954346, 253.8291015625),
        )

        val intersectionPoint = Point(488.177482, 364.171107)
        val firstIntersectionCoord = OpenCurve.Coord(t = 0.538009)
        val secondIntersectionCoord = OpenCurve.Coord(t = 0.378574)

        val singleIntersection1 = assertNotNull(
            firstCurve.findIntersections(
                objectCurve = secondCurve,
            ).singleOrNull(),
        )

        assertEqualsWithTolerance(
            expected = object : OpenCurve.Intersection() {
                override val point: Point = intersectionPoint

                override val subjectCoord: OpenCurve.Coord = firstIntersectionCoord

                override val objectCoord: OpenCurve.Coord = secondIntersectionCoord
            },
            actual = singleIntersection1,
        )

        val singleIntersection2 = assertNotNull(
            secondCurve.findIntersections(
                objectCurve = firstCurve,
            ).singleOrNull(),
        )

        assertEqualsWithTolerance(
            expected = object : OpenCurve.Intersection() {
                override val point: Point = intersectionPoint

                override val subjectCoord: OpenCurve.Coord = secondIntersectionCoord

                override val objectCoord: OpenCurve.Coord = firstIntersectionCoord
            },
            actual = singleIntersection2,
        )
    }
}

