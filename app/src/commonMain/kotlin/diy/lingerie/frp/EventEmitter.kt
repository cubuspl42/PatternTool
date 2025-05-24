package diy.lingerie.frp

class EventEmitter<E> : NotifyingStream<E>() {
    fun emit(event: E) {
        notify(event)
    }
}
