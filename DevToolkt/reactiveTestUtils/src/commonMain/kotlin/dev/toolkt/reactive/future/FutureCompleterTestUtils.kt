package dev.toolkt.reactive.future

import dev.toolkt.reactive.managed_io.Actions

fun <ValueT> FutureCompleter<ValueT>.completeExternally(
    value: ValueT,
) = Actions.external {
    complete(value)
}
