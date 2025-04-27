package diy.lingerie.pattern_tool

import diy.lingerie.geometry.toSvgPathElement
import diy.lingerie.utils.alsoApply
import diy.lingerie.utils.xml.svg.SVGViewBox
import diy.lingerie.utils.xml.svg.createSvgDocument
import diy.lingerie.utils.xml.svg.documentSvgElement
import diy.lingerie.utils.xml.svg.height
import diy.lingerie.utils.xml.svg.viewBox
import diy.lingerie.utils.xml.svg.width
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.svg.SVGDocument

fun Outline.dumpSvg(
    svgDomImplementation: SVGDOMImplementation,
): SVGDocument = svgDomImplementation.createSvgDocument().alsoApply { document ->
    documentSvgElement.apply {
        width = "100%"
        height = "100%"

        viewBox = SVGViewBox(
            xMin = 0.0,
            yMin = 0.0,
            width = 256.0,
            height = 256.0,
        )

        appendChild(
            innerSpline.toSvgPathElement(document = document),
        )
    }
}
