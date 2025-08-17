package dev.toolkt.reactive.cell

import dev.toolkt.reactive.future.FutureCompleter
import dev.toolkt.reactive.managed_io.Proactions

fun <ValueT> FutureCompleter<ValueT>.completeExternally(
    value: ValueT,
) = Proactions.external {
    complete(value)
}
