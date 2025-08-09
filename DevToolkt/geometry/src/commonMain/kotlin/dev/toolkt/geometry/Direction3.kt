package dev.toolkt.geometry

import dev.toolkt.math.algebra.linear.vectors.Vector3
import kotlin.jvm.JvmInline

/**
 * A direction in 3D space, represented by a normalized vector.
 */
@JvmInline
value class Direction3 internal constructor(
    /**
     * Normalized direction vector
     */
    val normalizedDirectionVector: Vector3,
) {
    companion object {
        fun normalize(
            directionVector: Vector3,
        ): Direction3? = directionVector.normalizeOrNull()?.let {
            Direction3(normalizedDirectionVector = it)
        }

        val ZAxisPlus = Direction3(
            normalizedDirectionVector = Vector3(
                x = 0.0,
                y = 0.0,
                z = 1.0,
            ),
        )
    }

    init {
        require(normalizedDirectionVector.isNormalized())
    }

    val opposite: Direction3
        get() = Direction3(
            normalizedDirectionVector = -normalizedDirectionVector,
        )
}
