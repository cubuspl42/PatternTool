package diy.lingerie.web_tool_3d

import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.reactive.cell.PropertyCell
import three.THREE

sealed class MyObjectUserData {
    data class HandleBall(
        val position: PropertyCell<Vector2>,
    ) : MyObjectUserData()
}

val THREE.Object3D.myUserData: MyObjectUserData?
    get() = this.userData as? MyObjectUserData
