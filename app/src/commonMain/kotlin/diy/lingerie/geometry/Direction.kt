package diy.lingerie.geometry

import diy.lingerie.math.algebra.linear.vectors.Vector2
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
    val normalizedDirectionVector: Vector2,
) : RadialObject {
    override fun equalsWithRadialTolerance(
        other: RadialObject,
        tolerance: RelativeAngle.RadialTolerance,
    ): Boolean = when {
        other !is Direction -> false

        else -> angle.equalsWithRadialTolerance(
            other.angle,
            tolerance = tolerance,
        )
    }

    val normal: Direction
        get() = Direction(
            normalizedDirectionVector = Vector2(
                x = -normalizedDirectionVector.y,
                y = normalizedDirectionVector.x,
            ),
        )

    /**
     * Angle from the positive X-axis to the direction vector
     */
    private val angle: RelativeAngle.Trigonometric
        get() = RelativeAngle.Trigonometric.of(
            normalizedVector = normalizedDirectionVector,
        )

    val opposite: Direction
        get() = Direction(
            normalizedDirectionVector = -normalizedDirectionVector,
        )

    companion object {
        val XAxisPlus = Direction(
            normalizedDirectionVector = Vector2(
                x = 1.0,
                y = 0.0,
            ),
        )

        val XAxisMinus = Direction(
            normalizedDirectionVector = Vector2(
                x = -1.0,
                y = 0.0,
            ),
        )

        val YAxisPlus = Direction(
            normalizedDirectionVector = Vector2(
                x = 0.0,
                y = 1.0,
            ),
        )

        val YAxisMinus = Direction(
            normalizedDirectionVector = Vector2(
                x = 0.0,
                y = -1.0,
            ),
        )
    }

    init {
        require(normalizedDirectionVector.isNormalized())
    }
}
