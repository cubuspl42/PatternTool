package diy.lingerie.geometry

import diy.lingerie.algebra.Vector2
import kotlin.jvm.JvmInline

/**
 * An orientation in 2D space, represented by one of its two directions. An
 * infinite family of parallel lines with a specific meaning.
 */
@JvmInline
value class Orientation(
    /**
     * One of two directions having this orientation
     */
    val representativeDirection: Direction,
) {
    companion object {
        /**
         * A horizontal direction, i.e. the one determined by the X axis
         */
        val Horizontal = Orientation(
            representativeDirection = Direction.Companion.XAxisPlus,
        )

        /**
         * A vertical direction, i.e. the one determined by the Y axis
         */
        val Vertical = Orientation(
            representativeDirection = Direction.Companion.YAxisPlus,
        )
    }

    val directionVector: Vector2
        get() = representativeDirection.directionVector
}
