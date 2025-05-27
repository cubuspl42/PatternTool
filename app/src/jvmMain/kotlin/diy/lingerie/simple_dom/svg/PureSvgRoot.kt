package diy.lingerie.simple_dom.svg

import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.numeric.equalsWithToleranceOrNull
import dev.toolkt.dom.pure.PureDimension
import diy.lingerie.utils.xml.childElements
import diy.lingerie.utils.xml.getAttributeOrNull
import diy.lingerie.utils.xml.svg.MinimalCssContext
import diy.lingerie.utils.xml.svg.SVGDOMImplementationUtils
import diy.lingerie.utils.xml.svg.createSvgDocument
import diy.lingerie.utils.xml.svg.documentSvgElement
import diy.lingerie.utils.xml.writeToFile
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.apache.batik.anim.dom.SVGOMDocument
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGDocument
import java.io.Reader
import java.nio.file.Path

data class PureSvgRoot(
    val viewBox: ViewBox? = null,
    val defs: List<PureSvgDef> = emptyList(),
    val width: PureDimension<*>,
    val height: PureDimension<*>,
    val graphicsElements: List<PureSvgGraphicsElement>,
) : PureSvgElement() {
    data class ViewBox(
        val x: Double,
        val y: Double,
        val width: Double,
        val height: Double,
    ) : NumericObject {
        fun toViewBoxString(): String = "$x $y $width $height"

        override fun equalsWithTolerance(
            other: NumericObject, tolerance: NumericObject.Tolerance
        ): Boolean = when {
            other !is ViewBox -> false
            !x.equalsWithTolerance(other.x, tolerance) -> false
            !y.equalsWithTolerance(other.y, tolerance) -> false
            !width.equalsWithTolerance(other.width, tolerance) -> false
            !height.equalsWithTolerance(other.height, tolerance) -> false
            else -> true
        }

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
        ): PureSvgRoot {
            val uri = "file://Document.svg"

            val document = svgDocumentFactory.createDocument(uri, reader) as SVGOMDocument

            document.cssEngine = svgDomImplementation.createCSSEngine(document, MinimalCssContext())

            return document.toSimple()
        }
    }

    val effectiveViewBox: ViewBox
        get() = viewBox ?: ViewBox(
            x = 0.0,
            y = 0.0,
            width = width.value,
            height = height.value,
        )

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

    private fun toDefsElement(
        document: Document,
    ): Element = document.createSvgElement("defs").apply {
        defs.forEach { def ->
            appendChild(def.toRawElement(document = document))
        }
    }

    private fun setup(
        document: Document,
        root: Element,
    ) {
        val viewBox = this.viewBox

        root.run {
            setAttribute("width", width.toDimensionString())
            setAttribute("height", height.toDimensionString())

            if (viewBox != null) {
                setAttribute("viewBox", viewBox.toViewBoxString())
            }

            appendChild(
                toDefsElement(document = document)
            )

            graphicsElements.forEach { child ->
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

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean {
        return when {
            other !is PureSvgRoot -> false
            !width.equalsWithTolerance(other.width, tolerance) -> false
            !height.equalsWithTolerance(other.height, tolerance) -> false
            !viewBox.equalsWithToleranceOrNull(other.viewBox, tolerance) -> false
            else -> true
        }
    }

    fun flatten(
        baseTransformation: Transformation,
    ): List<PureSvgShape> = graphicsElements.flatMap {
        it.flatten(baseTransformation = baseTransformation)
    }
}

fun SVGDocument.toSimple(): PureSvgRoot {
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
