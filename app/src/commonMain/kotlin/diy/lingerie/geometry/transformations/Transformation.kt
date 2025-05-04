package diy.lingerie.geometry.transformations

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.linear.vectors.Vector2
import diy.lingerie.geometry.Direction
import diy.lingerie.geometry.Line
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.RelativeAngle
import diy.lingerie.geometry.Span
import diy.lingerie.geometry.Vector2
import diy.lingerie.geometry.x
import diy.lingerie.geometry.y

sealed class Transformation : NumericObject {
    data class ReflectionOverLine(
        val line: Line,
    ) : Transformation() {
        override fun toSvgTransformationString(): String {
            TODO("Not yet implemented")
        }

        override val components: List<PrimitiveTransformation>
            get() = TODO("Not yet implemented")

        override fun transform(point: Point): Point {
            TODO("Not yet implemented")
        }

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }
    }

    object Identity : Transformation() {
        override fun toSvgTransformationString(): String = ""

        override val components: List<PrimitiveTransformation> = emptyList()

        override fun transform(point: Point): Point = point

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }
    }

    companion object {
        fun combine(
            transformations: List<Transformation>,
        ): CombinedTransformation = CombinedTransformation(
            components = transformations.flatMap {
                it.components
            },
        )
    }

    abstract fun toSvgTransformationString(): String

    abstract val components: List<PrimitiveTransformation>

    abstract fun transform(
        point: Point,
    ): Point
}

sealed class PrimitiveTransformation : Transformation() {
    companion object {
        fun combine(
            transformations: List<PrimitiveTransformation>,
        ): CombinedTransformation = CombinedTransformation(
            components = transformations,
        )

        fun combine(
            vararg transformations: PrimitiveTransformation,
        ): CombinedTransformation = combine(
            transformations = transformations.toList(),
        )
    }

    data class Translation(
        val translationVector: Vector2,
    ) : PrimitiveTransformation() {
        companion object {
            fun inDirection(
                direction: Direction,
                distance: Span,
            ): Translation = Translation(
                translationVector = direction.normalizedDirectionVector * distance.value,
            )

            fun between(
                origin: Point,
                target: Point,
            ): Translation = Translation(
                translationVector = target.pointVector - origin.pointVector,
            )
        }

        constructor(
            tx: Double,
            ty: Double,
        ) : this(
            translationVector = Vector2(
                x = tx,
                y = ty,
            ),
        )

        val direction: Direction?
            get() = translationVector.normalizeOrNull()?.let {
                Direction(normalizedDirectionVector = it)
            }

        override fun toSvgTransformationString(): String = "translate(${translationVector.x}, ${translationVector.y})"

        override fun transform(point: Point): Point = Point(
            x = point.x + translationVector.x,
            y = point.y + translationVector.y,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }
    }

    data class Scaling(
        val scaleVector: Vector2,
    ) : PrimitiveTransformation() {
        override fun toSvgTransformationString(): String = "scale(${scaleVector.x}, ${scaleVector.y})"

        override fun transform(point: Point): Point = Point(
            x = point.x * scaleVector.x,
            y = point.y * scaleVector.y,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }
    }

    data class Rotation private constructor(
        val angle: RelativeAngle,
    ) : PrimitiveTransformation() {
        companion object {
            /**
             * The identity rotation (±0 degrees)
             */
            val Identity = Rotation(
                angle = RelativeAngle.Zero,
            )

            /**
             * The quarter rotation in the clockwise direction (+90 degrees; +Pi/2 radians)
             */
            val QuarterClockwise = Rotation(
                angle = RelativeAngle.Right,
            )

            /**
             * The quarter rotation in the counterclockwise direction (-90 degrees; -Pi/2 radians)
             */
            val QuarterCounterClockwise = Rotation(
                angle = -RelativeAngle.Right,
            )

            /**
             * The half rotation (±180 degrees; ±Pi radians)
             */
            val Half = Rotation(
                angle = RelativeAngle.Straight,
            )

            fun trigonometric(
                cosFi: Double,
                sinFi: Double,
            ): Rotation = Rotation(
                angle = RelativeAngle.Trigonometric(
                    cosFi = cosFi,
                    sinFi = sinFi,
                )
            )

            fun relative(
                angle: RelativeAngle,
            ): Rotation = Rotation(
                angle = angle,
            )
        }

        private val cosFi: Double
            get() = angle.cosFi

        private val sinFi: Double
            get() = angle.sinFi

        override fun toSvgTransformationString(): String = "rotate(${angle.fiInDegrees})"

        override fun transform(point: Point): Point = Point(
            x = point.x * cosFi - point.y * sinFi,
            y = point.x * sinFi + point.y * cosFi,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }
    }

    final override val components: List<PrimitiveTransformation>
        get() = listOf(this)
}

sealed class ComplexTransformation : Transformation() {
    final override fun transform(
        point: Point,
    ): Point = components.fold(point) { acc, transformation ->
        transformation.transform(acc)
    }
}

data class CombinedTransformation(
    override val components: List<PrimitiveTransformation>,
) : ComplexTransformation() {
    companion object;

    override fun toSvgTransformationString(): String =
        components.reversed().joinToString(separator = " ") { transformation ->
            transformation.toSvgTransformationString()
        }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean {
        TODO("Not yet implemented")
    }
}

sealed class ShiftedTransformation : ComplexTransformation() {
    final override val components: List<PrimitiveTransformation>
        get() = listOf(
            PrimitiveTransformation.Translation(-origin.pointVector)
        ) + innerComponents + listOf(
            PrimitiveTransformation.Translation(origin.pointVector)
        )

    abstract val origin: Point

    abstract val innerComponents: List<PrimitiveTransformation>
}
