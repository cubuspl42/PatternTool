package diy.lingerie.cup_layout_tool

import dev.toolkt.geometry.Plane
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.effect.MomentContext

class UserBezierMesh private constructor(
    val apexPosition: PropertyCell<Point3D>,
    val handle0: UserBezierMesh.Handle,
    val handle1: UserBezierMesh.Handle,
    val handle2: UserBezierMesh.Handle,
    val handle3: UserBezierMesh.Handle,

    ) {
    class Handle private constructor(
        val position: MutableCell<Point>,
    ) {
        companion object {
            val plane = Plane.Xy

            context(momentContext: MomentContext) fun create(

                initialPosition: Point,
            ): Handle = Handle(
                position = MutableCell.create(initialValue = initialPosition),
            )
        }

        val worldPosition: Cell<Point3D> = position.map { it.toPoint3D() }
    }

    companion object {
        context(momentContext: MomentContext) fun create(
            initialApexVertex: Point3D,
            initialBezierCurve: BezierCurve,
        ): UserBezierMesh = UserBezierMesh(
            apexPosition = PropertyCell.create(
                initialValue = initialApexVertex,
            ),
            handle0 = Handle.create(
                initialPosition = initialBezierCurve.start,
            ),
            handle1 = Handle.create(
                initialPosition = initialBezierCurve.firstControl,
            ),
            handle2 = Handle.create(
                initialPosition = initialBezierCurve.secondControl,
            ),
            handle3 = Handle.create(
                initialPosition = initialBezierCurve.end,
            ),
        )
    }

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
