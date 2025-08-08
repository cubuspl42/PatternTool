package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.geometry.Point3D
import dev.toolkt.reactive.cell.Cell
import diy.lingerie.web_tool_3d.application_state.InteractionState
import three.MeshBasicMaterialParams
import three.MeshLambertMaterialParams
import three.THREE

class MyBezierMesh(
    val root: THREE.Object3D,
    val point0HandleBall: THREE.Object3D,
    val point1HandleBall: THREE.Object3D,
    val point2HandleBall: THREE.Object3D,
    val point3HandleBall: THREE.Object3D,
    val apexHandleBall: THREE.Object3D,
) {
    companion object {
        fun create(
            interactionState: InteractionState,
            userBezierMesh: UserBezierMesh,
            color: Int,
        ): MyBezierMesh {
            val bezierCurve = userBezierMesh.bezierCurve
            val apexVertex = userBezierMesh.apexPosition

            val point0HandleBall = buildFlatHandleBallMesh(
                handle = userBezierMesh.handle0,
                interactionState=interactionState,
            )

            val point1HandleBall = buildFlatHandleBallMesh(
                handle = userBezierMesh.handle1,
                interactionState=interactionState,
            )

            val point2HandleBall = buildFlatHandleBallMesh(
                handle = userBezierMesh.handle2,
                interactionState=interactionState,
            )

            val point3HandleBall = buildFlatHandleBallMesh(
                handle = userBezierMesh.handle3,
                interactionState=interactionState,
            )

            val apexHandleBall = buildHandleBallMesh(
                position = userBezierMesh.apexPosition,
            )

            val handleBalls = listOf(
                point0HandleBall,
                point1HandleBall,
                point2HandleBall,
                point3HandleBall,
                apexHandleBall,
            )

            val bezierMeshGroup = createReactiveDualMeshGroup(
                position = Cell.of(Point3D.origin),
                geometry = createReactiveGeometry(
                    createUserBezierMeshGeometryData(
                        userBezierMesh = userBezierMesh,
                    ),
                ),
                primaryMaterial = THREE.MeshLambertMaterial(
                    params = MeshLambertMaterialParams(
                        color = color,
                    ),
                ),
                secondaryMaterial = THREE.MeshBasicMaterial(
                    MeshBasicMaterialParams(
                        color = PureColor.green.value,
                        wireframe = true,
                        transparent = true,
                        opacity = 0.25,
                    ),
                ),
                rotation = Cell.of(THREE.Euler()),
            )

            return MyBezierMesh(
                root = createReactiveGroup(
                    children = listOf(bezierMeshGroup) + handleBalls,
                ),
                point0HandleBall = point0HandleBall,
                point1HandleBall = point1HandleBall,
                point2HandleBall = point2HandleBall,
                point3HandleBall = point3HandleBall,
                apexHandleBall = apexHandleBall,
            )
        }
    }

    val handleBalls: List<THREE.Object3D>
        get() = listOf(
            point0HandleBall,
            point1HandleBall,
            point2HandleBall,
            point3HandleBall,
            apexHandleBall,
        )
}
