package diy.lingerie.simple_dom.svg

import diy.lingerie.geometry.Point
import diy.lingerie.simple_dom.SimpleColor
import org.w3c.dom.Document
import org.w3c.dom.Element

data class SvgPath(
    val strokeColor: SimpleColor = SimpleColor.black,
    val segments: List<Segment>,
) : SvgElement() {
    sealed class Segment {
        data object ClosePath : Segment() {
            override fun toPathSegString(): String = "Z"
        }

        sealed class ActiveSegment : Segment() {
            abstract val finalPoint: Point
        }

        data class MoveTo(
            override val finalPoint: Point,
        ) : ActiveSegment() {
            override fun toPathSegString(): String = "M${finalPoint.toSvgString()}"
        }

        data class LineTo(
            override val finalPoint: Point,
        ) : ActiveSegment() {
            override fun toPathSegString(): String = "L${finalPoint.toSvgString()}"
        }

        data class CubicBezierCurveTo(
            val controlPoint1: Point,
            val controlPoint2: Point,
            override val finalPoint: Point,
        ) : ActiveSegment() {
            override fun toPathSegString(): String =
                "C${controlPoint1.toSvgString()} ${controlPoint2.toSvgString()} ${finalPoint.toSvgString()}"
        }

        abstract fun toPathSegString(): String


        protected fun Point.toSvgString(): String = "${x},${y}"
    }

    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("path").apply {
        setAttribute("fill", "none")
        setAttribute("stroke", strokeColor.toHexString())
        setAttribute("d", segments.joinToString(" ") { it.toPathSegString() })
    }
}
