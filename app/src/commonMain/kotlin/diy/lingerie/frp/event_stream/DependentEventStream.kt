package diy.lingerie.frp.event_stream

import diy.lingerie.frp.vertices.Vertex

internal class DependentEventStream<E>(
    override val vertex: Vertex<E>,
) : ActiveEventStream<E>()
