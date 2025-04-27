package diy.lingerie.geometry

import diy.lingerie.test_utils.getResourceAsReader
import diy.lingerie.utils.xml.childElements
import diy.lingerie.utils.xml.svg.documentSvgElement
import diy.lingerie.utils.xml.svg.parseSvgDocument
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.w3c.dom.svg.SVGPathElement
import kotlin.test.Test

val svgDocumentFactory = SAXSVGDocumentFactory(null)

class ClosedSplineSvgTests {
    @Test
    fun toClosedSplineTest() {
        val reader = ClosedSplineSvgTests::class.java.getResourceAsReader("closedPath1.svg")!!
        val svgDocument = svgDocumentFactory.parseSvgDocument(reader = reader)
        val svgPathElement = svgDocument.documentSvgElement.childElements.single() as SVGPathElement

        val closedSpline = svgPathElement.toClosedSpline()
    }
}
