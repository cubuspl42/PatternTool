package diy.lingerie.geometry.transformations

import diy.lingerie.algebra.Vector2
import diy.lingerie.geometry.Line
import diy.lingerie.geometry.Point
import kotlin.math.cos
import kotlin.math.sin

sealed class Transformation {
    data class ReflectionOverLine(
        val line: Line,
    ) : Transformation() {
        override fun transform(point: Point): Point {
            TODO("Not yet implemented")
        }
    }

    abstract fun transform(
        point: Point,
    ): Point
}

sealed class PrimitiveTransformation : Transformation() {
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
        val angle: Double,
    ) : PrimitiveTransformation() {
        override fun transform(point: Point): Point = Point(
            x = point.x * cos(angle) - point.y * sin(angle),
            y = point.x * sin(angle) + point.y * cos(angle),
        )
    }
}

sealed class ComplexTransformation : Transformation() {
    final override fun transform(
        point: Point,
    ): Point = components.fold(point) { acc, transformation ->
        transformation.transform(acc)
    }

    abstract val components: List<PrimitiveTransformation>
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
