package diy.lingerie.geometry

import diy.lingerie.geometry.curves.bezier.MonoBezierCurve
import diy.lingerie.geometry.splines.ClosedSpline
import diy.lingerie.simple_dom.SimpleUnit
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.simple_dom.svg.toSimplePath
import diy.lingerie.test_utils.assertEqualsWithTolerance
import diy.lingerie.test_utils.getResourceAsReader
import diy.lingerie.utils.alsoApply
import diy.lingerie.utils.xml.childElements
import diy.lingerie.utils.xml.svg.SVGDOMImplementationUtils
import diy.lingerie.utils.xml.svg.SVGViewBox
import diy.lingerie.utils.xml.svg.createSvgDocument
import diy.lingerie.utils.xml.svg.documentSvgElement
import diy.lingerie.utils.xml.svg.height
import diy.lingerie.utils.xml.svg.parseSvgDocument
import diy.lingerie.utils.xml.svg.viewBox
import diy.lingerie.utils.xml.svg.width
import diy.lingerie.utils.xml.writeToFile
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.svg.SVGPathElement
import kotlin.io.path.Path
import kotlin.test.Test

val svgDocumentFactory = SAXSVGDocumentFactory(null)
val svgDomImplementation: SVGDOMImplementation = SVGDOMImplementationUtils.getSVGDOMImplementation()

class ClosedSplineSvgTests {
    @Test
    fun toClosedSplineTest() {
        val reader = ClosedSplineSvgTests::class.java.getResourceAsReader("closedPath1.svg")!!
        val svgDocument = svgDocumentFactory.parseSvgDocument(reader = reader)
        val svgPathElement = svgDocument.documentSvgElement.childElements.single() as SVGPathElement

        val closedSpline = svgPathElement.toSimplePath().toClosedSpline()

        assertEqualsWithTolerance(
            expected = ClosedSpline(
                links = listOf(
                    ClosedSpline.Link(
                        start = Point(32.551998138427734, 125.20800018310547),
                        edge = MonoBezierCurve.Edge(
                            firstControl = Point(43.84199905395508, 108.19000244140625),
                            secondControl = Point(50.24800109863281, 84.95600128173828),
                        ),
                    ),
                    ClosedSpline.Link(
                        start = Point(65.63800048828125, 72.54399871826172),
                        edge = MonoBezierCurve.Edge(
                            firstControl = Point(82.27100372314453, 59.12900161743164),
                            secondControl = Point(108.29000091552734, 56.98699951171875),
                        ),
                    ),
                    ClosedSpline.Link(
                        start = Point(131.3769989013672, 50.821998596191406),
                        edge = MonoBezierCurve.Edge(
                            firstControl = Point(126.60900115966797, 85.4229965209961),
                            secondControl = Point(146.66000366210938, 103.48999786376953),
                        ),
                    ),
                    ClosedSpline.Link(
                        start = Point(181.7220001220703, 111.55500030517578),
                        edge = LineSegment.Edge,
                    ),
                    ClosedSpline.Link(
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
        val closedSpline = ClosedSpline(
            links = listOf(
                ClosedSpline.Link(
                    start = Point(32.551998138427734, 125.20800018310547),
                    edge = MonoBezierCurve.Edge(
                        firstControl = Point(43.84199905395508, 108.19000244140625),
                        secondControl = Point(50.24800109863281, 84.95600128173828),
                    ),
                ),
                ClosedSpline.Link(
                    start = Point(65.63800048828125, 72.54399871826172),
                    edge = MonoBezierCurve.Edge(
                        firstControl = Point(82.27100372314453, 59.12900161743164),
                        secondControl = Point(108.29000091552734, 56.98699951171875),
                    ),
                ),
                ClosedSpline.Link(
                    start = Point(131.3769989013672, 50.821998596191406),
                    edge = MonoBezierCurve.Edge(
                        firstControl = Point(126.60900115966797, 85.4229965209961),
                        secondControl = Point(146.66000366210938, 103.48999786376953),
                    ),
                ),
                ClosedSpline.Link(
                    start = Point(181.7220001220703, 111.55500030517578),
                    edge = LineSegment.Edge,
                ),
                ClosedSpline.Link(
                    start = Point(131.70599365234375, 177.86399841308594),
                    edge = MonoBezierCurve.Edge(
                        firstControl = Point(85.72000122070312, 174.21600341796875),
                        secondControl = Point(49.132999420166016, 160.46400451660156),
                    ),
                ),
            )
        )

        val svgRoot = SvgRoot(
            width = 256,
            height = 256,
            unit = SimpleUnit.pt,
            children = listOf(
                closedSpline.toSvgPathElement(),
            ),
        )

        val svgDocument = svgRoot.toSvgDocument(svgDomImplementation = svgDomImplementation)

        svgDocument.writeToFile(
            Path("../output/closedSpline.svg")
        )
    }
}
