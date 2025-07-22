package dev.toolkt.dom.pure.svg

import dev.toolkt.core.iterable.uncons
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.numeric.equalsWithToleranceOrNull
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.transformations.Transformation
import org.w3c.dom.Document
import org.w3c.dom.Element

data class PureSvgPath(
    override val stroke: Stroke? = Stroke.default,
    override val fill: Fill? = Fill.None,
    override val markerEndId: String? = null,
    val segments: List<Segment>,
) : PureSvgShape() {
    sealed class Segment : NumericObject {
        data object ClosePath : Segment() {
            override val finalPointOrNull: Nothing?
                get() = null

            override fun toPathSegString(): String = "Z"

            override fun transformVia(
                transformation: Transformation,
            ): Segment = this

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericTolerance
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
                other: NumericObject, tolerance: NumericTolerance
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
                other: NumericObject, tolerance: NumericTolerance
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

        sealed class QuadraticBezierSegment : CurveSegment()

        data class QuadraticBezierCurveTo(
            val controlPoint: Point,
            override val finalPoint: Point,
        ) : QuadraticBezierSegment() {
            override fun toPathSegString(): String = "Q${controlPoint.toSvgString()} ${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject,
                tolerance: NumericTolerance,
            ): Boolean = when {
                other !is QuadraticBezierCurveTo -> false
                !controlPoint.equalsWithTolerance(other.controlPoint, tolerance) -> false
                !finalPoint.equalsWithTolerance(other.finalPoint, tolerance) -> false
                else -> true
            }

            override fun transformVia(
                transformation: Transformation,
            ): QuadraticBezierCurveTo = QuadraticBezierCurveTo(
                controlPoint = transformation.transform(point = controlPoint),
                finalPoint = transformation.transform(point = finalPoint),
            )
        }

        data class SmoothQuadraticBezierCurveTo(
            override val finalPoint: Point,
        ) : QuadraticBezierSegment() {
            override fun toPathSegString(): String = "T${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject,
                tolerance: NumericTolerance,
            ): Boolean = when {
                other !is SmoothQuadraticBezierCurveTo -> false
                !finalPoint.equalsWithTolerance(other.finalPoint, tolerance) -> false
                else -> true
            }

            override fun transformVia(
                transformation: Transformation,
            ): SmoothQuadraticBezierCurveTo = SmoothQuadraticBezierCurveTo(
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
                other: NumericObject, tolerance: NumericTolerance
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
                other: NumericObject, tolerance: NumericTolerance
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

    companion object {
        fun polyline(
            stroke: Stroke,
            points: List<Point>,
        ): PureSvgPath? {
            val (firstPoint, trailingPoints) = points.uncons() ?: return null

            return PureSvgPath(
                stroke = stroke,
                segments = listOf(
                    Segment.MoveTo(
                        targetPoint = firstPoint,
                    ),
                ) + trailingPoints.map { point ->
                    Segment.LineTo(
                        finalPoint = point,
                    )
                },
            )
        }
    }

    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("path").apply {
        setAttribute("d", segments.joinToString(" ") { it.toPathSegString() })

        setupRawShape(element = this)
    }

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: NumericTolerance
    ): Boolean = when {
        other !is PureSvgPath -> false
        !stroke.equalsWithToleranceOrNull(other.stroke, tolerance) -> false
        !segments.equalsWithTolerance(other.segments, tolerance) -> false
        else -> true
    }

    override fun transformVia(
        transformation: Transformation,
    ): PureSvgPath = PureSvgPath(
        stroke = stroke,
        segments = segments.map { segment ->
            segment.transformVia(transformation = transformation)
        },
    )
}
