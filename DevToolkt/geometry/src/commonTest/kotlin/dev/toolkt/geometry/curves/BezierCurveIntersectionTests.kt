package dev.toolkt.geometry.curves

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Span
import dev.toolkt.geometry.SpatialObject
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BezierCurveIntersectionTests {
    @Test
    fun testFindIntersections_LineSegment_BezierCurve_noIntersections() {
        val lineSegment = LineSegment(
            start = Point(506.3340148925781, 185.1540069580078),
            end = Point(515.3410034179688, 470.50299072265625),
        )

        val bezierCurve = BezierCurve(
            start = Point(378.7720947265625, 369.17799377441406),
            firstControl = Point(506.089111328125, 662.9309844970703),
            secondControl = Point(758.8061218261719, 543.6739959716797),
            end = Point(456.3341064453125, 661.4170074462891),
        )

        assertIntersectionsEqual(
            expectedIntersections = emptyList(),
            actualIntersections = BezierCurve.Companion.findIntersections(
                subjectLineSegment = lineSegment,
                objectBezierCurve = bezierCurve,
            ),
        )
    }

    @Test
    fun testFindIntersections_LineSegment_BezierCurve_oneIntersection() {
        val lineSegment = LineSegment(
            start = Point(326.45270601450466, 473.7880075864232),
            end = Point(458.8733739397776, 334.2630461395838),
        )

        val bezierCurve = BezierCurve(
            start = Point(233.924490, 500.813035),
            firstControl = Point(584.090705, 596.912517),
            secondControl = Point(479.786356, 425.215015),
            end = Point(321.3376393038343, 341.3936380645191),
        )

        assertIntersectionsEqual(
            expectedIntersections = listOf(
                ExpectedIntersection(
                    point = Point(401.375136, 394.846031),
                    firstCoord = OpenCurve.Coord(t = 0.565791),
                    secondCoord = OpenCurve.Coord(t = 0.814485),
                ),
            ),
            actualIntersections = BezierCurve.Companion.findIntersections(
                subjectLineSegment = lineSegment,
                objectBezierCurve = bezierCurve,
            ),
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

        assertIntersectionsEqual(
            expectedIntersections = listOf(
                ExpectedIntersection(
                    point = Point(348.833366, 450.206637),
                    firstCoord = OpenCurve.Coord(t = 0.169011),
                    secondCoord = OpenCurve.Coord(t = 0.140208),
                ),
                ExpectedIntersection(
                    point = Point(374.210966, 423.467544),
                    firstCoord = OpenCurve.Coord(t = 0.360656),
                    secondCoord = OpenCurve.Coord(t = 0.542099),
                ),
                ExpectedIntersection(
                    point = Point(428.627571, 366.131519),
                    firstCoord = OpenCurve.Coord(t = 0.771593),
                    secondCoord = OpenCurve.Coord(t = 0.881570),
                ),
            ),
            actualIntersections = BezierCurve.Companion.findIntersections(
                subjectLineSegment = firstCurve,
                objectBezierCurve = secondCurve,
            ),
        )
    }

    @Test
    @Ignore // FIXME: Figure out the issues with locatePoint
    fun testFindIntersections_LineSegment_BezierCurve_oneIntersection_splitLoop() {
        val lineSegment = LineSegment(
            start = Point(401.14355433959827, 374.2024184921395),
            end = Point(601.1435543395982, 374.2024184921395),
        )

        // Part of a loop
        val bezierCurve = BezierCurve(
            start = Point(492.59773540496826, 197.3452272415161),
            firstControl = Point(393.3277416229248, 180.14210319519043),
            secondControl = Point(287.3950023651123, 260.3726043701172),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        assertIntersectionsEqual(
            expectedIntersections = listOf(
                ExpectedIntersection(
                    point = Point(501.14355433959827, 374.2024184921395),
                    firstCoord = OpenCurve.Coord(t = 0.2606471534818411),
                    secondCoord = OpenCurve.Coord(t = 0.8083924553357065),
                ),
            ),
            actualIntersections = BezierCurve.Companion.findIntersections(
                subjectLineSegment = lineSegment,
                objectBezierCurve = bezierCurve,
            ),
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

        testBezierIntersectionsConsistentSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            expectedIntersection = listOf(
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
            ),
        )
    }

    @Test
    @Ignore // FIXME: Figure out how things _should_ behave
    // This test started failing with the simplified numeric equation solving
    fun testFindIntersections_BezierCurve_BezierCurve_oneIntersection_overlapping() {
        // Although these curves look like two nearly-line-shaped innocent
        // curves crossing in the "X" shape, it's actually a single loop
        // curve cut to pieces

        val firstBezierCurve = BezierCurve(
            start = Point(383.0995044708252, 275.80810546875),
            firstControl = Point(435.23948860168457, 325.49310302734375),
            secondControl = Point(510.3655261993408, 384.4371032714844),
            end = Point(614.6575183868408, 453.4740905761719),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(372.14351081848145, 439.6011047363281),
            firstControl = Point(496.5914783477783, 370.8171081542969),
            secondControl = Point(559.4554920196533, 307.91810607910156),
            end = Point(582.3854846954346, 253.8291015625),
        )

        // It's not clear why this test cases succeeds and the next one (similar) fails
        testBezierIntersectionsConsistentSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    point = Point(488.177482, 364.171107),
                    secondCoord = OpenCurve.Coord(t = 0.378574),
                    firstCoord = OpenCurve.Coord(t = 0.538009),
                ),
            ),
        )
    }

    @Test
    fun testFindIntersections_BezierCurve_BezierCurve_oneIntersection_splitLoop() {
        // A loop split at its top

        val firstBezierCurve = BezierCurve(
            start = Point(273.80049324035645, 489.08709716796875),
            firstControl = Point(684.4749774932861, 329.1851005554199),
            secondControl = Point(591.8677291870117, 214.5483512878418),
            end = Point(492.59773540496826, 197.3452272415161),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(492.59773540496826, 197.3452272415161),
            firstControl = Point(393.3277416229248, 180.14210319519043),
            secondControl = Point(287.3950023651123, 260.3726043701172),
            end = Point(671.4185047149658, 490.2051086425781),
        )

        testBezierIntersectionsBySubdivisionSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    point = Point(492.59773540496826, 197.3452272415161),
                    firstCoord = OpenCurve.Coord(t = 0.9999999925494194),
                    secondCoord = OpenCurve.Coord(t = 0.0),
                ),
                ExpectedIntersection(
                    point = Point(501.14355433959827, 374.2024184921395),
                    firstCoord = OpenCurve.Coord(t = 0.2606471534818411),
                    secondCoord = OpenCurve.Coord(t = 0.8083924553357065),
                ),
            ),
        )

        testBezierIntersectionsByEquationSolvingSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                // FIXME: At least one intersection should be found, but none are
//                ExpectedIntersection(
//                    firstCoord = OpenCurve.Coord(t = 0.0),
//                    point = Point(501.579334, 374.596689),
//                    secondCoord = OpenCurve.Coord(t = 0.0),
//                ),
            ),
        )
    }

    @Test
    @Ignore // FIXME: Figure out how things _should_ behave
    // This test started failing with the simplified numeric equation solving
    fun testFindIntersections_BezierCurve_BezierCurve_oneIntersection_cutLoop() {
        // A loop cut into two pieces that make it non-obvious that any loop
        // is involved at all (for some reason, possibly numeric accuracy, this
        // one is not problematic!)

        val firstBezierCurve = BezierCurve(
            start = Point(247.45586850992547, 379.490073683598),
            firstControl = Point(422.61086805841114, 396.6670752291757),
            secondControl = Point(531.4859546756852, 386.71814287026064),
            end = Point(594.0656015814893, 364.6746085219802),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(452.41959820093143, 239.38755149520694),
            firstControl = Point(410.63096772289646, 281.7264423034185),
            secondControl = Point(385.13020832675465, 365.70689316897005),
            end = Point(405.2940882855255, 513.4262225999319),
        )

        testBezierIntersectionsBySubdivisionSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    // A reasonable approximation of the intersection point
                    point = Point(398.9586117177181, 388.1522281575668),
                    firstCoord = OpenCurve.Coord(t = 0.326171875),
                    secondCoord = OpenCurve.Coord(t = 0.671875),
                ),
            ),
        )

        testBezierIntersectionsByEquationSolvingSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    // A slightly different but also reasonable approximation
                    point = Point(399.1359546266406, 388.1678196955365),
                    firstCoord = OpenCurve.Coord(t = 0.32803571347803123),
                    secondCoord = OpenCurve.Coord(t = 0.672939435717393),
                ),
            ),
        )
    }

    @Test
    fun testFindIntersections_BezierCurve_BezierCurve_oneIntersection_anotherCutLoop() {
        // A loop cut into two pieces that make it non-obvious that any loop
        // is involved at all (for some reason, possibly numeric accuracy, this
        // one IS problematic and confuses the equation solving algorithm)

        // The original loop curve:
        // start = Point(233.92449010844575, 500.813035986871),
        // firstControl = Point(863.426829231712, 303.18800785949134),
        // secondControl = Point(53.73076075494464, 164.97814335091425),
        // end = Point(551.3035908506827, 559.7310384198445),

        val firstBezierCurve = BezierCurve(
            start = Point(233.92449010844575, 500.813035986871),
            firstControl = Point(422.77519184542564, 441.5255275486571),
            secondControl = Point(482.0980368984025, 387.5853838361354),
            end = Point(486.0476425340348, 351.778389940191),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(382.2960291124364, 335.5675928528492),
            firstControl = Point(370.41409366476535, 370.845949740462),
            secondControl = Point(402.03174182196125, 441.30516989916543),
            end = Point(551.3035908506827, 559.7310384198445),
        )

        val expectedIntersections = listOf(
            ExpectedIntersection(
                // This is a reasonable approximation of the intersection point
                point = Point(413.8638152871538, 426.9971560440854),
                firstCoord = OpenCurve.Coord(t = 0.438232421875),
                secondCoord = OpenCurve.Coord(t = 0.5462646484375),
            ),
        )

        testBezierIntersectionsVariousSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedDefaultIntersections = expectedIntersections,
            expectedSubdivisionIntersections = expectedIntersections,
            expectedEquationSolvingIntersections = emptyList(),
        )
    }

    @Test
    fun testFindIntersections_BezierCurve_BezierCurve_threeIntersections_c_loop() {
        // A simple C-shaped curve and a loop. For an unknown reason, this case
        // might be problematic (possibly uncovering a big problem in the lower layer)

        val firstBezierCurve = BezierCurve(
            start = Point(1547.0, 893.0),
            firstControl = Point(964.0, 592.0),
            secondControl = Point(1044.0, 207.0),
            end = Point(1808.0, 680.0),
        )

        val secondBezierCurve = BezierCurve(
            start = Point(1407.0, 904.0),
            firstControl = Point(2176.0, 201.0),
            secondControl = Point(1018.0, 402.0),
            end = Point(1707.0, 855.0),
        )

        testBezierIntersectionsBySubdivisionSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    point = Point(1465.981292217617, 848.2089843881924),
                    firstCoord = OpenCurve.Coord(t = 0.04904937744140625),
                    secondCoord = OpenCurve.Coord(t = 0.02740478515625),
                ),
                ExpectedIntersection(
                    point = Point(1491.6693825254533, 514.1636085670052),
                    firstCoord = OpenCurve.Coord(t = 0.838714599609375),
                    secondCoord = OpenCurve.Coord(t = 0.6728515625),
                ),
                ExpectedIntersection(
                    point = Point(1662.9276703424082, 595.8206819867323),
                    firstCoord = OpenCurve.Coord(t = 0.9326324462890625),
                    secondCoord = OpenCurve.Coord(t = 0.190765380859375),
                ),
            ),
        )

        testBezierIntersectionsByEquationSolvingSymmetric(
            firstCurve = firstBezierCurve,
            secondCurve = secondBezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    point = Point(1465.981292217617, 848.2089843881924),
                    firstCoord = OpenCurve.Coord(t = 0.04904937744140625),
                    secondCoord = OpenCurve.Coord(t = 0.02740478515625),
                ),
                ExpectedIntersection(
                    point = Point(1491.6693825254533, 514.1636085670052),
                    firstCoord = OpenCurve.Coord(t = 0.838714599609375),
                    secondCoord = OpenCurve.Coord(t = 0.6728515625),
                ),
                ExpectedIntersection(
                    point = Point(1662.9276703424082, 595.8206819867323),
                    firstCoord = OpenCurve.Coord(t = 0.9326324462890625),
                    secondCoord = OpenCurve.Coord(t = 0.190765380859375),
                ),
            ),
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 1e-2,
            ),
        )
    }

    @Test
    @Ignore // FIXME: Fix various issues with self-intersection
    fun testFindIntersections_BezierCurve_oneSelfIntersection() {
        // A loop
        val bezierCurve = BezierCurve(
            start = Point(233.92449010844575, 500.813035986871),
            firstControl = Point(863.426829231712, 303.18800785949134),
            secondControl = Point(53.73076075494464, 164.97814335091425),
            end = Point(551.3035908506827, 559.7310384198445),
        )

        // Correct values
        val expectedIntersectionPoint = Point(413.87430209404283, 426.9901974419915)
        val expectedTValue1 = 0.131531503613082
        val expectedTValue2 = 0.8639172755496

        // FIXME: Multum intersection points are found, as a curve has infinite
        //        common points with itself (maybe it makes sense?)
        testBezierIntersectionsBySubdivisionSymmetric(
            firstCurve = bezierCurve, secondCurve = bezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    point = expectedIntersectionPoint,
                    firstCoord = OpenCurve.Coord(t = expectedTValue1),
                    secondCoord = OpenCurve.Coord(t = expectedTValue2),
                ),
            ),
        )

        // FIXME: One intersection is found, it's the point on curve, but
        //        otherwise its position is seemingly random
        testBezierIntersectionsByEquationSolvingSymmetric(
            firstCurve = bezierCurve,
            secondCurve = bezierCurve,
            expectedIntersection = listOf(
                ExpectedIntersection(
                    point = expectedIntersectionPoint,
                    firstCoord = OpenCurve.Coord(t = expectedTValue1),
                    secondCoord = OpenCurve.Coord(t = expectedTValue2),
                ),
            ),
        )
    }

    internal fun testBezierIntersectionsConsistentSymmetric(
        firstCurve: BezierCurve,
        secondCurve: BezierCurve,
        expectedIntersection: List<ExpectedIntersection>,
    ) {
        testBezierIntersectionsByEquationSolvingSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            expectedIntersection = expectedIntersection,
        )

        testBezierIntersectionsBySubdivisionSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            expectedIntersection = expectedIntersection,
        )
    }

    private fun testBezierIntersectionsVariousSymmetric(
        firstCurve: BezierCurve,
        secondCurve: BezierCurve,
        expectedDefaultIntersections: List<ExpectedIntersection>,
        expectedEquationSolvingIntersections: List<ExpectedIntersection>,
        expectedSubdivisionIntersections: List<ExpectedIntersection>,
    ) {
        val numericObjectToleranceAbsolute = NumericObject.Tolerance.Absolute(
            absoluteTolerance = 1e-4,
        )

        val spatialTolerance = SpatialObject.SpatialTolerance(
            spanTolerance = Span.of(value = 0.1),
        )

        testIntersectionsSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            findIntersections = { firstBezierCurve, secondBezierCurve ->
                BezierCurve.findIntersections(
                    subjectBezierCurve = firstBezierCurve,
                    objectBezierCurve = secondBezierCurve,
                    tolerance = spatialTolerance,
                )
            },
            expectedIntersections = expectedDefaultIntersections,
            tolerance = numericObjectToleranceAbsolute,
        )

        testIntersectionsSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            findIntersections = { firstBezierCurve, secondBezierCurve ->
                BezierCurve.findIntersectionsByEquationSolving(
                    subjectBezierCurve = firstBezierCurve,
                    objectBezierCurve = secondBezierCurve,
                )
            },
            expectedIntersections = expectedEquationSolvingIntersections,
            tolerance = numericObjectToleranceAbsolute,
        )


        testIntersectionsSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            findIntersections = { firstBezierCurve, secondBezierCurve ->
                BezierCurve.findIntersectionsBySubdivision(
                    subjectBezierCurve = firstBezierCurve,
                    objectBezierCurve = secondBezierCurve,
                    tolerance = spatialTolerance,
                )
            },
            expectedIntersections = expectedSubdivisionIntersections,
            tolerance = numericObjectToleranceAbsolute,
        )
    }

    private fun testBezierIntersectionsByEquationSolvingSymmetric(
        firstCurve: BezierCurve,
        secondCurve: BezierCurve,
        expectedIntersection: List<ExpectedIntersection>,
        tolerance: NumericObject.Tolerance.Absolute = NumericObject.Tolerance.Absolute(
            absoluteTolerance = 1e-2,
        ),
    ) {
        testIntersectionsSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            findIntersections = { firstBezierCurve, secondBezierCurve ->
                BezierCurve.findIntersectionsByEquationSolving(
                    subjectBezierCurve = firstBezierCurve,
                    objectBezierCurve = secondBezierCurve,
                )
            },
            expectedIntersections = expectedIntersection,
            tolerance = tolerance,
        )
    }

    private fun testBezierIntersectionsBySubdivisionSymmetric(
        firstCurve: BezierCurve,
        secondCurve: BezierCurve,
        expectedIntersection: List<ExpectedIntersection>,
    ) {
        testIntersectionsSymmetric(
            firstCurve = firstCurve,
            secondCurve = secondCurve,
            findIntersections = { firstBezierCurve, secondBezierCurve ->
                BezierCurve.findIntersectionsBySubdivision(
                    subjectBezierCurve = firstBezierCurve,
                    objectBezierCurve = secondBezierCurve,
                    tolerance = SpatialObject.SpatialTolerance(
                        spanTolerance = Span.of(value = 0.1),
                    ),
                )
            },
            expectedIntersections = expectedIntersection,
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 0.1,
            ),
        )
    }
}
