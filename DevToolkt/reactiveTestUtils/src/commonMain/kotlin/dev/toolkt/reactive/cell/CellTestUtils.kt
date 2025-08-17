package dev.toolkt.reactive.cell

import dev.toolkt.reactive.managed_io.Actions

fun <V> MutableCell.Companion.createExternally(
    initialValue: V,
): MutableCell<V> = Actions.external {
    MutableCell.create(initialValue = initialValue)
}

fun <V> MutableCell<V>.setExternally(
    newValue: V,
) = Actions.external {
    set(newValue = newValue)
}

fun <V> Cell<V>.sampleExternally(): V = Actions.external {
    sample()
}
