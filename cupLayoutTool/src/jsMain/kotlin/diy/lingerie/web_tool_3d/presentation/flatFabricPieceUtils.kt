package diy.lingerie.web_tool_3d.presentation

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.geometry.Point3D
import dev.toolkt.reactive.cell.Cell
import diy.lingerie.web_tool_3d.application_state.FlatFabricPiece
import dev.toolkt.js.threejs.MeshLambertMaterialParams
import dev.toolkt.js.threejs.THREE

private const val fabricParticleRadius = 0.75

private val innerFabricParticleMaterial = THREE.MeshLambertMaterial(
    params = MeshLambertMaterialParams(
        color = PureColor.green.value,
    ),
)

private val edgeFabricParticleMaterial = THREE.MeshLambertMaterial(
    params = MeshLambertMaterialParams(
        color = PureColor.blue.value,
    ),
)

private val fabricParticleGeometry = THREE.SphereGeometry(
    radius = fabricParticleRadius,
).apply {
    computeVertexNormals()
}


fun createFlatFabricPieceObject3D(
    flatFabricPiece: FlatFabricPiece,
): THREE.Object3D = createReactiveGroup(
    children = flatFabricPiece.innerParticlePositions.map {
        createFlatParticleObject3D(
            particlePosition = it.toPoint3D(), material = innerFabricParticleMaterial
        )
    } + flatFabricPiece.edgeParticlePositions.map {
        createFlatParticleObject3D(
            particlePosition = it.toPoint3D(),
            material = edgeFabricParticleMaterial,
        )
    },
)

private fun createFlatParticleObject3D(
    particlePosition: Point3D,
    material: THREE.Material,
): THREE.Object3D = createReactiveMesh(
    geometry = fabricParticleGeometry,
    material = Cell.of(material),
    position = Cell.of(particlePosition),
)
