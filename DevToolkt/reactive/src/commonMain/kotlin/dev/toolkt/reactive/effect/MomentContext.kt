package dev.toolkt.reactive.effect

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
