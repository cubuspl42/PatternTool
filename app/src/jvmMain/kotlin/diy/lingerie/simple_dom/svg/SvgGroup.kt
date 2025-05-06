package diy.lingerie.simple_dom.svg

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.geometry.transformations.CombinedTransformation
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.xml.childElements
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGTransformList

data class SvgGroup(
    val id: String? = null,
    val transformation: Transformation? = null,
    val children: List<SvgElement>,
) : SvgElement() {
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
    transformation = CombinedTransformation.fromSvgTransformList(
        transformList = transform.baseVal,
    ),
    children = childElements.mapNotNull { it.toSimpleElement() },
)

fun CombinedTransformation.Companion.fromSvgTransformList(
    transformList: SVGTransformList,
): CombinedTransformation {
    val consolidatedMatrix = transformList.consolidate().matrix

    val r11 = consolidatedMatrix.a.toDouble()
    val r21 = consolidatedMatrix.b.toDouble()
    val r12 = consolidatedMatrix.c.toDouble()
    val r22 = consolidatedMatrix.d.toDouble()

    val tx = consolidatedMatrix.e.toDouble()
    val ty = consolidatedMatrix.f.toDouble()

    if (r11 != r22 || r12 != -r21) {
        throw IllegalArgumentException("Unsupported transformation matrix: $r11, $r12, $r21, $r22, $tx, $ty")
    }

    val cosFi = r11
    val sinFi = r21

    return CombinedTransformation(
        standaloneTransformations = listOf(
            PrimitiveTransformation.Rotation.trigonometric(
                cosFi = cosFi,
                sinFi = sinFi,
            ),
            PrimitiveTransformation.Translation(
                tx = tx,
                ty = ty,
            ),
        ),
    )
}
