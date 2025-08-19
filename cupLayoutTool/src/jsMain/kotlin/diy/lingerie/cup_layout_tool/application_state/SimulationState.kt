package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.dom.reactive.utils.createTimeoutStream
import dev.toolkt.geometry.Span
import dev.toolkt.geometry.Vector3
import dev.toolkt.reactive.event_stream.accum
import diy.lingerie.cup_layout_tool.application_state.physics.Force
import kotlin.time.Duration.Companion.seconds

class SimulationState {
    companion object {
        val stepDuration = (1.0 / 60.0).seconds
    }

    /*
    val fabricNet = ReactiveFabricNet.diff(
        fabricNet = createTimeoutStream(
            delay = stepDuration,
        ).accum(
            initialValue = FabricNet.rectangular(
                width = 16,
                height = 16,
                springRestLength = Span.of(4.0),
            )
        ) { fabricPiece, _ ->
            fabricPiece.simulate(
                externalForce = Force(
                    forceVector = Vector3(
                        x = 0.0,
                        y = 0.0,
                        z = -10.0,
                    ),
                ),
                stepDuration = stepDuration,
            )
        },
    )

     */
}
