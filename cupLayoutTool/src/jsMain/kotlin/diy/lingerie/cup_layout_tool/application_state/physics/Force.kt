package diy.lingerie.cup_layout_tool.application_state.physics

import dev.toolkt.geometry.Direction3
import dev.toolkt.math.algebra.linear.vectors.Vector3

value class Force(
    /**
     * Force vector (in Newtons)
     */
    val forceVector: Vector3,
) {

    companion object {
        val Zero = Force(
            forceVector = Vector3.Zero,
        )

        fun inDirection(
            forceValue: Double,
            direction3: Direction3,
        ): Force = Force(
            forceVector = direction3.normalizedDirectionVector * forceValue,
        )

        fun resultant(
            forces: Iterable<Force>,
        ): Force {
            // F = ΣF
            return Force(
                forceVector = forces.fold(Vector3.Zero) { acc, force -> acc + force.forceVector },
            )
        }
    }
}
