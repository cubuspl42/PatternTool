package dev.toolkt.dom.pure.svg

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.geometry.transformations.Transformation
import org.w3c.dom.Document
import org.w3c.dom.Element

data class PureSvgGroup(
    val id: String? = null,
    val transformation: Transformation? = null,
    val children: List<PureSvgGraphicsElement>,
) : PureSvgGraphicsElement() {
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
        other !is PureSvgGroup -> false
        id != other.id -> false
        transformation != other.transformation -> false
        children.size != other.children.size -> false
        !children.equalsWithTolerance(other.children, tolerance) -> false
        else -> true
    }

    override fun flatten(
        baseTransformation: Transformation,
    ): List<PureSvgShape> {
        val newTransformation = baseTransformation.combineWith(
            this.transformation ?: Transformation.Identity,
        )

        return children.flatMap {
            it.flatten(baseTransformation = newTransformation)
        }
    }
}
