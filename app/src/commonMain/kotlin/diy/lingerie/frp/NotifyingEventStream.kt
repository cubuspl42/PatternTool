package diy.lingerie.frp

abstract class NotifyingEventStream<E>(
    private val notifierBase: NotifierBase<E> = NotifierBase(),
) : EventStream<E>(), Notifier<E> by notifierBase {
    protected fun send(
        event: E,
    ) {
        notifierBase.notify(event)
    }

    protected open fun onResumed() {}

    protected open fun onPaused() {}
}
