package diy.lingerie.reactive.vertices.cell

import diy.lingerie.reactive.HybridSubscription
import diy.lingerie.reactive.Listener
import diy.lingerie.reactive.cell.ActiveCell
import diy.lingerie.reactive.cell.Cell
import diy.lingerie.reactive.cell.ConstCell

class SwitchCellVertex<V>(
    private val nestedCell: CellVertex<Cell<V>>,
) : DependentCellVertex<V>(
    initialValue = nestedCell.currentValue.currentValue,
) {
    override val kind: String = "Switch"

    override fun buildHybridSubscription() = object : HybridSubscription {
        private val outerSubscription = nestedCell.subscribeHybrid(
            listener = object : Listener<Cell.Change<Cell<V>>> {
                override fun handle(change: Cell.Change<Cell<V>>) {
                    val newInnerCell = change.newValue

                    update(newInnerCell.currentValue)

                    resubscribeToInner(newInnerCell = newInnerCell)
                }
            },
        )

        private var innerSubscription = subscribeToInner(
            innerCell = nestedCell.currentValue,
        )

        private fun subscribeToInner(
            innerCell: Cell<V>,
        ): HybridSubscription = when (innerCell) {
            is ActiveCell<V> -> {
                innerCell.vertex.subscribeHybrid(
                    listener = object : Listener<Cell.Change<V>> {
                        override fun handle(change: Cell.Change<V>) {
                            val newValue = change.newValue

                            update(newValue)
                        }
                    },
                )
            }

            is ConstCell<V> -> HybridSubscription.Noop
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

        override fun updateStrength(newStrength: ListenerStrength) {
            outerSubscription.updateStrength(newStrength = newStrength)
            innerSubscription.updateStrength(newStrength = newStrength)
        }
    }

    init {
        init()
    }
}
