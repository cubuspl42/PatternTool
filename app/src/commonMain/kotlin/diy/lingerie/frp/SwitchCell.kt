package diy.lingerie.frp

class SwitchCell<V>(
    outerCell: Cell<Cell<V>>,
) : ChangingCell<V>(
    initialValue = outerCell.currentValue.currentValue,
) {
    init {
        outerCell.newValues.subscribeFullyBound(
            target = this,
            listener = object : Listener<Cell<V>> {
                override fun handle(innerCell: Cell<V>) {
                    update(innerCell.currentValue)

                    resubscribeToInner(innerCell)
                }
            },
        )
    }

    private var innerSubscription: Subscription = subscribeToInner(
        innerCell = outerCell.currentValue
    )

    private fun subscribeToInner(
        innerCell: Cell<V>,
    ): Subscription = innerCell.newValues.subscribeSemiBound(
        target = this,
        listener = object : Listener<V> {
            override fun handle(newValue: V) {
                update(newValue)

                resubscribeToInner(innerCell)
            }
        },
    )

    private fun resubscribeToInner(
        innerCell: Cell<V>,
    ) {
        innerSubscription.cancel()
        innerSubscription = subscribeToInner(innerCell)
    }
}
