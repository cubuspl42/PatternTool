package diy.lingerie.simple_dom.fo

import diy.lingerie.simple_dom.SimpleUnit
import diy.lingerie.simple_dom.svg.SvgElement
import org.apache.fop.util.XMLConstants
import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

data class FoRoot(
    val pageWidth: Int,
    val pageHeight: Int,
    val pageUnit: SimpleUnit,
    val blocks: List<FoSvgBlock>,
) : FoElement() {
    companion object {
        private const val XMLNS_NS = XMLConstants.XMLNS_NAMESPACE_URI

        private const val MASTER_NAME = "A4"
    }

    fun toDocument(): Document {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
        }

        val builder = factory.newDocumentBuilder()

        return builder.newDocument().apply {
            appendChild(
                toRawElement(document = this),
            )
        }
    }

    override fun toRawElement(
        document: Document,
    ): Element = document.createFoElement("root").apply {
        setAttributeNS(XMLNS_NS, "xmlns:svg", SvgElement.SVG_NS)

        appendChild(
            document.createFoElement("layout-master-set").apply {
                appendChild(
                    document.createFoElement("simple-page-master").apply {
                        setAttribute("master-name", MASTER_NAME)
                        setAttribute("page-width", "${pageWidth}${pageUnit.suffix}")
                        setAttribute("page-height", "${pageHeight}${pageUnit.suffix}")

                        appendChild(
                            document.createFoElement("region-body").apply {
                                setAttribute("margin", "0")
                            },
                        )
                    },
                )
            },
        )

        appendChild(
            document.createFoElement("page-sequence").apply {
                setAttribute("master-reference", MASTER_NAME)

                appendChild(
                    document.createFoElement("flow").apply {
                        setAttribute("flow-name", "xsl-region-body")

                        blocks.forEach { block ->
                            appendChild(block.toRawElement(document = document))

                        }
                    },
                )
            },
        )
    }
}
