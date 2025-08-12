package diy.lingerie.web_tool_3d.presentation

import dev.toolkt.geometry.Vector3
import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.geometry.z
import dev.toolkt.math.algebra.linear.vectors.Vector3
import three.THREE

fun THREE.Vector3.toMathVector3() = Vector3(
    x = this.x,
    y = this.y,
    z = this.z,
)

fun Vector3.toThreeJsVector3() = THREE.Vector3(
    x = this.x,
    y = this.y,
    z = this.z,
)
