package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.reactive.cell.Cell

data class UserBezierMesh private constructor(
    val apexVertex: Cell<Vector3>,
    val bezierCurve: Cell<CubicBezierBinomial>,
) {
    companion object {
        fun create(
            initialApexVertex: Vector3,
            initialBezierCurve: CubicBezierBinomial,
        ): UserBezierMesh = UserBezierMesh(
            apexVertex = Cell.of(initialApexVertex),
            bezierCurve = Cell.of(initialBezierCurve),
        )
    }

    val point0: Cell<Vector3>
        get() = bezierCurve.map { it.point0.toVector3(0.0) }

    val point1: Cell<Vector3>
        get() = bezierCurve.map { it.point1.toVector3(0.0) }

    val point2: Cell<Vector3>
        get() = bezierCurve.map { it.point2.toVector3(0.0) }

    val point3: Cell<Vector3>
        get() = bezierCurve.map { it.point3.toVector3(0.0) }
}
