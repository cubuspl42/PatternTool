package dev.toolkt.reactive.future

import dev.toolkt.reactive.managed_io.Proactions

fun <ValueT> FutureCompleter<ValueT>.completeExternally(
    value: ValueT,
) = Proactions.external {
    complete(value)
}
