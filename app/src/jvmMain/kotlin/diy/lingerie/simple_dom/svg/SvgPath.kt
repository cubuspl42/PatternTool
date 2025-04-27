package diy.lingerie.simple_dom.svg

import diy.lingerie.geometry.Point
import diy.lingerie.simple_dom.SimpleColor
import diy.lingerie.utils.xml.childElements
import diy.lingerie.utils.xml.svg.asList
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import org.w3c.dom.svg.SVGPathSegMovetoAbs

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

        sealed class CurveSegment : ActiveSegment()

        data class MoveTo(
            val targetPoint: Point,
        ) : ActiveSegment() {
            override val finalPoint: Point
                get() = targetPoint

            override fun toPathSegString(): String = "M${finalPoint.toSvgString()}"
        }

        data class LineTo(
            override val finalPoint: Point,
        ) : CurveSegment() {
            override fun toPathSegString(): String = "L${finalPoint.toSvgString()}"
        }

        data class CubicBezierCurveTo(
            val controlPoint1: Point,
            val controlPoint2: Point,
            override val finalPoint: Point,
        ) : CurveSegment() {
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

fun SVGPathElement.toSimplePath(): SvgPath = SvgPath(
    strokeColor = SimpleColor.black,
    segments = pathSegList.asList().map { it.toSimple() },
)

fun SVGPathSeg.toSimple(): SvgPath.Segment = when (pathSegType) {
    SVGPathSeg.PATHSEG_MOVETO_ABS -> {
        this as SVGPathSegMovetoAbs

        SvgPath.Segment.MoveTo(
            targetPoint = Point(
                x = x.toDouble(),
                y = y.toDouble(),
            ),
        )
    }

    SVGPathSeg.PATHSEG_LINETO_ABS -> {
        this as SVGPathSegMovetoAbs

        SvgPath.Segment.LineTo(
            finalPoint = Point(
                x = x.toDouble(),
                y = y.toDouble(),
            ),
        )
    }

    SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS -> {
        this as SVGPathSegCurvetoCubicAbs

        SvgPath.Segment.CubicBezierCurveTo(
            controlPoint1 = Point(
                x = x1.toDouble(),
                y = y1.toDouble(),
            ),
            controlPoint2 = Point(
                x = x2.toDouble(),
                y = y2.toDouble(),
            ),
            finalPoint = Point(
                x = x.toDouble(),
                y = y.toDouble(),
            ),
        )
    }

    SVGPathSeg.PATHSEG_CLOSEPATH -> SvgPath.Segment.ClosePath

    else -> error("Unsupported path segment type: $pathSegType")
}
