package dev.toolkt.reactive.future

import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.effect.Moments

fun <ResultT> FutureCompleter.Companion.createExternally(): FutureCompleter<ResultT> = Moments.external {
    FutureCompleter.create()
}

fun <ResultT> FutureCompleter<ResultT>.completeExternally(
    value: ResultT,
) {
    Actions.external {
        complete(value)
    }
}
