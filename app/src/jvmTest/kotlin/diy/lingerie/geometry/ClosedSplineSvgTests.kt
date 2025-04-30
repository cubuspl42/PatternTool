package diy.lingerie.geometry

import diy.lingerie.geometry.curves.bezier.MonoBezierCurve
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.geometry.splines.SplineLink
import diy.lingerie.simple_dom.pt
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.test_utils.assertEqualsWithTolerance
import diy.lingerie.utils.getResourceAsReader
import kotlin.io.path.Path
import kotlin.test.Test

class ClosedSplineSvgTests {
    @Test
    fun toClosedSplineTest() {
        val reader = ClosedSplineSvgTests::class.java.getResourceAsReader("closedPath1.svg")!!
        val svgRoot = SvgRoot.parse(reader = reader)
        val svgPath = svgRoot.children.single() as SvgPath

        val closedSpline = svgPath.toClosedSpline()

        assertEqualsWithTolerance(
            expected = ClosedSpline.positionallyContinuous(
                links = listOf(
                    SplineLink(
                        start = Point(32.551998138427734, 125.20800018310547),
                        edge = MonoBezierCurve.Edge(
                            firstControl = Point(43.84199905395508, 108.19000244140625),
                            secondControl = Point(50.24800109863281, 84.95600128173828),
                        ),
                    ),
                    SplineLink(
                        start = Point(65.63800048828125, 72.54399871826172),
                        edge = MonoBezierCurve.Edge(
                            firstControl = Point(82.27100372314453, 59.12900161743164),
                            secondControl = Point(108.29000091552734, 56.98699951171875),
                        ),
                    ),
                    SplineLink(
                        start = Point(131.3769989013672, 50.821998596191406),
                        edge = MonoBezierCurve.Edge(
                            firstControl = Point(126.60900115966797, 85.4229965209961),
                            secondControl = Point(146.66000366210938, 103.48999786376953),
                        ),
                    ),
                    SplineLink(
                        start = Point(181.7220001220703, 111.55500030517578),
                        edge = LineSegment.Edge,
                    ),
                    SplineLink(
                        start = Point(131.70599365234375, 177.86399841308594),
                        edge = MonoBezierCurve.Edge(
                            firstControl = Point(85.72000122070312, 174.21600341796875),
                            secondControl = Point(49.132999420166016, 160.46400451660156),
                        ),
                    ),
                )
            ),
            actual = closedSpline,
        )
    }

    @Test
    fun toSvgPathElementTest() {
        val closedSpline = ClosedSpline.positionallyContinuous(
            links = listOf(
                SplineLink(
                    start = Point(32.551998138427734, 125.20800018310547),
                    edge = MonoBezierCurve.Edge(
                        firstControl = Point(43.84199905395508, 108.19000244140625),
                        secondControl = Point(50.24800109863281, 84.95600128173828),
                    ),
                ),
                SplineLink(
                    start = Point(65.63800048828125, 72.54399871826172),
                    edge = MonoBezierCurve.Edge(
                        firstControl = Point(82.27100372314453, 59.12900161743164),
                        secondControl = Point(108.29000091552734, 56.98699951171875),
                    ),
                ),
                SplineLink(
                    start = Point(131.3769989013672, 50.821998596191406),
                    edge = MonoBezierCurve.Edge(
                        firstControl = Point(126.60900115966797, 85.4229965209961),
                        secondControl = Point(146.66000366210938, 103.48999786376953),
                    ),
                ),
                SplineLink(
                    start = Point(181.7220001220703, 111.55500030517578),
                    edge = LineSegment.Edge,
                ),
                SplineLink(
                    start = Point(131.70599365234375, 177.86399841308594),
                    edge = MonoBezierCurve.Edge(
                        firstControl = Point(85.72000122070312, 174.21600341796875),
                        secondControl = Point(49.132999420166016, 160.46400451660156),
                    ),
                ),
            )
        )

        val svgRoot = SvgRoot(
            width = 256.pt,
            height = 256.pt,
            children = listOf(
                closedSpline.toSvgPath(),
            ),
        )

        svgRoot.writeToFile(
            Path("../output/closedSpline.svg")
        )
    }
}
