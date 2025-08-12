package diy.lingerie.web_tool_3d.application_state

import dev.toolkt.geometry.Point3D
import dev.toolkt.reactive.cell.Cell

class ReactiveFabricPiece(
    val particles: Map<FabricPiece.ParticleId, ReactiveParticle>,
) {
    data class ReactiveParticle(
        val position: Cell<Point3D>,
    )

    companion object {
        fun diff(
            fabricPiece: Cell<FabricPiece>,
        ): ReactiveFabricPiece {
            val particleIds = fabricPiece.currentValue.particleIds

            return ReactiveFabricPiece(
                particles = particleIds.associateWith { particleId ->
                    ReactiveParticle(
                        position = fabricPiece.map {
                            it.particleStateMap.getParticleState(particleId).position
                        },
                    )
                },
            )
        }
    }
}
