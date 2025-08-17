package dev.toolkt.reactive.managed_io

fun <ResultT> Effect<ResultT>.startExternally(): Effective<ResultT> = Reactions.external {
    start()
}

fun Trigger.jumpStartExternally(): Effect.Handle = Reactions.external {
    jumpStart()
}

fun Effect.Handle.endExternally() = Reactions.external {
    end()
}
