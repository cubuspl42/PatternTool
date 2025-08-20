package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.dom.reactive.utils.createIntervalTimeoutStream
import dev.toolkt.geometry.Span
import dev.toolkt.geometry.Vector3
import dev.toolkt.reactive.event_stream.accum
import dev.toolkt.reactive.managed_io.ActionContext
import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.map
import diy.lingerie.cup_layout_tool.application_state.physics.Force
import kotlinx.browser.window
import kotlin.time.Duration.Companion.seconds

class SimulationState private constructor(
    val reactiveFabricNet: ReactiveFabricNet,
) {
    companion object {
        val stepDuration = (1.0 / 60.0).seconds

        context(actionContext: ActionContext) fun enter(): Effect<SimulationState> = window.createIntervalTimeoutStream(
            delay = stepDuration,
        ).map { tickerStream ->
            val reactiveFabricNet = ReactiveFabricNet.diff(
                fabricNet = tickerStream.accum(
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

            SimulationState(
                reactiveFabricNet = reactiveFabricNet,
            )
        }
    }
}
