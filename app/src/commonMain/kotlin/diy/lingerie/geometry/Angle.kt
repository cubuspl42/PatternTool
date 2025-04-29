package diy.lingerie.geometry

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

sealed class Angle {
    companion object {
        fun ofDegrees(value: Double): Angle = Radial(
            fi = value * PI / 180.0,
        )
    }

    data object Zero : Angle() {
        override val fi: Double
            get() = 0.0

        override val cosFi: Double
            get() = 1.0

        override val sinFi: Double
            get() = 0.0
    }

    data class Radial(
        override val fi: Double
    ) : Angle() {
        override val cosFi: Double
            get() = cos(fi)

        override val sinFi: Double
            get() = sin(fi)
    }

    data class Trigonometric(
        override val cosFi: Double,
        override val sinFi: Double,
    ) : Angle() {
        override val fi: Double
            get() = atan2(sinFi, cosFi)
    }

    val fiInDegrees: Double
        get() = fi * 180.0 / PI

    /**
     * @param fi The angle value in radians.
     */
    abstract val fi: Double

    /**
     * Cosine of the angle.
     */
    abstract val cosFi: Double

    /**
     * Sine of the angle.
     */
    abstract val sinFi: Double
}
