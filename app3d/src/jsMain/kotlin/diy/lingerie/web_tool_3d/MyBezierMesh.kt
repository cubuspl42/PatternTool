package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.reactive.cell.Cell
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
            userBezierMesh: UserBezierMesh,
            color: Int,
        ): MyBezierMesh {
            val bezierCurve = userBezierMesh.bezierCurve
            val apexVertex = userBezierMesh.apexVertex

            val point0HandleBall = buildHandleBallMesh(
                position = userBezierMesh.point0,
            )

            val point1HandleBall = buildHandleBallMesh(
                position = userBezierMesh.point1,
            )

            val point2HandleBall = buildHandleBallMesh(
                position = userBezierMesh.point2,
            )

            val point3HandleBall = buildHandleBallMesh(
                position = userBezierMesh.point3,
            )

            val apexHandleBall = buildHandleBallMesh(
                position = userBezierMesh.apexVertex,
            )

            val handleBalls = listOf(
                point0HandleBall,
                point1HandleBall,
                point2HandleBall,
                point3HandleBall,
                apexHandleBall,
            )

            val bezierGeometryFactory = BezierGeometryFactory(
                apexVertex = apexVertex.currentValue,
                bezierCurve = bezierCurve.currentValue,
            )

            val bezierMeshGroup = createReactiveDualMeshGroup(
                position = Cell.of(Vector3.Zero),
                geometry = bezierGeometryFactory.createGeometry(),
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
