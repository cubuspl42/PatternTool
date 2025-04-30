package diy.lingerie.geometry

import diy.lingerie.algebra.Vector2
import kotlin.jvm.JvmInline

/**
 * A direction in 2D space, represented by a normalized vector. An infinite
 * family of parallel likewise-oriented vectors with a specific meaning.
 */
@JvmInline
value class Direction(
    /**
     * Normalized direction vector
     */
    val directionVector: Vector2,
) {
    companion object {
        val XAxisPlus = Direction(
            directionVector = Vector2(
                x = 1.0,
                y = 0.0,
            ),
        )

        val XAxisMinus = Direction(
            directionVector = Vector2(
                x = -1.0,
                y = 0.0,
            ),
        )

        val YAxisPlus = Direction(
            directionVector = Vector2(
                x = 0.0,
                y = 1.0,
            ),
        )

        val YAxisMinus = Direction(
            directionVector = Vector2(
                x = 0.0,
                y = -1.0,
            ),
        )
    }
}
