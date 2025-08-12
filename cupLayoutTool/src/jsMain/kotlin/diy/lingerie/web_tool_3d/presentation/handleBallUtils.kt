package diy.lingerie.web_tool_3d.presentation

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.geometry.Point3D
import dev.toolkt.reactive.cell.Cell
import diy.lingerie.web_tool_3d.UserBezierMesh
import diy.lingerie.web_tool_3d.application_state.InteractionState
import three.MeshLambertMaterialParams
import three.THREE

private const val handleBallRadius = 2.0

private val handleBallGeometry = THREE.SphereGeometry(
    radius = handleBallRadius,
)

private val handleBallMaterial = THREE.MeshLambertMaterial(
    params = MeshLambertMaterialParams(
        color = PureColor.red.value,
    ),
)

private val handleBallFocusMaterial = THREE.MeshLambertMaterial(
    params = MeshLambertMaterialParams(
        color = PureColor.lightBlue.value,
    ),
)

fun buildHandleBallMesh(
    position: Cell<Point3D>,
): THREE.Mesh {
    val sphereMesh = createReactiveMesh(
        geometry = handleBallGeometry,
        material = Cell.of(handleBallMaterial),
        position = position,
    )

    return sphereMesh
}

fun buildFlatHandleBallMesh(
    interactionState: InteractionState,
    handle: UserBezierMesh.Handle,
): THREE.Mesh {
    val isFocused = interactionState.focusedHandle.map { it == handle }

    val sphereMesh = createReactiveMesh(
        geometry = handleBallGeometry,
        material = isFocused.map { isFocusedNow ->
            when {
                isFocusedNow -> handleBallFocusMaterial
                else -> handleBallMaterial
            }
        },
        userData = MyObjectUserData.HandleBallUserData(
            handle = handle,
        ),
        position = handle.position.map {
            it.toPoint3D()
        },
    )

    return sphereMesh
}
