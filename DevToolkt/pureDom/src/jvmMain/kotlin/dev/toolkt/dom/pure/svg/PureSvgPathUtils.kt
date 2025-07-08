package dev.toolkt.dom.pure.svg

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.core.iterable.mapCarrying
import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.utils.xml.svg.asList
import dev.toolkt.dom.pure.utils.xml.svg.getComputedStyle
import dev.toolkt.dom.pure.utils.xml.svg.toList
import dev.toolkt.dom.pure.utils.xml.svg.toPureColor
import org.apache.batik.css.engine.SVGCSSEngine
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel
import org.w3c.dom.svg.SVGPathSegMovetoAbs

fun SVGPathElement.toPurePath(): PureSvgPath {
    val (segments, _) = pathSegList.asList().mapCarrying(
        initialCarry = Point.origin,
    ) { currentPoint, svgPathSeg ->
        val segment = svgPathSeg.toPureSegment(currentPoint = currentPoint)

        Pair(
            segment,
            segment.finalPointOrNull ?: currentPoint,
        )
    }

    return PureSvgPath(
        stroke = extractStroke(),
        segments = segments,
    )
}

fun SVGElement.extractStroke(): PureSvgShape.Stroke {
    val strokeColor = getComputedStyle(SVGCSSEngine.STROKE_INDEX).toPureColor()
    val strokeWidth = getComputedStyle(SVGCSSEngine.STROKE_WIDTH_INDEX).floatValue.toDouble()
    val strokeDashArray = getComputedStyle(SVGCSSEngine.STROKE_DASHARRAY_INDEX).toList()

    return PureSvgShape.Stroke(
        color = strokeColor ?: PureColor.Companion.black,
        width = strokeWidth,
        dashArray = strokeDashArray?.map { it.floatValue.toDouble() },
    )
}

fun SVGPathSeg.toPureSegment(
    currentPoint: Point,
): PureSvgPath.Segment = when (pathSegType) {
    SVGPathSeg.PATHSEG_MOVETO_ABS -> {
        this as SVGPathSegMovetoAbs

        PureSvgPath.Segment.MoveTo(
            targetPoint = Point(
                x = x.toDouble(),
                y = y.toDouble(),
            ),
        )
    }

    SVGPathSeg.PATHSEG_MOVETO_REL -> {
        this as SVGPathSegMovetoAbs

        PureSvgPath.Segment.MoveTo(
            targetPoint = PrimitiveTransformation.Translation(
                tx = x.toDouble(),
                ty = y.toDouble(),
            ).transform(
                point = currentPoint,
            ),
        )
    }

    SVGPathSeg.PATHSEG_LINETO_ABS -> {
        this as SVGPathSegMovetoAbs

        PureSvgPath.Segment.LineTo(
            finalPoint = Point(
                x = x.toDouble(),
                y = y.toDouble(),
            ),
        )
    }

    SVGPathSeg.PATHSEG_LINETO_REL -> {
        this as SVGPathSegMovetoAbs

        PureSvgPath.Segment.LineTo(
            finalPoint = PrimitiveTransformation.Translation(
                tx = x.toDouble(),
                ty = y.toDouble(),
            ).transform(
                point = currentPoint,
            ),
        )
    }

    SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS -> {
        this as SVGPathSegCurvetoQuadraticAbs

        PureSvgPath.Segment.QuadraticBezierCurveTo(
            controlPoint = Point(
                x = x1.toDouble(),
                y = y1.toDouble(),
            ),
            finalPoint = Point(
                x = x.toDouble(),
                y = y.toDouble(),
            ),
        )
    }

    SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_REL -> {
        this as SVGPathSegCurvetoQuadraticRel

        PureSvgPath.Segment.QuadraticBezierCurveTo(
            controlPoint = PrimitiveTransformation.Translation(
                tx = x1.toDouble(),
                ty = y1.toDouble(),
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

    SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS -> {
        this as SVGPathSegCurvetoCubicAbs

        PureSvgPath.Segment.CubicBezierCurveTo(
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

        PureSvgPath.Segment.CubicBezierCurveTo(
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

    SVGPathSeg.PATHSEG_CLOSEPATH -> PureSvgPath.Segment.ClosePath

    else -> error("Unsupported path segment type: $pathSegType (${this.pathSegTypeAsLetter})")
}
