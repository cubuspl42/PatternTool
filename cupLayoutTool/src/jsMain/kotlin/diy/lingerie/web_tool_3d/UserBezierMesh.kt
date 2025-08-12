package diy.lingerie.web_tool_3d

import dev.toolkt.geometry.Plane
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.PropertyCell

class UserBezierMesh private constructor(
    initialApexVertex: Point3D,
    initialBezierCurve: BezierCurve,
) {
    class Handle(
        initialPosition: Point,
    ) {
        companion object {
            val plane = Plane.Xy
        }

        val position = MutableCell(initialValue = initialPosition)

        val worldPosition: Cell<Point3D> = position.map { it.toPoint3D() }
    }

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

    val handle0 = Handle(
        initialPosition = initialBezierCurve.start,
    )

    val handle1 = Handle(
        initialPosition = initialBezierCurve.firstControl,
    )

    val handle2 = Handle(
        initialPosition = initialBezierCurve.secondControl,
    )

    val handle3 = Handle(
        initialPosition = initialBezierCurve.end,
    )

    val bezierCurve: Cell<BezierCurve> = Cell.map4(
        handle0.position,
        handle1.position,
        handle2.position,
        handle3.position,
    ) { handle0Position0Now, handle1PositionNow, handle2PositionNow, handle3PositionNow ->
        BezierCurve(
            start = handle0Position0Now,
            firstControl = handle1PositionNow,
            secondControl = handle2PositionNow,
            end = handle3PositionNow,
        )
    }
}
