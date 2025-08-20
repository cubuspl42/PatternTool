package dev.toolkt.reactive.effect

data class Effective<out A>(
    val result: A,
    val handle: Effect.Handle,
) {
    companion object {
        fun <A> pure(
            result: A,
        ): Effective<A> = Effective(
            result = result,
            handle = Effect.Handle.Noop,
        )
    }

    fun <B> map(
        transform: (A) -> B,
    ): Effective<B> = Effective(
        result = transform(result),
        handle = handle,
    )
}
