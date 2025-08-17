package dev.toolkt.reactive.cell

import dev.toolkt.reactive.managed_io.Reactions

fun <V> CellSlot.Companion.createExternally(
    initialValue: V,
): CellSlot<V> = Reactions.external {
    CellSlot.create(initialValue = initialValue)
}

fun <ValueT> CellSlot<ValueT>.bindExternally(
    cell: Cell<ValueT>,
) = Reactions.external {
    bind(cell)
}
