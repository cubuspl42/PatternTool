package diy.lingerie.web_tool

import dev.toolkt.geometry.curves.PrimitiveCurve
import dev.toolkt.reactive.cell.Cell

sealed class UserCurve<CurveT : PrimitiveCurve> {
    abstract val reactiveCurve: ReactiveCurve<CurveT>

    val primitiveCurve: Cell<CurveT>
        get() = reactiveCurve.primitiveCurve
}
