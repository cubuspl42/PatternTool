package diy.lingerie.cup_layout_tool.presentation

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.reactive.cell.Cell
import diy.lingerie.cup_layout_tool.application_state.FabricNet
import diy.lingerie.cup_layout_tool.application_state.ReactiveFabricNet
import dev.toolkt.js.threejs.LineBasicMaterialParams
import dev.toolkt.js.threejs.MeshLambertMaterialParams
import dev.toolkt.js.threejs.THREE

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
    fabricPiece: ReactiveFabricNet,
): THREE.Object3D = createReactiveGroup(
    children = fabricPiece.particles.values.map {
        createParticleObject3D(
            reactiveParticle = it,
        )
    }
)

fun createSpringMesh(
    fabricNet: FabricNet,
): THREE.Object3D {
    val linePoints = fabricNet.springs.flatMap { spring ->
        val firstParticleState = fabricNet.particleStateMap.getParticleState(
            particleId = spring.firstParticleId,
        )

        val secondParticleState = fabricNet.particleStateMap.getParticleState(
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
    reactiveParticle: ReactiveFabricNet.ReactiveParticle,
): THREE.Object3D = createReactiveMesh(
    geometry = fabricParticleGeometry,
    material = Cell.of(fabricParticleMaterial),
    position = reactiveParticle.position,
)
