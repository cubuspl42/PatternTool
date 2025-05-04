package diy.lingerie.simple_dom

import diy.lingerie.math.algebra.NumericObject
import org.w3c.dom.Document
import org.w3c.dom.Element

abstract class SimpleElement : NumericObject {
    abstract fun toRawElement(
        document: Document,
    ): Element
}
