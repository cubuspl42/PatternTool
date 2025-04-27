package diy.lingerie.simple_dom.fo

import diy.lingerie.simple_dom.SimpleElement
import org.apache.fop.fo.FOElementMapping
import org.w3c.dom.Document
import org.w3c.dom.Element

abstract class FoElement : SimpleElement() {
    companion object {
        const val FO_NS = FOElementMapping.URI
    }

    protected fun Document.createFoElement(
        name: String,
    ): Element = createElementNS(FO_NS, "fo:$name")
}
