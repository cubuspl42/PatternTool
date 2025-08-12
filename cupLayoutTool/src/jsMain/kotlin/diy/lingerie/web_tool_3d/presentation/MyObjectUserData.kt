package diy.lingerie.web_tool_3d.presentation

import diy.lingerie.web_tool_3d.UserBezierMesh
import dev.toolkt.js.threejs.THREE

sealed class MyObjectUserData {
    data class HandleBallUserData(
        val handle: UserBezierMesh.Handle,
    ) : MyObjectUserData()
}

val THREE.Object3D.myUserData: MyObjectUserData?
    get() = this.userData as? MyObjectUserData
