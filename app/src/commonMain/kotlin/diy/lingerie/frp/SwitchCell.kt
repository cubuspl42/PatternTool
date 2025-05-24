package diy.lingerie.frp

class SwitchCell<V>(
    nestedCell: Cell<Cell<V>>,
) : ChangingCell<V>(
    initialValue = nestedCell.currentValue.currentValue,
) {
    init {
        nestedCell.newValues.subscribeFullyBound(
            target = this,
            listener = object : Listener<Cell<V>> {
                override fun handle(newInnerCell: Cell<V>) {
                    update(newInnerCell.currentValue)

                    resubscribeToInner(newInnerCell = newInnerCell)
                }
            },
        )
    }

    private var innerSubscription: Subscription = subscribeToInner(
        innerCell = nestedCell.currentValue,
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
        newInnerCell: Cell<V>,
    ) {
        innerSubscription.cancel()
        innerSubscription = subscribeToInner(innerCell = newInnerCell)
    }
}
