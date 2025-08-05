package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell

class UserBezierMesh private constructor(
    initialApexVertex: Point3D,
    initialBezierCurve: BezierCurve,
) {
    companion object {
        fun create(
            initialApexVertex: Point3D,
            initialBezierCurve: BezierCurve,
        ): UserBezierMesh = UserBezierMesh(
            initialApexVertex = initialApexVertex,
            initialBezierCurve = initialBezierCurve,
        )
    }

    val apexPosition = PropertyCell(initialValue = initialApexVertex)

    val point0 = PropertyCell(initialValue = initialBezierCurve.firstControl)

    val point1 = PropertyCell(initialValue = initialBezierCurve.secondControl)

    val point2 = PropertyCell(initialValue = initialBezierCurve.end)

    val point3 = PropertyCell(initialValue = initialBezierCurve.start)

    val bezierCurve: Cell<BezierCurve> = Cell.map4(
        point0,
        point1,
        point2,
        point3,
    ) { point0Now, point1Now, point2Now, point3Now ->
        BezierCurve(
            start = point0Now,
            firstControl = point1Now,
            secondControl = point2Now,
            end = point3Now,
        )
    }
}
