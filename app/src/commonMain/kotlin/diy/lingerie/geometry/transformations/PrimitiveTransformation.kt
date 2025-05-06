package diy.lingerie.geometry.transformations

import diy.lingerie.geometry.Direction
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.RelativeAngle
import diy.lingerie.geometry.Span
import diy.lingerie.geometry.Vector2
import diy.lingerie.geometry.rotate
import diy.lingerie.geometry.x
import diy.lingerie.geometry.y
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.math.algebra.linear.vectors.Vector2

sealed class PrimitiveTransformation : StandaloneTransformation() {
    companion object {
        fun combine(
            transformations: List<PrimitiveTransformation>,
        ): CombinedTransformation = CombinedTransformation(
            standaloneTransformations = transformations,
        )

        fun combine(
            vararg transformations: PrimitiveTransformation,
        ): CombinedTransformation = combine(
            transformations = transformations.toList(),
        )
    }

    data class Universal(
        val a: Double = 1.0,
        val b: Double = 0.0,
        val c: Double = 0.0,
        val d: Double = 1.0,
        val tx: Double = 0.0,
        val ty: Double = 0.0,
    ) : PrimitiveTransformation() {
        override fun toSvgTransformationString(): String = "matrix($a, $b, $c, $d, $tx, $ty)"

        override fun transform(point: Point): Point {
            val x = point.x
            val y = point.y
            return Point(
                x = a * x + c * y + tx,
                y = b * x + d * y + ty,
            )
        }

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }

        override val toUniversal: Universal
            get() = this

        override fun invert(): Universal {
            val determinant = a * d - b * c

            if (determinant.equalsWithTolerance(0.0)) {
                throw IllegalStateException("Cannot invert transformation with zero determinant")
            }

            val invertedA = d / determinant
            val invertedB = -b / determinant
            val invertedC = -c / determinant
            val invertedD = a / determinant
            val invertedTx = (c * ty - d * tx) / determinant
            val invertedTy = (b * tx - a * ty) / determinant

            return Universal(
                a = invertedA,
                b = invertedB,
                c = invertedC,
                d = invertedD,
                tx = invertedTx,
                ty = invertedTy,
            )
        }

        fun mixWith(
            laterTranslation: Universal,
        ): Universal = Universal(
            a = a * laterTranslation.a + c * laterTranslation.b,
            b = b * laterTranslation.a + d * laterTranslation.b,
            c = a * laterTranslation.c + c * laterTranslation.d,
            d = b * laterTranslation.c + d * laterTranslation.d,
            tx = a * laterTranslation.tx + c * laterTranslation.ty + tx,
            ty = b * laterTranslation.tx + d * laterTranslation.ty + ty,
        )
    }

    sealed class Specific : PrimitiveTransformation()

    data class Translation(
        val translationVector: Vector2,
    ) : Specific() {
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

        val tx: Double
            get() = translationVector.x

        val ty: Double
            get() = translationVector.y

        val direction: Direction?
            get() = translationVector.normalizeOrNull()?.let {
                Direction(normalizedDirectionVector = it)
            }

        fun scale(
            scaling: Scaling,
        ): Translation = Translation(
            translationVector = translationVector * scaling.scaleVector,
        )

        fun rotate(
            rotation: Rotation,
        ): Translation = Translation(
            translationVector = translationVector.rotate(
                angle = rotation.angle,
            ),
        )

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

        override val toUniversal: Universal
            get() = Universal(
                tx = translationVector.x,
                ty = translationVector.y,
            )

        override fun invert(): Translation = Translation(
            translationVector = -translationVector,
        )
    }

    data class Scaling(
        val scaleVector: Vector2,
    ) : Specific() {
        constructor(
            sx: Double,
            sy: Double,
        ) : this(
            scaleVector = Vector2(
                x = sx,
                y = sy,
            ),
        )

        init {
            require(!scaleVector.equalsWithTolerance(Vector2.Companion.Zero))
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

        override val toUniversal: Universal
            get() = Universal(
                a = scaleVector.x,
                d = scaleVector.y,
            )

        override fun invert(): Scaling = Scaling(
            scaleVector = diy.lingerie.geometry.Vector2(
                x = 1.0 / scaleVector.x,
                y = 1.0 / scaleVector.y,
            ),
        )
    }

    data class Rotation private constructor(
        val angle: RelativeAngle,
    ) : Specific() {
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

        override fun transform(
            point: Point,
        ): Point = Point(
            pointVector = point.pointVector.rotate(
                angle = angle,
            ),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }

        override val toUniversal: Universal
            get() = Universal(
                a = cosFi,
                b = -sinFi,
                c = sinFi,
                d = cosFi,
            )

        override fun invert(): Rotation = Rotation(
            angle = -angle,
        )
    }

    abstract override fun invert(): PrimitiveTransformation

    final override val primitiveTransformations: List<PrimitiveTransformation>
        get() = listOf(this)
}
