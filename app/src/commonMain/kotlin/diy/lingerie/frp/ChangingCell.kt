package diy.lingerie.frp

abstract class ChangingCell<V>(
    initialValue: V,
) : Cell<V>() {
    private val changesEmitter = EventEmitter<Change<V>>()

    private var mutableValue: V = initialValue

    final override val currentValue: V
        get() = mutableValue

    final override val changes: EventStream<Change<V>>
        get() = changesEmitter

    protected fun update(newValue: V) {
        val oldValue = mutableValue

        mutableValue = newValue

        changesEmitter.emit(
            Change(
                oldValue = oldValue,
                newValue = newValue,
            ),
        )
    }
}

