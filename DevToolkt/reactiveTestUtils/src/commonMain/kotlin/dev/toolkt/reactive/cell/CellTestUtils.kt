package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.effect.Moments

fun <ValueT> MutableCell.Companion.createExternally(
    initialValue: ValueT,
): MutableCell<ValueT> = Moments.external {
    MutableCell.create(initialValue = initialValue)
}

fun <ValueT> MutableCell<ValueT>.setExternally(
    newValue: ValueT,
) = Actions.external {
    set(newValue = newValue)
}

fun <ValueT> Cell<ValueT>.sampleExternally(): ValueT = Actions.external {
    sample()
}

fun <ValueT> EventStream<ValueT>.holdExternally(initialValue: ValueT): Cell<ValueT> = Moments.external {
    hold(initialValue = initialValue)
}
