package diy.lingerie.frp

class SwitchCellVertex<V>(
    private val nestedCell: CellVertex<Cell<V>>,
) : DependentCellVertex<V>(
    initialValue = nestedCell.currentValue.currentValue,
) {
    override fun buildInitialSubscription(): Subscription = object : Subscription {
        val outerSubscription = nestedCell.subscribe(
            listener = object : Listener<Cell.Change<Cell<V>>> {
                override fun handle(change: Cell.Change<Cell<V>>) {
                    val newInnerCell = change.newValue

                    update(newInnerCell.currentValue)

                    resubscribeToInner(newInnerCell = newInnerCell)
                }
            },
        )

        private var innerSubscription: Subscription = subscribeToInner(
            innerCell = nestedCell.currentValue,
        )

        private fun subscribeToInner(
            innerCell: Cell<V>,
        ): Subscription = when (innerCell) {
            is ActiveCell<V> -> {
                innerCell.vertex.subscribe(
                    listener = object : Listener<Cell.Change<V>> {
                        override fun handle(change: Cell.Change<V>) {
                            val newValue = change.newValue

                            update(newValue)
                        }
                    },
                )
            }

            is ConstCell<V> -> Subscription.Noop
        }

        private fun resubscribeToInner(
            newInnerCell: Cell<V>,
        ) {
            innerSubscription.cancel()
            innerSubscription = subscribeToInner(innerCell = newInnerCell)
        }


        override fun cancel() {
            outerSubscription.cancel()
            innerSubscription.cancel()
        }

        override fun change(strength: Vertex.ListenerStrength) {
            outerSubscription.change(strength = strength)
            innerSubscription.change(strength = strength)
        }
    }

    init {
        init()
    }
}
