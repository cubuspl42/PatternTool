package diy.lingerie.geometry.curves

import diy.lingerie.geometry.Point
import diy.lingerie.test_utils.assertEqualsWithTolerance

internal data class ExpectedIntersection(
    val point: Point,
    val firstCoord: OpenCurve.Coord,
    val secondCoord: OpenCurve.Coord,
) {
    fun swap(): ExpectedIntersection = ExpectedIntersection(
        point = point,
        firstCoord = secondCoord,
        secondCoord = firstCoord,
    )
}

internal fun <CurveT : OpenCurve> testIntersectionsSymmetric(
    firstCurve: CurveT,
    secondCurve: CurveT,
    findIntersections: (CurveT, CurveT) -> Set<OpenCurve.Intersection>,
    expectedIntersection: List<ExpectedIntersection>,
) {
    val intersectionsOneWay = findIntersections(
        firstCurve,
        secondCurve,
    )

    assertIntersectionsEqual(
        expectedIntersections = expectedIntersection,
        actualIntersections = intersectionsOneWay,
    )

    val intersectionsOtherWay = findIntersections(
        secondCurve,
        firstCurve,
    )

    assertIntersectionsEqual(
        expectedIntersections = expectedIntersection.map {
            it.swap()
        },
        actualIntersections = intersectionsOtherWay,
    )
}

internal fun assertIntersectionsEqual(
    expectedIntersections: List<ExpectedIntersection>,
    actualIntersections: Set<OpenCurve.Intersection>,
) {
    assertEqualsWithTolerance(
        expected = expectedIntersections.map { intersection ->
            object : OpenCurve.Intersection() {
                override val point: Point = intersection.point
                override val subjectCoord: OpenCurve.Coord = intersection.firstCoord
                override val objectCoord: OpenCurve.Coord = intersection.secondCoord
            }
        },
        actual = actualIntersections.sortedBy { it.point.x },
    )
}
