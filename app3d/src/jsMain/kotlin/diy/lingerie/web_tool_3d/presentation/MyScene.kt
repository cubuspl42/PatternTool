package diy.lingerie.web_tool_3d.presentation

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureSize
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.Span
import dev.toolkt.reactive.cell.Cell
import diy.lingerie.web_tool_3d.presentation.MyBezierMesh
import diy.lingerie.web_tool_3d.application_state.ApplicationState
import diy.lingerie.web_tool_3d.application_state.FabricPiece
import kotlinx.browser.window
import three.THREE

private val lightPosition = Point3D(x = 20.0, y = 20.0, z = 20.0)

private val bezierMeshColor = PureColor.blue

private const val cameraDistance = 200.0

private const val cameraZ = 50.0

class MyScene(
    val floor: THREE.Object3D,
    val myCamera: MyCamera,
    val myBezierMesh: MyBezierMesh,
    val scene: THREE.Scene,
) {
    companion object {
        fun create(
            applicationState: ApplicationState,
            viewportSize: Cell<PureSize>,
            cameraRotation: Cell<Double>,
        ): MyScene {
            val myCamera = createMyCamera(
                height = cameraZ,
                distance = cameraDistance,
                viewportSize = viewportSize,
                rotation = cameraRotation,
            )

            val floorGrid = buildFloorGrid()

            val myBezierMesh = MyBezierMesh.Companion.create(
                interactionState = applicationState.interactionState,
                userBezierMesh = applicationState.documentState.userBezierMesh,
                color = bezierMeshColor.value,
            )


            val scene = createReactiveScene(
                listOf(
                    myCamera.wrapperGroup,
                    THREE.AmbientLight(),
                    createReactivePointLight(
                        position = Cell.of(lightPosition),
                    ),
//                    myBezierMesh.root,
                    floorGrid,
                    createFabricPieceObject3D(
                        fabricPiece = applicationState.simulationState.fabricPiece,
                    ),
                ),
            )

            return MyScene(
                myCamera = myCamera,
                floor = floorGrid,
                myBezierMesh = myBezierMesh,
                scene = scene,
            )
        }
    }

    val camera: THREE.PerspectiveCamera
        get() = myCamera.camera
}
