package diy.lingerie.cup_layout_tool.application_state.physics

import dev.toolkt.math.algebra.linear.vectors.Vector3

value class Acceleration(
    /**
     * Acceleration vector (in meters per second squared)
     */
    val accelerationVector: Vector3,
) {
    companion object {
        fun calculate(
            force: Force,
            mass: Mass,
        ): Acceleration {
            // F = m * a
            // a = F / m
            return Acceleration(
                accelerationVector = force.forceVector / mass.massValue,
            )
        }
    }
}
