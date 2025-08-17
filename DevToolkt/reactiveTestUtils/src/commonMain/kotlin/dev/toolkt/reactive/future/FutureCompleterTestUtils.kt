package dev.toolkt.reactive.future

import dev.toolkt.reactive.managed_io.Reactions

fun <ValueT> FutureCompleter<ValueT>.completeExternally(
    value: ValueT,
) = Reactions.external {
    complete(value)
}
