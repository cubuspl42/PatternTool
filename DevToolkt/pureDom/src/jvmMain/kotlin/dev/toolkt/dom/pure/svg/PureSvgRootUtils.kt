package dev.toolkt.dom.pure.svg

import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.dom.pure.utils.xml.childElements
import dev.toolkt.dom.pure.utils.xml.getAttributeOrNull
import dev.toolkt.dom.pure.utils.xml.svg.MinimalCssContext
import dev.toolkt.dom.pure.utils.xml.svg.SVGDOMImplementationUtils
import dev.toolkt.dom.pure.utils.xml.svg.createSvgDocument
import dev.toolkt.dom.pure.utils.xml.svg.documentSvgElement
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.apache.batik.anim.dom.SVGOMDocument
import org.w3c.dom.svg.SVGDocument
import java.io.Reader

private val svgDocumentFactory = SAXSVGDocumentFactory(null)

private val svgDomImplementation: SVGDOMImplementation = SVGDOMImplementationUtils.getSVGDOMImplementation()

fun PureSvgRoot.Companion.parse(
    reader: Reader,
): PureSvgRoot {
    val uri = "file://Document.svg"

    val document = svgDocumentFactory.createDocument(uri, reader) as SVGOMDocument

    document.cssEngine = svgDomImplementation.createCSSEngine(document, MinimalCssContext())

    return document.toPure()
}

fun PureSvgRoot.toSvgDocument(): SVGDocument = svgDomImplementation.createSvgDocument().apply {
    setup(
        document = this,
        root = documentSvgElement,
    )
}

fun SVGDocument.toPure(): PureSvgRoot {
    val widthString =
        documentSvgElement.getAttributeOrNull("width") ?: throw IllegalArgumentException("Width is not set")
    val heightString =
        documentSvgElement.getAttributeOrNull("height") ?: throw IllegalArgumentException("Height is not set")

    val width = PureDimension.parse(widthString)
    val height = PureDimension.parse(heightString)

    val viewBox = documentSvgElement.getAttributeOrNull("viewBox")?.let { viewBoxString ->
        PureSvgRoot.ViewBox.parse(viewBoxString)
    }

    return PureSvgRoot(
        width = width,
        height = height,
        viewBox = viewBox,
        graphicsElements = documentSvgElement.childElements.mapNotNull {
            it.toSvgGraphicsElements()
        },
    )
}
