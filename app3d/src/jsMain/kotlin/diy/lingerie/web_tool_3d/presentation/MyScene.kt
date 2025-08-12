package diy.lingerie.web_tool_3d.presentation

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureSize
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.Span
import dev.toolkt.reactive.cell.Cell
import diy.lingerie.web_tool_3d.application_state.ApplicationState
import diy.lingerie.web_tool_3d.application_state.FlatFabricPiece
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

            val cornerCoordTl = FlatFabricPiece.ThreadCoord(i = 1, j = 1)
            val cornerCoordTr = FlatFabricPiece.ThreadCoord(i = 1, j = 3)
            val cornerCoordBl = FlatFabricPiece.ThreadCoord(i = 3, j = 1)
            val cornerCoordBr = FlatFabricPiece.ThreadCoord(i = 3, j = 3)

            val topCoord = FlatFabricPiece.ThreadCoord(i = 1, j = 2)
            val leftCoord = FlatFabricPiece.ThreadCoord(i = 2, j = 1)
            val rightCoord = FlatFabricPiece.ThreadCoord(i = 2, j = 3)
            val bottomCoord = FlatFabricPiece.ThreadCoord(i = 3, j = 2)

            val centralCoord = FlatFabricPiece.ThreadCoord(i = 2, j = 2)

            val threadGap = 20.0
            val width = threadGap * 4

            val tinyLength = Span.of(0.25 * threadGap)
            val smallLength = Span.of(0.5 * threadGap)

            val flatFabricPiece = FlatFabricPiece(
                threadGap = threadGap,
                innerCoords = setOf(
                    cornerCoordTl,
                    topCoord,
                    cornerCoordTr,
                    leftCoord,
                    centralCoord,
                    rightCoord,
                    cornerCoordBl,
                    bottomCoord,
                    cornerCoordBr,
                ),
                edges = listOf(
                    FlatFabricPiece.Edge(
                        start = Point(0.0, 0.0),
                        tassels = listOf(
                            FlatFabricPiece.Tassel(
                                baseCoord = cornerCoordTl,
                                threadDirection = FlatFabricPiece.ThreadDirection.IMinus,
                                length = smallLength,
                            ),
                            FlatFabricPiece.Tassel(
                                baseCoord = topCoord,
                                threadDirection = FlatFabricPiece.ThreadDirection.IMinus,
                                length = tinyLength,
                            ),
                            FlatFabricPiece.Tassel(
                                baseCoord = cornerCoordTr,
                                threadDirection = FlatFabricPiece.ThreadDirection.IMinus,
                                length = smallLength,
                            ),
                        ),
                    ),
                    FlatFabricPiece.Edge(
                        start = Point(width, 0.0),
                        tassels = listOf(
                            FlatFabricPiece.Tassel(
                                baseCoord = cornerCoordTr,
                                threadDirection = FlatFabricPiece.ThreadDirection.JPlus,
                                length = smallLength,
                            ),
                            FlatFabricPiece.Tassel(
                                baseCoord = rightCoord,
                                threadDirection = FlatFabricPiece.ThreadDirection.JPlus,
                                length = tinyLength,
                            ),
                            FlatFabricPiece.Tassel(
                                baseCoord = cornerCoordBr,
                                threadDirection = FlatFabricPiece.ThreadDirection.JPlus,
                                length = smallLength,
                            ),
                        ),
                    ),
                    FlatFabricPiece.Edge(
                        start = Point(width, width),
                        tassels = listOf(
                            FlatFabricPiece.Tassel(
                                baseCoord = cornerCoordBr,
                                threadDirection = FlatFabricPiece.ThreadDirection.IPlus,
                                length = smallLength,
                            ),
                            FlatFabricPiece.Tassel(
                                baseCoord = bottomCoord,
                                threadDirection = FlatFabricPiece.ThreadDirection.IPlus,
                                length = tinyLength,
                            ),
                            FlatFabricPiece.Tassel(
                                baseCoord = cornerCoordBl,
                                threadDirection = FlatFabricPiece.ThreadDirection.IPlus,
                                length = smallLength,
                            ),
                        ),
                    ),
                    FlatFabricPiece.Edge(
                        start = Point(0.0, width),
                        tassels = listOf(
                            FlatFabricPiece.Tassel(
                                baseCoord = cornerCoordBl,
                                threadDirection = FlatFabricPiece.ThreadDirection.JMinus,
                                length = smallLength,
                            ),
                            FlatFabricPiece.Tassel(
                                baseCoord = leftCoord,
                                threadDirection = FlatFabricPiece.ThreadDirection.JMinus,
                                length = tinyLength,
                            ),
                            FlatFabricPiece.Tassel(
                                baseCoord = cornerCoordTl,
                                threadDirection = FlatFabricPiece.ThreadDirection.JMinus,
                                length = smallLength,
                            ),
                        ),
                    ),
                ),
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
//                    createFabricPieceObject3D(
//                        fabricPiece = applicationState.simulationState.fabricNet,
//                    ),
                    createFlatFabricPieceObject3D(
                        flatFabricPiece = flatFabricPiece,
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
