package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureColor
import three.MeshLambertMaterialParams
import three.THREE

private const val floorWidth = 3.0

private val floorGeometry = THREE.PlaneGeometry(
    width = floorWidth,
    height = floorWidth,
)

private val floorMaterial = THREE.MeshLambertMaterial(
    params = MeshLambertMaterialParams(
        color = PureColor.lightGray.value,
        transparent = true,
        opacity = 0.4,
    ),
)

fun buildFloor(): THREE.Mesh {
    val floorMesh = createReactiveMesh(
        geometry = floorGeometry,
        material = floorMaterial,
    )

    return floorMesh
}
