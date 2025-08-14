package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.geometry.Point3D

class BezierCurve3D(
    val start: Point3D,
    val firstControl: Point3D,
    val secondControl: Point3D,
    val end: Point3D,
) {
    fun evaluateAt(t: Double): Point3D {
        TODO()
    }
}
