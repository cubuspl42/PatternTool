package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import three.MeshLambertMaterialParams
import three.THREE

private const val handleBallRadius = 0.02

private val handleBallGeometry = THREE.SphereGeometry(
    radius = handleBallRadius,
)

private val handleBallMaterial = THREE.MeshLambertMaterial(
    params = MeshLambertMaterialParams(
        color = PureColor.red.value,
    ),
)

fun buildHandleBallMesh(
    position: Cell<Point3D>,
): THREE.Mesh {
    val sphereMesh = createReactiveMesh(
        geometry = handleBallGeometry,
        material = handleBallMaterial,
        position = position,
    )

    return sphereMesh
}

fun buildFlatHandleBallMesh(
    position: PropertyCell<Point>,
): THREE.Mesh {
    val sphereMesh = createReactiveMesh(
        geometry = handleBallGeometry,
        material = handleBallMaterial,
        userData = MyObjectUserData.HandleBallUserData(
            position = position,
        ),
        position = position.map {
            it.toPoint3D()
        },
    )

    return sphereMesh
}
