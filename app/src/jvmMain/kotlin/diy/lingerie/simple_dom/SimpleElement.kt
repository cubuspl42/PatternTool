package diy.lingerie.simple_dom

import org.w3c.dom.Document
import org.w3c.dom.Element

abstract class SimpleElement {
    abstract fun toRawElement(
        document: Document,
    ): Element
}
