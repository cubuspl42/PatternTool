package diy.lingerie.geometry.transformations

import diy.lingerie.geometry.Line
import diy.lingerie.geometry.Point

sealed class Transformation {
    data class ReflectionOverLine(
        val line: Line,
    ): Transformation() {
        override fun transform(point: Point): Point {
            TODO("Not yet implemented")
        }
    }

    abstract fun transform(
        point: Point,
    ): Point
}
