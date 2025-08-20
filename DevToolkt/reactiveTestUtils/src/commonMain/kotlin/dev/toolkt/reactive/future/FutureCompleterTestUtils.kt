package dev.toolkt.reactive.future

import dev.toolkt.reactive.effect.Actions

fun <ResultT> FutureCompleter<ResultT>.completeExternally(
    value: ResultT,
) = Actions.external {
    complete(value)
}
