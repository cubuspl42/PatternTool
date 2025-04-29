package diy.lingerie.simple_dom.fo

import diy.lingerie.algebra.NumericObject
import diy.lingerie.simple_dom.svg.SvgRoot
import org.w3c.dom.Document
import org.w3c.dom.Element

data class FoSvgBlock(
    val svgElement: SvgRoot,
) : FoElement() {
    override fun toRawElement(
        document: Document,
    ): Element = document.createFoElement("block").apply {
        setAttribute("break-after", "page")

        appendChild(
            document.createFoElement("instream-foreign-object").apply {
                appendChild(
                    svgElement.toRawElement(document = document),
                )
            },
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is FoSvgBlock -> false
        !svgElement.equalsWithTolerance(other.svgElement, tolerance) -> false
        else -> true
    }
}
