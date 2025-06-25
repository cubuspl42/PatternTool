package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.collections.mutableIndexedListOf
import dev.toolkt.core.iterable.removeRange
import dev.toolkt.core.range.single
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream

class FuseReactiveListOperator<E>(
    private val source: ReactiveList<Cell<E>>,
) : ReactiveListPureOperator<E>() {
    class ChangesEventStream<E>(
        private val source: ReactiveList<Cell<E>>,
    ) : DependentEventStream<ReactiveList.Change<E>>() {
        // Thought: This subscription object is quite heavy (has the size
        // of the original list), while a separate subscription is created for
        // each observer. This could be potentially improved by some sharing
        // mechanism depending on weak caching. Switch to StatefulEventStream?
        override fun observe(): Subscription = object : Subscription {
            private val outerSubscription = source.changes.listen { outerChange ->
                val update = outerChange.update
                val indexRange = update.indexRange
                val updatedCells = update.updatedElements

                indexRange.forEach { index ->
                    val innerSubscription = innerSubscriptions.getOrNull(index)
                        ?: throw IllegalStateException("No subscription found for index $index.")

                    innerSubscription.cancel()
                }

                innerSubscriptions.removeRange(
                    indexRange = indexRange,
                )

                updatedCells.reversed().forEach { updatedCell ->
                    subscribeToInner(
                        index = indexRange.first,
                        innerCell = updatedCell,
                    )
                }

                this@ChangesEventStream.notify(
                    ReactiveList.Change.single(
                        update = ReactiveList.Change.Update.change(
                            indexRange = indexRange,
                            changedElements = updatedCells.map { it.currentValue },
                        ),
                    ),
                )
            }

            private val innerSubscriptions = mutableIndexedListOf<Subscription>()

            init {
                source.currentElements.forEachIndexed { index, cell ->
                    subscribeToInner(
                        index = index,
                        innerCell = cell,
                    )
                }
            }

            private fun subscribeToInner(
                index: Int,
                innerCell: Cell<E>,
            ) {
                val newInnerHandle = innerSubscriptions.addAtEx(
                    index = index,
                    element = Subscription.Noop, // A temporary value
                )

                val newInnerSubscription = innerCell.newValues.listenWeak(
                    target = newInnerHandle,
                ) { innerHandle, newInnerValue ->
                    val currentIndex = innerSubscriptions.indexOfVia(handle = innerHandle)

                    this@ChangesEventStream.notify(
                        ReactiveList.Change.single(
                            update = ReactiveList.Change.Update.change(
                                indexRange = IntRange.single(currentIndex),
                                changedElements = listOf(newInnerValue),
                            ),
                        ),
                    )
                }

                innerSubscriptions.setVia(
                    handle = newInnerHandle,
                    element = newInnerSubscription,
                )
            }

            override fun cancel() {
                // Observation: Removing this block clearly makes the implementation
                // obviously invalid, but no tests are currently testing this properly
                outerSubscription.cancel()
                innerSubscriptions.forEach { it.cancel() }
                innerSubscriptions.clear()
            }
        }
    }

    override fun getCurrentContent(): List<E> = source.currentElements.map {
        it.currentValue
    }

    override fun getChanges(): EventStream<ReactiveList.Change<E>> = ChangesEventStream(
        source = source,
    )
}
