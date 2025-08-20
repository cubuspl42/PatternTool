package dev.toolkt.reactive.future

import dev.toolkt.reactive.managed_io.Actions

fun <ResultT> FutureCompleter<ResultT>.completeExternally(
    value: ResultT,
) = Actions.external {
    complete(value)
}
