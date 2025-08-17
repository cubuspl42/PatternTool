package dev.toolkt.reactive.cell

import dev.toolkt.reactive.managed_io.Proactions
import dev.toolkt.reactive.managed_io.Reactions

fun <V> MutableCell.Companion.createExternally(
    initialValue: V,
): MutableCell<V> = Reactions.external {
    MutableCell.create(initialValue = initialValue)
}

fun <V> MutableCell<V>.setExternally(
    newValue: V,
) = Proactions.external {
    set(newValue = newValue)
}

fun <V> Cell<V>.sampleExternally(): V = Reactions.external {
    sample()
}
