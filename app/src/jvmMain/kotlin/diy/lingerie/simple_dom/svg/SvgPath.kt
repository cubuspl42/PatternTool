package diy.lingerie.simple_dom.svg

import diy.lingerie.geometry.Point
import org.w3c.dom.Document
import org.w3c.dom.Element

data class SvgPath(
    val segments: List<Segment>,
) : SvgElement() {
    sealed class Segment {
        data class MoveTo(
            override val finalPoint: Point,
        ) : Segment() {
            override fun toPathSegString(): String = "M${finalPoint.toSvgString()}"
        }

        data class LineTo(
            override val finalPoint: Point,
        ) : Segment() {
            override fun toPathSegString(): String = "L${finalPoint.toSvgString()}"
        }

        data class CubicBezierCurveTo(
            val controlPoint1: Point,
            val controlPoint2: Point,
            override val finalPoint: Point,
        ) : Segment() {
            override fun toPathSegString(): String =
                "C${controlPoint1.toSvgString()} ${controlPoint2.toSvgString()} ${finalPoint.toSvgString()}"
        }

        abstract fun toPathSegString(): String

        abstract val finalPoint: Point

        protected fun Point.toSvgString(): String = "${x},${y}"
    }

    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("path").apply {
        setAttribute("d", segments.joinToString(" ") { it.toPathSegString() })
    }
}
