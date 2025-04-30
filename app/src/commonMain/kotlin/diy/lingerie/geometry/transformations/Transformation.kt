package diy.lingerie.geometry.transformations

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.Vector2
import diy.lingerie.geometry.RelativeAngle
import diy.lingerie.geometry.Line
import diy.lingerie.geometry.Point

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
            tolerance: NumericObject.Tolerance
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
            tolerance: NumericObject.Tolerance
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
        constructor(
            tx: Double,
            ty: Double,
        ) : this(
            translationVector = Vector2(
                x = tx,
                y = ty,
            ),
        )

        override fun toSvgTransformationString(): String = "translate(${translationVector.x}, ${translationVector.y})"

        override fun transform(point: Point): Point = Point(
            x = point.x + translationVector.x,
            y = point.y + translationVector.y,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance
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
            tolerance: NumericObject.Tolerance
        ): Boolean {
            TODO("Not yet implemented")
        }
    }

    /**
     * @param angle - angle in radians
     */
    data class Rotation(
        val angle: RelativeAngle,
    ) : PrimitiveTransformation() {
        override fun toSvgTransformationString(): String = "rotate(${angle.fiInDegrees})"

        override fun transform(point: Point): Point = Point(
            x = point.x * angle.cosFi - point.y * angle.sinFi,
            y = point.x * angle.sinFi + point.y * angle.cosFi,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance
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
