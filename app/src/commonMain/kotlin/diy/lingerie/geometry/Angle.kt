package diy.lingerie.geometry

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * @param fi The angle value in radians.
 */
data class Angle(
    val fi: Double,
) {
    companion object {
        val zero: Angle = Angle(fi = 0.0)

        fun ofDegrees(value: Double): Angle = Angle(
            fi = value * PI / 180.0,
        )
    }

    val cosFi: Double
        get() = cos(fi)

    val sinFi: Double
        get() = sin(fi)
}
