package diy.lingerie.web_tool_3d.presentation

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.reactive.cell.Cell
import diy.lingerie.web_tool_3d.application_state.FabricPiece
import three.LineBasicMaterialParams
import three.MeshLambertMaterialParams
import three.THREE
import three.setPositionAttribute

private const val fabricParticleRadius = 0.75

private val fabricParticleMaterial = THREE.MeshLambertMaterial(
    params = MeshLambertMaterialParams(
        color = PureColor.darkBlue.value,
    ),
)

private val fabricParticleGeometry = THREE.SphereGeometry(
    radius = fabricParticleRadius,
).apply {
    computeVertexNormals()
}

private val springLineMaterial = THREE.LineBasicMaterial(
    params = LineBasicMaterialParams(
        color = PureColor.blue.value,
        linewidth = 0.2,
    ),
)

fun createFabricPieceObject3D(
    fabricPiece: FabricPiece,
): THREE.Object3D {
    val lines = fabricPiece.springs.map { spring ->
        val firstParticleState = fabricPiece.particleStateMap.getParticleState(
            particleId = spring.firstParticleId,
        )

        val secondParticleState = fabricPiece.particleStateMap.getParticleState(
            particleId = spring.secondParticleId,
        )

        Pair(
            firstParticleState.position,
            secondParticleState.position,
        )
    }

    return createReactiveGroup(
        children = fabricPiece.particleStates.map {
            createParticleObject3D(
                particleState = it,
            )
        } + createSpringMesh(
            fabricPiece = fabricPiece,
        ),
    )
}

fun createSpringMesh(
    fabricPiece: FabricPiece,
): THREE.Object3D {
    val linePoints = fabricPiece.springs.flatMap { spring ->
        val firstParticleState = fabricPiece.particleStateMap.getParticleState(
            particleId = spring.firstParticleId,
        )

        val secondParticleState = fabricPiece.particleStateMap.getParticleState(
            particleId = spring.secondParticleId,
        )

        listOf(
            firstParticleState.position,
            secondParticleState.position,
        )
    }

    // This should be cleaned
    val geometry = THREE.BufferGeometry().apply {
        setPositionAttribute(
            positions = linePoints,
        )
    }

    return THREE.LineSegments(
        geometry = geometry,
        material = springLineMaterial,
    )
}

fun createParticleObject3D(
    particleState: FabricPiece.ParticleState,
): THREE.Object3D = createReactiveMesh(
    geometry = fabricParticleGeometry,
    material = Cell.of(fabricParticleMaterial),
    position = Cell.of(particleState.position),
)
