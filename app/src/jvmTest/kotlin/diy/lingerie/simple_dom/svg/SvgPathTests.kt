package diy.lingerie.simple_dom.svg

import dev.toolkt.geometry.Point
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.dom.pure.PureColor
import diy.lingerie.utils.xml.childElements
import diy.lingerie.utils.xml.svg.SVGDOMImplementationUtils
import diy.lingerie.utils.xml.svg.documentSvgElement
import diy.lingerie.utils.xml.svg.parseSvgDocument
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.svg.SVGPathElement
import kotlin.test.Test

class SvgPathTests {
    companion object {
        private val svgDocumentFactory = SAXSVGDocumentFactory(null)

        private val svgDomImplementation: SVGDOMImplementation = SVGDOMImplementationUtils.getSVGDOMImplementation()

        private fun parseSvgPath(
            pathString: String,
        ): SVGPathElement {
            val documentString = """
                <svg xmlns="http://www.w3.org/2000/svg">
                    $pathString
                </svg>
            """.trimIndent()

            val svgDocument = svgDocumentFactory.parseSvgDocument(
                svgDomImplementation = svgDomImplementation,
                reader = documentString.reader(),
            )

            val pathElement = svgDocument.documentSvgElement.childElements.singleOrNull() as? SVGPathElement
                ?: throw IllegalStateException("Expected a single SVGPathElement, but found ${svgDocument.documentSvgElement.childElements.size}")

            return pathElement
        }
    }

    @Test
    fun testToSimplePath_basic() {
        val pathElement = parseSvgPath(
            pathString = """
                <path
                    d="M12.3 45.6 L78.9 12.3 C34.5 67.8, 90.1 23.4, 56.7 89.0 Z"
                    stroke="red"
                    stroke-width="2"
                    stroke-dasharray="5, 2"
                />
            """.trimIndent(),
        )

        val path = pathElement.toSimplePath()

        val expectedPath = SvgPath(
            stroke = SvgShape.Stroke(
                color = PureColor.red,
                width = 2.0,
                dashArray = listOf(5.0, 2.0),
            ),
            segments = listOf(
                SvgPath.Segment.MoveTo(
                    targetPoint = Point(x = 12.3, y = 45.6),
                ),
                SvgPath.Segment.LineTo(
                    finalPoint = Point(x = 78.9, y = 12.3),
                ),
                SvgPath.Segment.CubicBezierCurveTo(
                    controlPoint1 = Point(x = 34.5, y = 67.8),
                    controlPoint2 = Point(x = 90.1, y = 23.4),
                    finalPoint = Point(x = 56.7, y = 89.0),
                ),
                SvgPath.Segment.ClosePath,
            ),
        )

        assertEqualsWithTolerance(
            expected = expectedPath,
            actual = path,
        )
    }
}
