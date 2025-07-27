package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureSize
import dev.toolkt.geometry.Vector3
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.reactive.cell.Cell
import three.THREE

private val lightPosition = Vector3(x = 1.0, y = 1.0, z = 1.0)

private val bezierMeshColor = PureColor.blue

private const val cameraDistance = 2.0

private const val cameraZ = 0.5

class MyScene(
    val myBezierMesh: MyBezierMesh,
    val myCamera: MyCamera,
    val scene: THREE.Scene,
) {
    companion object {
        fun create(
            userSystem: UserSystem,
            viewportSize: Cell<PureSize>,
            cameraRotation: Cell<Double>,
        ): MyScene {
            val floor = buildFloor()

            val myBezierMesh = MyBezierMesh.create(
                userBezierMesh = userSystem.userBezierMesh,
                color = bezierMeshColor.value,
            )

            val myCamera = createMyCamera(
                height = cameraZ,
                distance = cameraDistance,
                viewportSize = viewportSize,
                rotation = cameraRotation,
            )

            val scene = createReactiveScene(
                listOf(
                    THREE.AmbientLight(),
                    createReactivePointLight(
                        position = Cell.of(lightPosition),
                    ),
                    myBezierMesh.root,
                    myCamera.wrapperGroup,
                    floor,
                ),
            )

            return MyScene(
                myBezierMesh = myBezierMesh,
                scene = scene,
                myCamera = myCamera,
            )
        }
    }

    val camera: THREE.PerspectiveCamera
        get() = myCamera.camera
}
