package diy.lingerie.simple_dom.svg

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.Size
import dev.toolkt.core.numeric.NumericObject
import org.w3c.dom.Document
import org.w3c.dom.Element

data class SvgMarker(
    val id: String,
    val size: Size,
    val ref: Point,
    val path: SvgPath,
) : SvgDef() {
    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("marker").apply {
        setAttribute("id", id)
        setAttribute("markerWidth", size.width.value.toString())
        setAttribute("markerHeight", size.height.value.toString())
        setAttribute("refX", ref.x.toString())
        setAttribute("refY", ref.y.toString())
        setAttribute("orient", "auto")
        setAttribute("fill", "context-stroke")

        appendChild(path.toRawElement(document = document))
    }

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is SvgMarker -> false
        id != other.id -> false
        size != other.size -> false
        ref != other.ref -> false
        path != other.path -> false
        else -> true
    }
}
