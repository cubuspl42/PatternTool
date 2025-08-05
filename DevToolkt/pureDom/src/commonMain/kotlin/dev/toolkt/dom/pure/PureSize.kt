package dev.toolkt.dom.pure

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Vector2
import dev.toolkt.math.algebra.linear.vectors.Vector2

/**
 * A size (width and height) of an axis-aligned 2D DOM-related entity.
 */
data class PureSize(
    val width: Double,
    val height: Double,
) {
    fun relativizeVector(point: Point): Vector2 = Vector2(
        x = point.x / width,
        y = point.y / height,
    )

    fun relativize(point: Point): Point = Point(
        x = point.x / width,
        y = point.y / height,
    )

    val sizeVector: Vector2
        get() = Vector2(
            x = width,
            y = height,
        )
}
