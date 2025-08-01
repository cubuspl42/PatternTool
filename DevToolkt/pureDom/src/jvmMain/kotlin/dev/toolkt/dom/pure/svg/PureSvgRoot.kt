package dev.toolkt.dom.pure.svg

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.numeric.equalsWithToleranceOrNull
import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.dom.pure.utils.xml.writeToFile
import dev.toolkt.geometry.transformations.Transformation
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.nio.file.Path

data class PureSvgRoot(
    val viewBox: ViewBox? = null,
    val defs: List<PureSvgDef> = emptyList(),
    val width: PureDimension<*>,
    val height: PureDimension<*>,
    val graphicsElements: List<PureSvgGraphicsElement>,
) : PureSvgElement() {
    data class ViewBox(
        val x: Double,
        val y: Double,
        val width: Double,
        val height: Double,
    ) : NumericObject {
        fun toViewBoxString(): String = "$x $y $width $height"

        override fun equalsWithTolerance(
            other: NumericObject, tolerance: NumericTolerance,
        ): Boolean = when {
            other !is ViewBox -> false
            !x.equalsWithTolerance(other.x, tolerance) -> false
            !y.equalsWithTolerance(other.y, tolerance) -> false
            !width.equalsWithTolerance(other.width, tolerance) -> false
            !height.equalsWithTolerance(other.height, tolerance) -> false
            else -> true
        }

        companion object {
            fun parse(string: String): ViewBox {
                val parts = string.split(" ")

                if (parts.size != 4) {
                    error("Invalid viewBox string: $string")
                }

                val (x, y, width, height) = parts

                return ViewBox(
                    x = x.toDouble(),
                    y = y.toDouble(),
                    width = width.toDouble(),
                    height = height.toDouble(),
                )
            }
        }
    }

    companion object;

    val effectiveViewBox: ViewBox
        get() = viewBox ?: ViewBox(
            x = 0.0,
            y = 0.0,
            width = width.value,
            height = height.value,
        )

    fun writeToFile(
        filePath: Path,
    ) {
        toSvgDocument().writeToFile(
            filePath = filePath,
        )
    }

    private fun toDefsElement(
        document: Document,
    ): Element = document.createSvgElement("defs").apply {
        defs.forEach { def ->
            appendChild(def.toRawElement(document = document))
        }
    }

    internal fun setup(
        document: Document,
        root: Element,
    ) {
        val viewBox = this.viewBox

        root.run {
            setAttribute("width", width.cssString)
            setAttribute("height", height.cssString)

            if (viewBox != null) {
                setAttribute("viewBox", viewBox.toViewBoxString())
            }

            appendChild(
                toDefsElement(document = document)
            )

            graphicsElements.forEach { child ->
                appendChild(child.toRawElement(document = document))
            }
        }
    }

    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("svg").apply {
        setup(
            document = document,
            root = this,
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean {
        return when {
            other !is PureSvgRoot -> false
            !width.equalsWithTolerance(other.width, tolerance) -> false
            !height.equalsWithTolerance(other.height, tolerance) -> false
            !viewBox.equalsWithToleranceOrNull(other.viewBox, tolerance) -> false
            else -> true
        }
    }

    fun flatten(
        baseTransformation: Transformation,
    ): List<PureSvgShape> = graphicsElements.flatMap {
        it.flatten(baseTransformation = baseTransformation)
    }
}
