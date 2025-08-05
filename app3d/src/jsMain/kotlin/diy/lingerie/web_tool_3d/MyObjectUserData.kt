package diy.lingerie.web_tool_3d

import three.THREE

sealed class MyObjectUserData {
    data class HandleBallUserData(
        val handle: UserBezierMesh.Handle,
    ) : MyObjectUserData()
}

val THREE.Object3D.myUserData: MyObjectUserData?
    get() = this.userData as? MyObjectUserData
