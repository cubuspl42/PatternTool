package diy.lingerie.reactive.vertices.event_stream

import diy.lingerie.reactive.vertices.Vertex

class EmitterVertex<E> : Vertex<E>() {
    override val kind: String = "Emitter"

    override fun onResumed() {
    }

    override fun onPaused() {
    }
}
