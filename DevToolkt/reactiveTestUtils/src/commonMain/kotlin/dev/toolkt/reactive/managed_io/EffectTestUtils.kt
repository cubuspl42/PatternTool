package dev.toolkt.reactive.managed_io

fun <ResultT> Effect<ResultT>.startExternally(): Effective<ResultT> = Proactions.external {
    start()
}

fun Trigger.jumpStartExternally(): Effect.Handle = Proactions.external {
    jumpStart()
}

fun Effect.Handle.endExternally() = Proactions.external {
    end()
}
