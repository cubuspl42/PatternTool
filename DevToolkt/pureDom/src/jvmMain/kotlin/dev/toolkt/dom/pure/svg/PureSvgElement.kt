package dev.toolkt.dom.pure.svg

import dev.toolkt.dom.pure.PureElement
import org.w3c.dom.Document
import org.w3c.dom.Element

abstract class PureSvgElement : PureElement() {


    protected fun Document.createSvgElement(
        name: String,
    ): Element = createElementNS(PureSvg.Namespace, "svg:$name")
}
