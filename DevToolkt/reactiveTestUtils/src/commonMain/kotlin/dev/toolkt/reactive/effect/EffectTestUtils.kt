package dev.toolkt.reactive.effect

fun <ResultT> Effect<ResultT>.startExternally(): Effective<ResultT> = Actions.external {
    start()
}

fun Trigger.jumpStartExternally(): Effect.Handle = Actions.external {
    jumpStart()
}

fun Effect.Handle.endExternally() = Actions.external {
    end()
}
