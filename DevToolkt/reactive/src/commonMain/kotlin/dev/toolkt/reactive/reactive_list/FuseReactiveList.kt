package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.collections.mutableIndexedListOf
import dev.toolkt.core.delegates.weakLazy
import dev.toolkt.core.iterable.removeRange
import dev.toolkt.core.range.single
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.managed_io.Transaction

internal class FuseReactiveList<ElementT>(
    private val source: ReactiveList<Cell<ElementT>>,
) : ActiveReactiveList<ElementT>() {
    class ChangesEventStream<E>(
        private val source: ReactiveList<Cell<E>>,
    ) : DependentEventStream<ReactiveList.Change<E>>() {
        // Thought: This subscription object is quite heavy (has the size
        // of the original list), while a separate subscription is created for
        // each observer. This could be potentially improved by some sharing
        // mechanism depending on weak caching. Switch to StatefulEventStream?
        override fun observe(): Subscription = object : Subscription {
            private val outerSubscription = source.changes.listen(
                listener = object : UnconditionalListener<Change<Cell<E>>>() {
                    override fun handleUnconditionally(
                        transaction: Transaction,
                        event: ReactiveList.Change<Cell<E>>,
                    ) {
                        val outerChange = event

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
                            transaction = transaction,
                            event = ReactiveList.Change.single(
                                update = ReactiveList.Change.Update.change(
                                    indexRange = indexRange,
                                    changedElements = updatedCells.map { it.currentValueUnmanaged },
                                ),
                            ),
                        )
                    }
                },
            )

            private val innerSubscriptions = mutableIndexedListOf<Subscription>()

            init {
                source.currentElementsUnmanaged.forEachIndexed { index, cell ->
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

                val newInnerSubscription = innerCell.newValues.listen(
                    listener = object : UnconditionalListener<E>() {
                        override fun handleUnconditionally(
                            transaction: Transaction,
                            event: E,
                        ) {
                            val newInnerValue = event

                            val currentIndex = innerSubscriptions.indexOfVia(handle = newInnerHandle)
                                ?: throw AssertionError("No index found for handle $newInnerHandle.")

                            this@ChangesEventStream.notify(
                                transaction = transaction,
                                event = ReactiveList.Change.single(
                                    update = ReactiveList.Change.Update.change(
                                        indexRange = IntRange.single(currentIndex),
                                        changedElements = listOf(newInnerValue),
                                    ),
                                ),
                            )
                        }
                    }
                )

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

    override val changes: EventStream<Change<ElementT>> by weakLazy {
        ChangesEventStream(source = source)
    }

    override val currentElementsUnmanaged: List<ElementT>
        get() = source.currentElementsUnmanaged.map {
            it.currentValueUnmanaged
        }
}
