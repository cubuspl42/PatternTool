package diy.lingerie.frp

class EventEmitter<E> : NotifyingEventStream<E>() {
    fun emit(event: E) {
        send(event)
    }
}
