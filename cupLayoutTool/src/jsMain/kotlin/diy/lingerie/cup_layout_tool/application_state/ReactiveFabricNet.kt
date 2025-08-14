package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.geometry.Point3D
import dev.toolkt.reactive.cell.Cell

class ReactiveFabricNet(
    val particles: Map<FabricNet.ParticleId, ReactiveParticle>,
) {
    data class ReactiveParticle(
        val position: Cell<Point3D>,
    )

    companion object {
        fun diff(
            fabricNet: Cell<FabricNet>,
        ): ReactiveFabricNet {
            val particleIds = fabricNet.currentValue.particleIds

            return ReactiveFabricNet(
                particles = particleIds.associateWith { particleId ->
                    ReactiveParticle(
                        position = fabricNet.map {
                            it.particleStateMap.getParticleState(particleId).position
                        },
                    )
                },
            )
        }
    }
}
