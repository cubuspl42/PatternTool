package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureColor
import three.LineBasicMaterialParams
import three.THREE

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
