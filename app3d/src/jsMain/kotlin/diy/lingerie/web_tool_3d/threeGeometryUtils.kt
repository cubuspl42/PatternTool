package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.Ray3
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

    val targetWorldPoint = targetNdcPoint.unproject(camera = this@castRay)

    return Ray3.of(
        origin = this@castRay.worldPosition.toPoint3D(),
        target = targetWorldPoint,
    )
}

fun Point3D.project(
    camera: THREE.Camera,
): Point3D = toThreeJsVector3().apply {
    project(camera = camera)
}.toPoint3D()

fun Point3D.unproject(
    camera: THREE.Camera,
): Point3D = toThreeJsVector3().apply {
    unproject(camera = camera)
}.toPoint3D()

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
