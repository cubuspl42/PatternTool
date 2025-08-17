package dev.toolkt.reactive.cell

import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.Effective
import dev.toolkt.reactive.managed_io.Proactions

fun <ResultT> Effect<ResultT>.startExternally(): Effective<ResultT> = Proactions.external {
    start()
}

fun Effect.Handle.endExternally() = Proactions.external {
    end()
}
