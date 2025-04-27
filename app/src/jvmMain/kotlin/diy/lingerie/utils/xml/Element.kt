package diy.lingerie.utils.xml

import diy.lingerie.utils.xml.svg.asList
import org.w3c.dom.Element

val Element.childElements: List<Element>
    get() = this.childNodes.asList().filterIsInstance<Element>()
