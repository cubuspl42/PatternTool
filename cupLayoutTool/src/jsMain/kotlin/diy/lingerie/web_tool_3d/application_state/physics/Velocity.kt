package diy.lingerie.web_tool_3d.application_state.physics

import dev.toolkt.geometry.Point3D
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.dom.reactive.utils.inSeconds
import kotlin.time.Duration

value class Velocity(
    /**
     * Velocity vector (in units per second)
     */
    val velocityVector: Vector3,
) {
    companion object {
        val Zero: Velocity = Velocity(
            velocityVector = Vector3.Zero,
        )

        fun move(
            point: Point3D,
            velocity: Velocity,
            duration: Duration,
        ): Point3D {
            // s = v * t
            return Point3D(
                point.pointVector + (velocity.velocityVector * duration.inSeconds),
            )
        }
    }

    fun accelarate(
        acceleration: Acceleration,
        duration: Duration,
    ): Velocity {
        // v = u + a * t
        return Velocity(
            velocityVector = velocityVector + (acceleration.accelerationVector * duration.inSeconds),
        )
    }
}
