package diy.lingerie.simple_dom.svg

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.math.algebra.equalsWithToleranceOrNull
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.simple_dom.SimpleColor
import diy.lingerie.simple_dom.toList
import diy.lingerie.simple_dom.toSimpleColor
import diy.lingerie.utils.iterable.mapCarrying
import diy.lingerie.utils.xml.svg.asList
import diy.lingerie.utils.xml.svg.getComputedStyle
import org.apache.batik.css.engine.SVGCSSEngine
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel
import org.w3c.dom.svg.SVGPathSegMovetoAbs

data class SvgPath(
    val stroke: Stroke,
    val segments: List<Segment>,
) : SvgElement() {
    sealed class Segment : NumericObject {
        data object ClosePath : Segment() {
            override val finalPointOrNull: Nothing?
                get() = null

            override fun toPathSegString(): String = "Z"

            override fun transformVia(
                transformation: Transformation,
            ): Segment = this

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = other == ClosePath
        }

        sealed class ActiveSegment : Segment() {
            final override val finalPointOrNull: Point
                get() = finalPoint

            abstract val finalPoint: Point

            val effectiveReflectedControlPoint: Point
                get() = when (this) {
                    is CubicBezierSegment -> controlPoint2.reflectedBy(finalPoint)
                    else -> finalPoint
                }
        }

        sealed class CurveSegment : ActiveSegment()

        data class MoveTo(
            val targetPoint: Point,
        ) : ActiveSegment() {
            override val finalPoint: Point
                get() = targetPoint

            override fun toPathSegString(): String = "M${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = when (other) {
                is MoveTo -> targetPoint.equalsWithTolerance(other.targetPoint, tolerance)
                else -> false
            }

            override fun transformVia(
                transformation: Transformation,
            ): MoveTo = MoveTo(
                targetPoint = transformation.transform(point = targetPoint),
            )
        }

        data class LineTo(
            override val finalPoint: Point,
        ) : CurveSegment() {
            override fun toPathSegString(): String = "L${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = when (other) {
                is LineTo -> finalPoint.equalsWithTolerance(other.finalPoint, tolerance)
                else -> false
            }

            override fun transformVia(
                transformation: Transformation,
            ): LineTo = LineTo(
                finalPoint = transformation.transform(point = finalPoint),
            )
        }

        sealed class CubicBezierSegment : CurveSegment() {
            abstract val controlPoint2: Point
        }

        data class CubicBezierCurveTo(
            val controlPoint1: Point,
            override val controlPoint2: Point,
            override val finalPoint: Point,
        ) : CubicBezierSegment() {
            override fun toPathSegString(): String =
                "C${controlPoint1.toSvgString()} ${controlPoint2.toSvgString()} ${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = when {
                other !is CubicBezierCurveTo -> false
                !controlPoint1.equalsWithTolerance(other.controlPoint1, tolerance) -> false
                !controlPoint2.equalsWithTolerance(other.controlPoint2, tolerance) -> false
                !finalPoint.equalsWithTolerance(other.finalPoint, tolerance) -> false
                else -> true
            }

            override fun transformVia(
                transformation: Transformation,
            ): CubicBezierCurveTo = CubicBezierCurveTo(
                controlPoint1 = transformation.transform(point = controlPoint1),
                controlPoint2 = transformation.transform(point = controlPoint2),
                finalPoint = transformation.transform(point = finalPoint),
            )
        }

        data class SmoothCubicBezierCurveTo(
            override val controlPoint2: Point,
            override val finalPoint: Point,
        ) : CubicBezierSegment() {
            override fun toPathSegString(): String = "S${controlPoint2.toSvgString()} ${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = when {
                other !is SmoothCubicBezierCurveTo -> false
                !controlPoint2.equalsWithTolerance(other.controlPoint2, tolerance) -> false
                !finalPoint.equalsWithTolerance(other.finalPoint, tolerance) -> false
                else -> true
            }

            override fun transformVia(
                transformation: Transformation,
            ): SmoothCubicBezierCurveTo = SmoothCubicBezierCurveTo(
                controlPoint2 = transformation.transform(point = controlPoint2),
                finalPoint = transformation.transform(point = finalPoint),
            )
        }

        abstract val finalPointOrNull: Point?

        abstract fun toPathSegString(): String

        abstract fun transformVia(
            transformation: Transformation,
        ): Segment

        protected fun Point.toSvgString(): String = "${x},${y}"
    }

    data class Stroke(
        val color: SimpleColor,
        val width: Double,
        val dashArray: List<Double>? = null,
    ) : NumericObject {
        companion object {
            val default = Stroke(
                color = SimpleColor.black,
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

    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("path").apply {
        setAttribute("fill", "none")
        setAttribute("stroke", stroke.color.toHexString())
        setAttribute("stroke-width", stroke.width.toString())

        stroke.toDashArrayString()?.let {
            setAttribute("stroke-dasharray", it)
        }

        setAttribute("d", segments.joinToString(" ") { it.toPathSegString() })
    }

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is SvgPath -> false
        !stroke.equalsWithTolerance(other.stroke, tolerance) -> false
        !segments.equalsWithTolerance(other.segments, tolerance) -> false
        else -> true
    }

    override fun flatten(
        baseTransformation: Transformation,
    ): List<SvgPath> = listOf(
        transformVia(transformation = baseTransformation),
    )

    fun transformVia(
        transformation: Transformation,
    ): SvgPath = SvgPath(
        stroke = stroke,
        segments = segments.map { segment ->
            segment.transformVia(transformation = transformation)
        },
    )
}

fun SVGPathElement.toSimplePath(): SvgPath {
    val (segments, _) = pathSegList.asList().mapCarrying(
        initialCarry = Point.origin,
    ) { currentPoint, svgPathSeg ->
        val segment = svgPathSeg.toSimpleSegment(currentPoint = currentPoint)

        Pair(
            segment,
            segment.finalPointOrNull ?: currentPoint,
        )
    }

    val strokeColor = getComputedStyle(SVGCSSEngine.STROKE_INDEX).toSimpleColor()
    val strokeWidth = getComputedStyle(SVGCSSEngine.STROKE_WIDTH_INDEX).floatValue.toDouble()
    val strokeDashArray = getComputedStyle(SVGCSSEngine.STROKE_DASHARRAY_INDEX).toList()

    return SvgPath(
        stroke = SvgPath.Stroke(
            color = strokeColor ?: SimpleColor.black,
            width = strokeWidth,
            dashArray = strokeDashArray?.map { it.floatValue.toDouble() },
        ),
        segments = segments,
    )
}

fun SVGPathSeg.toSimpleSegment(
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
