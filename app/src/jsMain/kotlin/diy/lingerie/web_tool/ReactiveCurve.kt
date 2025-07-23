package diy.lingerie.web_tool

import dev.toolkt.geometry.curves.PrimitiveCurve
import dev.toolkt.reactive.cell.Cell

sealed class ReactiveCurve<CurveT : PrimitiveCurve> {
    abstract val primitiveCurve: Cell<CurveT>
}
