package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.Vector3
import three.THREE

fun THREE.Vector3.toMathVector3() = Vector3(
    x = this.x,
    y = this.y,
    z = this.z,
)
