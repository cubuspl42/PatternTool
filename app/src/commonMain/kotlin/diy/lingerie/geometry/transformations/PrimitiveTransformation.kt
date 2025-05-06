package diy.lingerie.geometry.transformations

import diy.lingerie.geometry.Direction
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.RelativeAngle
import diy.lingerie.geometry.Span
import diy.lingerie.geometry.x
import diy.lingerie.geometry.y
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.linear.vectors.Vector2

sealed class PrimitiveTransformation : SimpleTransformation() {
    companion object {
        fun combine(
            transformations: List<PrimitiveTransformation>,
        ): CombinedTransformation = CombinedTransformation(
            simpleTransformations = transformations,
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
            translationVector = diy.lingerie.geometry.Vector2(
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

        override fun invert(): Translation = Translation(
            translationVector = -translationVector,
        )
    }

    data class Scaling(
        val scaleVector: Vector2,
    ) : PrimitiveTransformation() {
        init {
            require(scaleVector != Vector2.Companion.Zero)
        }

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

        override fun invert(): Scaling = Scaling(
            scaleVector = diy.lingerie.geometry.Vector2(
                x = 1.0 / scaleVector.x,
                y = 1.0 / scaleVector.y,
            ),
        )
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

        override fun invert(): Rotation = Rotation(
            angle = -angle,
        )
    }

    abstract override fun invert(): PrimitiveTransformation

    final override val primitiveTransformations: List<PrimitiveTransformation>
        get() = listOf(this)
}
