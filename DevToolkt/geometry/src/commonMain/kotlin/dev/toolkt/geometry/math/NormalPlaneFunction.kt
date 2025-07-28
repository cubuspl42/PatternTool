package dev.toolkt.geometry.math

import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.geometry.z
import dev.toolkt.math.algebra.Function
import dev.toolkt.math.algebra.linear.vectors.Vector3

data class NormalPlaneFunction(
    val origin: Vector3,
    val normal: Vector3,
) : Function<Vector3, Double> {
    val d: Double
        get() = normal.dot(origin)

    override fun apply(a: Vector3): Double {
        val p = a

        val a = normal.x
        val b = normal.y
        val c = normal.z

        val x0 = origin.x
        val y0 = origin.y
        val z0 = origin.z

        return a * (p.x - x0) + b * (p.y - y0) + c * (p.z - z0)
    }
}
