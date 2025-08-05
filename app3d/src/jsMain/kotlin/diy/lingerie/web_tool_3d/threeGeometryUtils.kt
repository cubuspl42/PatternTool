package diy.lingerie.web_tool_3d

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.transformations.Transformation3D
import three.THREE

val THREE.Camera.projectionTransformation: Transformation3D
    get() = object : Transformation3D() {
        override fun transform(
            point: Point3D,
        ): Point3D = point.project(camera = this@projectionTransformation)

        override fun invert(): Transformation3D = unprojectionTransformation

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericTolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }
    }

val THREE.Camera.unprojectionTransformation: Transformation3D
    get() = object : Transformation3D() {
        override fun transform(
            point: Point3D,
        ): Point3D = point.unproject(camera = this@unprojectionTransformation)

        override fun invert(): Transformation3D = projectionTransformation

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericTolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }
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
