package dev.toolkt.reactive.managed_io

interface MomentContext

private class MomentContextImpl : MomentContext

object Moments {
    fun <ResultT> external(
        block: context(MomentContext) () -> ResultT,
    ): ResultT = with(MomentContextImpl()) {
        block()
    }
}
