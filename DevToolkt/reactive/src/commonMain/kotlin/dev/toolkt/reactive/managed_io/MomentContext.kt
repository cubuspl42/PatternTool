package dev.toolkt.reactive.managed_io

abstract class MomentContext
private class MomentContextImpl : MomentContext()

object Moments {
    fun <ResultT> external(
        block: context(MomentContext) () -> ResultT,
    ): ResultT = with(MomentContextImpl()) {
        block()
    }
}
