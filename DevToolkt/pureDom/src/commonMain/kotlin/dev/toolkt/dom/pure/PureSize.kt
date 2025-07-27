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
    fun relativize(point: Point): Vector2 = Vector2(
        x = point.x / width,
        y = point.y / height,
    )
}
