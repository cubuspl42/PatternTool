package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.math.algebra.linear.vectors.Vector3

data class UserBezierMesh(
    val apexVertex: Vector3,
    val bezierCurve: CubicBezierBinomial,
)
