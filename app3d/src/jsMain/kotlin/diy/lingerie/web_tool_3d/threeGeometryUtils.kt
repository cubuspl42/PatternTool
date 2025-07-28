package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.Ray
import dev.toolkt.geometry.Ray3
import dev.toolkt.geometry.z
import three.THREE
import three.worldPosition

fun THREE.Camera.castRay(
    /**
     * A 2D camera NDC point
     */
    ndcPoint: Point,
): Ray3 {
    // A 3D NDC target point in the far direction
    val targetNdcPoint = ndcPoint.toPoint3D(z = 1.0)

    val targetWorldPoint = targetNdcPoint.toThreeJsVector3().apply {
        unproject(camera = this@castRay)
    }.toPoint3D()

    return Ray3.of(
        origin = this@castRay.worldPosition.toPoint3D(),
        target = targetWorldPoint,
    )
}

fun Point3D.toThreeJsVector3() = THREE.Vector3(
    x = this.x,
    y = this.y,
    z = this.z,
)

fun THREE.Vector3.toPoint3D() = Point3D(
    x = this.x,
    y = this.y,
    z = this.z,
)
