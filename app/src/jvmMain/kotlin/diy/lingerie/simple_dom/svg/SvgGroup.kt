package diy.lingerie.simple_dom.svg

import diy.lingerie.algebra.Vector2
import diy.lingerie.geometry.Angle
import diy.lingerie.geometry.transformations.CombinedTransformation
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.xml.childElements
import diy.lingerie.utils.xml.svg.asList
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGTransform
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
    val svgTransforms = transformList.asList()

    val primitiveTransformations = svgTransforms.reversed().flatMap {
        Transformation.fromSvgTransform(it)
    }

    return CombinedTransformation(
        components = primitiveTransformations,
    )
}

fun Transformation.Companion.fromSvgTransform(
    transform: SVGTransform,
): List<PrimitiveTransformation> {
    val e = transform.matrix.e.toDouble()
    val f = transform.matrix.f.toDouble()

    val type = transform.type
    val angle = transform.angle.toDouble()

    val translation = PrimitiveTransformation.Translation(
        translationVector = Vector2(
            x = e,
            y = f,
        ),
    )

    return when (type) {
        SVGTransform.SVG_TRANSFORM_TRANSLATE -> listOf(translation)

        SVGTransform.SVG_TRANSFORM_ROTATE -> {
            val rotation = PrimitiveTransformation.Rotation(
                angle = Angle.ofDegrees(angle),
            )

            when {
                e == 0.0 && f == 0.0 -> listOf(rotation)
                else -> listOf(rotation, translation)
            }
        }

        else -> throw IllegalArgumentException("Unsupported transformation type: $type")
    }
}
