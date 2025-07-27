package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell

class UserBezierMesh private constructor(
    val apexVertex: PropertyCell<Vector3>,
    val point0: PropertyCell<Vector2>,
    val point1: PropertyCell<Vector2>,
    val point2: PropertyCell<Vector2>,
    val point3: PropertyCell<Vector2>,
) {
    companion object {
        fun create(
            initialApexVertex: Vector3,
            initialBezierCurve: CubicBezierBinomial,
        ): UserBezierMesh = UserBezierMesh(
            apexVertex = PropertyCell(initialValue = initialApexVertex),
            point0 = PropertyCell(initialValue = initialBezierCurve.point0),
            point1 = PropertyCell(initialValue = initialBezierCurve.point1),
            point2 = PropertyCell(initialValue = initialBezierCurve.point2),
            point3 = PropertyCell(initialValue = initialBezierCurve.point3),
        )
    }

    val bezierCurve: Cell<CubicBezierBinomial> = Cell.map4(
        point0,
        point1,
        point2,
        point3,
    ) { point0Now, point1Now, point2Now, point3Now ->
        CubicBezierBinomial(
            point0 = point0Now,
            point1 = point1Now,
            point2 = point2Now,
            point3 = point3Now,
        )
    }
}
