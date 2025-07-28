package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureSize
import dev.toolkt.geometry.Vector3
import dev.toolkt.reactive.cell.Cell
import diy.lingerie.web_tool_3d.application_state.DocumentState
import three.THREE

private val lightPosition = Vector3(x = 1.0, y = 1.0, z = 1.0)

private val bezierMeshColor = PureColor.blue

private const val cameraDistance = 2.0

private const val cameraZ = 0.5

class MyScene(
    val floor: THREE.Object3D,
    val myCamera: MyCamera,
    val myBezierMesh: MyBezierMesh,
    val scene: THREE.Scene,
) {
    companion object {
        fun create(
            documentState: DocumentState,
            viewportSize: Cell<PureSize>,
            cameraRotation: Cell<Double>,
        ): MyScene {
            val myCamera = createMyCamera(
                height = cameraZ,
                distance = cameraDistance,
                viewportSize = viewportSize,
                rotation = cameraRotation,
            )

            val floor = buildFloor()

            val myBezierMesh = MyBezierMesh.create(
                userBezierMesh = documentState.userBezierMesh,
                color = bezierMeshColor.value,
            )

            val scene = createReactiveScene(
                listOf(
                    myCamera.wrapperGroup,
                    THREE.AmbientLight(),
                    createReactivePointLight(
                        position = Cell.of(lightPosition),
                    ),
                    myBezierMesh.root,
                    floor,
                ),
            )

            return MyScene(
                myCamera = myCamera,
                floor = floor,
                myBezierMesh = myBezierMesh,
                scene = scene,
            )
        }
    }

    val camera: THREE.PerspectiveCamera
        get() = myCamera.camera
}
