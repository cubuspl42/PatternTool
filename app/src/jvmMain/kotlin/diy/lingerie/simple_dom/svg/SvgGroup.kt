package diy.lingerie.simple_dom.svg

import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.geometry.transformations.Transformation
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import diy.lingerie.utils.xml.childElements
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGTransformList

data class SvgGroup(
    val id: String? = null,
    val transformation: Transformation? = null,
    val children: List<SvgGraphicsElements>,
) : SvgGraphicsElements() {
    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("g").apply {
        id?.let {
            setAttribute("id", it)
        }

        transformation?.let {
            val value = it.toSvgTransformationString()
            setAttribute("transform", value)
        }

        children.forEach { child ->
            appendChild(child.toRawElement(document = document))
        }
    }

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is SvgGroup -> false
        id != other.id -> false
        transformation != other.transformation -> false
        children.size != other.children.size -> false
        !children.equalsWithTolerance(other.children, tolerance) -> false
        else -> true
    }

    override fun flatten(
        baseTransformation: Transformation,
    ): List<SvgShape> {
        val newTransformation = baseTransformation.combineWith(
            this.transformation ?: Transformation.Identity,
        )

        return children.flatMap {
            it.flatten(baseTransformation = newTransformation)
        }
    }
}

fun SVGGElement.toSimpleGroup(): SvgGroup = SvgGroup(
    id = id,
    transformation = Transformation.fromSvgTransformList(
        transformList = transform.baseVal,
    ),
    children = childElements.mapNotNull { it.toSvgGraphicsElements() },
)

fun Transformation.Companion.fromSvgTransformList(
    transformList: SVGTransformList,
): Transformation {
    val consolidatedMatrix = transformList.consolidate().matrix

    return PrimitiveTransformation.Universal(
        a = consolidatedMatrix.a.toDouble(),
        b = consolidatedMatrix.b.toDouble(),
        c = consolidatedMatrix.c.toDouble(),
        d = consolidatedMatrix.d.toDouble(),
        tx = consolidatedMatrix.e.toDouble(),
        ty = consolidatedMatrix.f.toDouble(),
    )
}
