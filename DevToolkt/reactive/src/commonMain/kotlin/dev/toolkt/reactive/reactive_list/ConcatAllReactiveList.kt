package dev.toolkt.reactive.reactive_list

import dev.toolkt.core.collections.MutablePrefixSumIndexedList
import dev.toolkt.core.collections.StableCollection
import dev.toolkt.core.collections.mutablePrefixSumIndexedListOf
import dev.toolkt.core.collections.updateVia
import dev.toolkt.core.delegates.weakLazy
import dev.toolkt.core.iterable.removeRange
import dev.toolkt.core.range.shift
import dev.toolkt.core.range.width
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.listen

class ConcatAllReactiveList<ElementT>(
    private val lists: ReactiveList<ReactiveList<ElementT>>,
) : ActiveReactiveList<ElementT>() {
    internal data class SubscriptionEntry(
        /**
         * A subscription to inner reactive list changes
         */
        val subscription: Subscription,
        /**
         * The size of the reactive list (has to be updated on each change affecting this list's size)
         */
        val listSize: Int,
    )

    class ChangesEventStream<ElementT>(
        private val lists: ReactiveList<ReactiveList<ElementT>>,
    ) : DependentEventStream<Change<ElementT>>() {
        override fun observe(): Subscription = object : Subscription {
            private val outerSubscription = lists.changes.listen { outerChange ->
                val update = outerChange.update
                val indexRange = update.indexRange
                val updatedLists = update.updatedElements
                val firstIndex = indexRange.first

                // Number of elements before the first updated index
                val prefixElementCount = innerSubscriptionEntries.calculatePrefixSum(
                    count = firstIndex,
                )

                // Total number of elements in the updated index range
                val updatedElementCount = indexRange.sumOf { index ->
                    lists.getNow(index = index).sizeNow
                }

                // The index range in the concatenated list
                val updatedRange = prefixElementCount until (prefixElementCount + updatedElementCount)

                this@ChangesEventStream.notify(
                    Change.single(
                        update = Change.Update.change(
                            indexRange = updatedRange,
                            changedElements = updatedLists.flatMap { it.currentElements },
                        ),
                    ),
                )

                indexRange.forEach { index ->
                    val innerSubscriptionEntry = innerSubscriptionEntries.getOrNull(index)
                        ?: throw IllegalStateException("No subscription found for index $index.")

                    val innerSubscription = innerSubscriptionEntry.subscription

                    innerSubscription.cancel()
                }

                innerSubscriptionEntries.removeRange(
                    indexRange = indexRange,
                )

                updatedLists.reversed().forEach { updatedCell ->
                    subscribeToInner(
                        index = firstIndex,
                        innerList = updatedCell,
                    )
                }
            }

            private val innerSubscriptionEntries: MutablePrefixSumIndexedList<SubscriptionEntry> =
                mutablePrefixSumIndexedListOf()

            init {
                lists.currentElements.forEachIndexed { index, reactiveList ->
                    subscribeToInner(
                        index = index,
                        innerList = reactiveList,
                    )
                }
            }

            private fun subscribeToInner(
                index: Int,
                innerList: ReactiveList<ElementT>,
            ) {
                val innerHandle = innerSubscriptionEntries.addAtEx(
                    index = index,
                    element = SubscriptionEntry(
                        subscription = Subscription.Noop, // A temporary value
                        listSize = innerList.sizeNow,
                    ),
                )

                val newInnerSubscription = innerList.changes.listen { innerChange ->
                    val update = innerChange.update

                    val currentIndex = innerSubscriptionEntries.indexOfVia(handle = innerHandle)
                        ?: throw AssertionError("No index found for handle $innerHandle")

                    // Current number of elements in all lists before this list
                    val prefixElementCount = innerSubscriptionEntries.calculatePrefixSum(
                        count = currentIndex,
                    )

                    val shiftedIndexRange = update.indexRange.shift(
                        delta = prefixElementCount,
                    )

                    val listSizeDelta = update.updatedElements.size - update.indexRange.width

                    this@ChangesEventStream.notify(
                        Change.single(
                            update = Change.Update.change(
                                indexRange = shiftedIndexRange,
                                changedElements = update.updatedElements,
                            ),
                        ),
                    )

                    innerSubscriptionEntries.updateListSizeVia(
                        handle = innerHandle,
                    ) { oldListSize ->
                        oldListSize + listSizeDelta
                    }
                }

                innerSubscriptionEntries.setSubscriptionVia(
                    handle = innerHandle,
                    newSubscription = newInnerSubscription,
                )
            }

            override fun cancel() {
                outerSubscription.cancel()
                innerSubscriptionEntries.forEach { it.subscription.cancel() }
                innerSubscriptionEntries.clear()
            }
        }
    }

    override val changes: EventStream<Change<ElementT>> by weakLazy {
        ChangesEventStream(lists = lists)
    }

    override val currentElements: List<ElementT>
        get() = lists.currentElements.flatMap { it.currentElements }
}

private fun MutablePrefixSumIndexedList<ConcatAllReactiveList.SubscriptionEntry>.setSubscriptionVia(
    handle: StableCollection.Handle<ConcatAllReactiveList.SubscriptionEntry>,
    newSubscription: Subscription,
): Subscription? {
    val oldEntry = updateVia(
        handle = handle,
    ) { oldEntry ->
        oldEntry.copy(
            subscription = newSubscription,
        )
    }

    return oldEntry?.subscription
}

private fun MutablePrefixSumIndexedList<ConcatAllReactiveList.SubscriptionEntry>.updateListSizeVia(
    handle: StableCollection.Handle<ConcatAllReactiveList.SubscriptionEntry>,
    update: (Int) -> Int,
): Int? {
    val oldEntry = updateVia(
        handle = handle,
    ) { oldEntry ->
        oldEntry.copy(
            listSize = update(oldEntry.listSize),
        )
    }

    return oldEntry?.listSize
}
