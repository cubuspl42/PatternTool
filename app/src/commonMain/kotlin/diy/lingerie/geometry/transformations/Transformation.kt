package diy.lingerie.geometry.transformations

import diy.lingerie.algebra.Vector2
import diy.lingerie.geometry.Angle
import diy.lingerie.geometry.Line
import diy.lingerie.geometry.Point

sealed class Transformation {
    data class ReflectionOverLine(
        val line: Line,
    ) : Transformation() {
        override val components: List<PrimitiveTransformation>
            get() = TODO("Not yet implemented")

        override fun transform(point: Point): Point {
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
        override fun transform(point: Point): Point = Point(
            x = point.x + translationVector.x,
            y = point.y + translationVector.y,
        )
    }

    data class Scaling(
        val scaleVector: Vector2,
    ) : PrimitiveTransformation() {
        override fun transform(point: Point): Point = Point(
            x = point.x * scaleVector.x,
            y = point.y * scaleVector.y,
        )
    }

    /**
     * @param angle - angle in radians
     */
    data class Rotation(
        val angle: Angle,
    ) : PrimitiveTransformation() {
        override fun transform(point: Point): Point = Point(
            x = point.x * angle.cosFi - point.y * angle.sinFi,
            y = point.x * angle.sinFi + point.y * angle.cosFi,
        )
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
) : ComplexTransformation()

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
