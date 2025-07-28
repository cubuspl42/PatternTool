package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.PropertyCell
import three.THREE

sealed class MyObjectUserData {
    data class HandleBallUserData(
        val position: PropertyCell<Point>,
    ) : MyObjectUserData()
}

val THREE.Object3D.myUserData: MyObjectUserData?
    get() = this.userData as? MyObjectUserData
