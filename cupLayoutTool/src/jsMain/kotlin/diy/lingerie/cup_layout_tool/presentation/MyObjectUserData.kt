package diy.lingerie.cup_layout_tool.presentation

import diy.lingerie.cup_layout_tool.UserBezierMesh
import dev.toolkt.js.threejs.THREE

sealed class MyObjectUserData {
    data class HandleBallUserData(
        val handle: UserBezierMesh.Handle,
    ) : MyObjectUserData()
}

val THREE.Object3D.myUserData: MyObjectUserData?
    get() = this.userData as? MyObjectUserData
