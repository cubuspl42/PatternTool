package dev.toolkt.reactive.managed_io

interface MomentContext {
    val transaction: Transaction
}

object Moments {
    fun <ResultT> external(
        block: context(MomentContext) () -> ResultT,
    ): ResultT = Actions.external(
        block = block,
    )
}
