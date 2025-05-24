package diy.lingerie.frp

internal class DependentEventStream<E>(
    override val vertex: Vertex<E>,
) : ActiveEventStream<E>()
