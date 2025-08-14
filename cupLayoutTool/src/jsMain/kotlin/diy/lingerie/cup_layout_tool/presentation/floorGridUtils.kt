package diy.lingerie.cup_layout_tool.presentation

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.js.threejs.LineBasicMaterialParams
import dev.toolkt.js.threejs.THREE

private const val floorWidth = 32

private val floorGeometry = createLineGridGeometry(
    sideWidth = floorWidth,
)

private val floorMaterial = THREE.LineBasicMaterial(
    params = LineBasicMaterialParams(
        color = PureColor.lightGray.value,
        linewidth = 0.2,
    ),
)

fun buildFloorGrid(): THREE.Object3D = createLineGridObject(
    lineGridGeometry = floorGeometry,
    lineMaterial = floorMaterial,
)
