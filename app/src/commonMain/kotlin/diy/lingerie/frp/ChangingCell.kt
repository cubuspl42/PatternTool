package diy.lingerie.frp

abstract class ChangingCell<V>(
    initialValue: V,
    private val notifierBase: NotifierBase<Change<V>> = NotifierBase(),
) : Cell<V>(), Notifier<Cell.Change<V>> by notifierBase {
    private var mutableValue: V = initialValue

    final override val currentValue: V
        get() = mutableValue

    final override val changes: EventStream<Change<V>>
        get() = ProxyEventStream(source = this)

    final override val newValues: EventStream<V>
        get() = changes.map { it.newValue }

    protected fun update(newValue: V) {
        val oldValue = mutableValue

        notifierBase.notify(
            Change(
                oldValue = oldValue,
                newValue = newValue,
            )
        )
    }
}
