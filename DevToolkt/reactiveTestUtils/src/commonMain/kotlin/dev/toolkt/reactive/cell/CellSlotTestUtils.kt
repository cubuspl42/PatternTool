package dev.toolkt.reactive.cell

import dev.toolkt.reactive.effect.Actions

fun <V> CellSlot.Companion.createExternally(
    initialValue: V,
): CellSlot<V> = Actions.external {
    CellSlot.create(initialValue = initialValue)
}

fun <ValueT> CellSlot<ValueT>.bindExternally(
    cell: Cell<ValueT>,
) = Actions.external {
    bind(cell)
}
