package diy.lingerie.simple_dom.svg

import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.math.algebra.equalsWithToleranceOrNull
import diy.lingerie.simple_dom.SimpleColor
import org.w3c.dom.Element

abstract class SvgShape : SvgElement() {
    data class Stroke(
        val color: SimpleColor,
        val width: Double,
        val dashArray: List<Double>? = null,
    ) : NumericObject {
        companion object {
            val default = Stroke(
                color = SimpleColor.Companion.black,
                width = 1.0,
            )
        }

        fun toDashArrayString(): String? = dashArray?.joinToString(" ") { it.toString() }

        override fun equalsWithTolerance(
            other: NumericObject, tolerance: NumericObject.Tolerance
        ): Boolean = when {
            other !is Stroke -> false
            color != other.color -> false
            !width.equalsWithTolerance(other.width, tolerance) -> false
            !dashArray.equalsWithToleranceOrNull(other.dashArray, tolerance) -> false
            else -> true
        }
    }

    data class Fill(
        val color: SimpleColor,
    ) : NumericObject {
        companion object {
            val default = Fill(
                color = SimpleColor.black,
            )
        }

        override fun equalsWithTolerance(
            other: NumericObject, tolerance: NumericObject.Tolerance
        ): Boolean = when {
            other !is Fill -> false
            color != other.color -> false
            else -> true
        }
    }


    final override fun flatten(
        baseTransformation: Transformation,
    ): List<SvgShape> = listOf(
        transformVia(transformation = baseTransformation),
    )

    protected fun setupRawShape(
        element: Element,
    ): Element {
        val fill = this.fill
        val stroke = this.stroke

        return element.apply {
            when {
                fill != null -> {
                    setAttribute("fill", fill.color.toHexString())
                }

                else -> {
                    setAttribute("fill", "none")
                }
            }

            when {
                stroke != null -> {
                    setAttribute("stroke", stroke.color.toHexString())
                    setAttribute("stroke-width", stroke.width.toString())

                    stroke.toDashArrayString()?.let {
                        setAttribute("stroke-dasharray", it)
                    }
                }

                else -> {
                    setAttribute("stroke", "none")
                }
            }
        }
    }

    abstract val stroke: Stroke?

    abstract val fill: Fill?

    abstract fun transformVia(
        transformation: Transformation,
    ): SvgShape
}