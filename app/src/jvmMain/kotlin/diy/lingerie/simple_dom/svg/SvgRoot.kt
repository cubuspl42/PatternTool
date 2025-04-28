package diy.lingerie.simple_dom.svg

import diy.lingerie.simple_dom.SimpleDimension
import diy.lingerie.utils.xml.childElements
import diy.lingerie.utils.xml.svg.SVGDOMImplementationUtils
import diy.lingerie.utils.xml.svg.createSvgDocument
import diy.lingerie.utils.xml.svg.documentSvgElement
import diy.lingerie.utils.xml.writeToFile
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGDocument
import java.io.Reader
import java.nio.file.Path

data class SvgRoot(
    val children: List<SvgElement>,
    val width: SimpleDimension,
    val height: SimpleDimension,
    val viewBox: ViewBox = ViewBox(
        x = 0.0,
        y = 0.0,
        width = width.value,
        height = height.value,
    ),
) : SvgElement() {
    data class ViewBox(
        val x: Double,
        val y: Double,
        val width: Double,
        val height: Double,
    ) {
        fun toViewBoxString(): String = "$x $y $width $height"

        companion object {
            fun parse(string: String): ViewBox {
                val parts = string.split(" ")

                if (parts.size != 4) {
                    error("Invalid viewBox string: $string")
                }

                val (x, y, width, height) = parts

                return ViewBox(
                    x = x.toDouble(),
                    y = y.toDouble(),
                    width = width.toDouble(),
                    height = height.toDouble(),
                )
            }
        }
    }

    companion object {
        private val svgDocumentFactory = SAXSVGDocumentFactory(null)

        private val svgDomImplementation: SVGDOMImplementation = SVGDOMImplementationUtils.getSVGDOMImplementation()

        fun parse(
            reader: Reader,
        ): SvgRoot {
            val uri = "file://Document.svg"
            val svgDocument = svgDocumentFactory.createDocument(uri, reader) as SVGDocument
            return svgDocument.toSimple()
        }
    }

    fun writeToFile(
        filePath: Path,
    ) {
        toSvgDocument().writeToFile(
            filePath = filePath,
        )
    }

    private fun toSvgDocument(): SVGDocument = svgDomImplementation.createSvgDocument().apply {
        setup(
            document = this,
            root = documentSvgElement,
        )
    }

    private fun setup(
        document: Document,
        root: Element,
    ) {
        root.run {
            setAttribute("width", width.toDimensionString())
            setAttribute("height", height.toDimensionString())
            setAttribute("viewBox", viewBox.toViewBoxString())

            children.forEach { child ->
                appendChild(child.toRawElement(document = document))
            }
        }
    }

    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("svg").apply {
        setup(
            document = document,
            root = this,
        )
    }
}

fun SVGDocument.toSimple(): SvgRoot {
    val width = SimpleDimension.parse(documentSvgElement.getAttribute("width"))
    val height = SimpleDimension.parse(documentSvgElement.getAttribute("height"))
    val viewBox = SvgRoot.ViewBox.parse(documentSvgElement.getAttribute("viewBox"))

    return SvgRoot(
        width = width,
        height = height,
        viewBox = viewBox,
        children = documentSvgElement.childElements.mapNotNull {
            it.toSimpleElement()
        },
    )
}
