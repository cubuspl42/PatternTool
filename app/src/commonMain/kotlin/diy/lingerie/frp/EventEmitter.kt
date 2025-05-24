package diy.lingerie.frp

class EventEmitter<E> : ActiveEventStream<E>() {
    override val vertex: Vertex<E> = EmitterVertex()

    fun emit(event: E) {
        vertex.notify(event)
    }
}
