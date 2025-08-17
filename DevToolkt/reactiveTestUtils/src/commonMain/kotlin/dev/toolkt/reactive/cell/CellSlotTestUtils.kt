package dev.toolkt.reactive.cell

import dev.toolkt.reactive.managed_io.Reactions

fun <ValueT> CellSlot<ValueT>.bindExternally(
    cell: Cell<ValueT>,
) = Reactions.external {
    bind(cell)
}
