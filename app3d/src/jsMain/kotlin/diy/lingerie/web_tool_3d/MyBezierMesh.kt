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
            bezierCurve: CubicBezierBinomial,
            apexVertex: Vector3,
            color: Int,
        ): MyBezierMesh {
            val point0HandleBall = buildHandleBallMesh(
                position = Cell.of(bezierCurve.point0.toVector3(0.0)),
            )

            val point1HandleBall = buildHandleBallMesh(
                position = Cell.of(bezierCurve.point1.toVector3(0.0)),
            )

            val point2HandleBall = buildHandleBallMesh(
                position = Cell.of(bezierCurve.point2.toVector3(0.0)),
            )

            val point3HandleBall = buildHandleBallMesh(
                position = Cell.of(bezierCurve.point3.toVector3(0.0)),
            )

            val apexHandleBall = buildHandleBallMesh(
                position = Cell.of(apexVertex),
            )

            val handleBalls = listOf(
                point0HandleBall,
                point1HandleBall,
                point2HandleBall,
                point3HandleBall,
                apexHandleBall,
            )

            val bezierGeometryFactory = BezierGeometryFactory(
                apexVertex = apexVertex,
                bezierCurve = bezierCurve,
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
