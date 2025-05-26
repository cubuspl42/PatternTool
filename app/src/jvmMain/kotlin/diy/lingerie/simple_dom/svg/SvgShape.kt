package diy.lingerie.simple_dom.svg

import diy.lingerie.geometry.transformations.Transformation
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.numeric.equalsWithToleranceOrNull
import diy.lingerie.simple_dom.SimpleColor
import diy.lingerie.simple_dom.toHexString
import org.w3c.dom.Element

abstract class SvgShape : SvgGraphicsElements() {
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

    sealed class Fill : NumericObject {
        data class Specified(
            val color: SimpleColor,
        ) : Fill() {
            companion object {
                val default = Specified(
                    color = SimpleColor.black,
                )
            }

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = when {
                other !is Specified -> false
                color != other.color -> false
                else -> true
            }

            override fun toFillString(): String = color.toHexString()
        }

        data object None : Fill() {
            override fun toFillString(): String = "none"

            override fun equalsWithTolerance(
                other: NumericObject,
                tolerance: NumericObject.Tolerance
            ): Boolean {
                TODO("Not yet implemented")
            }
        }


        abstract fun toFillString(): String
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
        val markerEndId = this.markerEndId

        return element.apply {
            if (fill != null) {
                setAttribute("fill", fill.toFillString())
            }

            if (stroke != null) {
                setAttribute("stroke", stroke.color.toHexString())
                setAttribute("stroke-width", stroke.width.toString())

                stroke.toDashArrayString()?.let {
                    setAttribute("stroke-dasharray", it)
                }
            }

            if (markerEndId != null) {
                setAttribute("marker-end", "url(#$markerEndId)")
            }
        }
    }

    open val markerEndId: String?
        get() = null

    abstract val stroke: Stroke?

    abstract val fill: Fill?

    abstract fun transformVia(
        transformation: Transformation,
    ): SvgShape
}
