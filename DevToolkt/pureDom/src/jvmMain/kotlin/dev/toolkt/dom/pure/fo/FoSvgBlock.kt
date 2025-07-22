package dev.toolkt.dom.pure.fo

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.dom.pure.svg.PureSvgRoot
import org.w3c.dom.Document
import org.w3c.dom.Element

data class FoSvgBlock(
    val svgElement: PureSvgRoot,
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
        tolerance: NumericTolerance,
    ): Boolean = when {
        other !is FoSvgBlock -> false
        !svgElement.equalsWithTolerance(other.svgElement, tolerance) -> false
        else -> true
    }
}
