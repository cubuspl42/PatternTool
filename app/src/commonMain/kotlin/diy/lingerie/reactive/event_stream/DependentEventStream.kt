package diy.lingerie.reactive.event_stream

import diy.lingerie.reactive.vertices.Vertex

internal class DependentEventStream<E>(
    override val vertex: Vertex<E>,
) : ActiveEventStream<E>()
