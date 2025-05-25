package diy.lingerie.frp.vertices.event_stream

import diy.lingerie.frp.vertices.Vertex

class EmitterVertex<E> : Vertex<E>() {
    override val kind: String = "Emitter"

    override fun onResumed() {
    }

    override fun onPaused() {
    }
}
