package diy.lingerie.simple_dom.svg

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.simple_dom.SimpleColor
import diy.lingerie.utils.iterable.mapCarrying
import diy.lingerie.utils.xml.svg.asList
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel
import org.w3c.dom.svg.SVGPathSegMovetoAbs

data class SvgPath(
    val strokeColor: SimpleColor = SimpleColor.black,
    val segments: List<Segment>,
) : SvgElement() {
    sealed class Segment {
        data object ClosePath : Segment() {
            override val finalPointOrNull: Nothing?
                get() = null

            override fun toPathSegString(): String = "Z"
        }

        sealed class ActiveSegment : Segment() {
            final override val finalPointOrNull: Point
                get() = finalPoint

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

        abstract val finalPointOrNull: Point?

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

fun SVGPathElement.toSimplePath(): SvgPath {
    val (segments, _) = pathSegList.asList().mapCarrying(
        initialCarry = Point.origin,
    ) { currentPoint, svgPathSeg ->
        val segment = svgPathSeg.toSimple(currentPoint = currentPoint)

        Pair(
            segment,
            segment.finalPointOrNull ?: currentPoint,
        )
    }

    return SvgPath(
        strokeColor = SimpleColor.black,
        segments = segments,
    )
}

fun SVGPathSeg.toSimple(
    currentPoint: Point,
): SvgPath.Segment = when (pathSegType) {
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

    SVGPathSeg.PATHSEG_CURVETO_CUBIC_REL -> {
        this as SVGPathSegCurvetoCubicRel

        SvgPath.Segment.CubicBezierCurveTo(
            controlPoint1 = PrimitiveTransformation.Translation(
                tx = x1.toDouble(),
                ty = y1.toDouble(),
            ).transform(
                point = currentPoint,
            ),
            controlPoint2 = PrimitiveTransformation.Translation(
                tx = x2.toDouble(),
                ty = y2.toDouble(),
            ).transform(
                point = currentPoint,
            ),
            finalPoint = PrimitiveTransformation.Translation(
                tx = x.toDouble(),
                ty = y.toDouble(),
            ).transform(
                point = currentPoint,
            ),
        )
    }

    SVGPathSeg.PATHSEG_CLOSEPATH -> SvgPath.Segment.ClosePath

    else -> error("Unsupported path segment type: $pathSegType (${this.pathSegTypeAsLetter})")
}
