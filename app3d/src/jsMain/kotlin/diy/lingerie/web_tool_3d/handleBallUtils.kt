package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.Vector3
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
    position: Cell<Vector3>,
): THREE.Mesh {
    val sphereMesh = createReactiveMesh(
        geometry = handleBallGeometry,
        material = handleBallMaterial,
        position = position,
    )

    return sphereMesh
}

fun buildFlatHandleBallMesh(
    position: PropertyCell<Vector2>,
): THREE.Mesh {
    val sphereMesh = createReactiveMesh(
        geometry = handleBallGeometry,
        material = handleBallMaterial,
        userData = MyObjectUserData.HandleBallUserData(
            position = position,
        ),
        position = position.map {
            it.toVector3(0.0)
        },
    )

    return sphereMesh
}
