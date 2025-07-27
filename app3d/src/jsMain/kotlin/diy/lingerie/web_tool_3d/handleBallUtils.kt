package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.math.algebra.linear.vectors.times
import dev.toolkt.reactive.cell.Cell
import org.w3c.dom.CaretPosition
import three.Float32Array
import three.MeshLambertMaterialParams
import three.THREE

private const val handleBallRadius = 0.02

private val handleBallGeometry = THREE.SphereGeometry(
    radius = handleBallRadius,
)

private val handleBallMaterial = THREE.MeshLambertMaterial(
    params = MeshLambertMaterialParams(
        color = PureColor.red.value,
    ),
)

fun buildHandleBallMesh(
    position: Cell<Vector3>,
): THREE.Mesh {
    val sphereMesh = createReactiveMesh(
        geometry = handleBallGeometry,
        material = handleBallMaterial,
        position = position,
    )

    return sphereMesh
}
