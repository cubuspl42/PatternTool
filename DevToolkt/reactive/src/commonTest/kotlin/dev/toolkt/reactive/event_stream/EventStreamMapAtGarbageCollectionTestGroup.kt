package dev.toolkt.reactive.event_stream

import dev.toolkt.core.async_tests.AsyncTest
import dev.toolkt.core.async_tests.AsyncTestGroup
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.awaitCollection
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.effect.Actions
import kotlin.test.assertNull

data object EventStreamMapAtGarbageCollectionTestGroup : AsyncTestGroup() {
    override val tests = listOf(
        ListenedTest,
        UnlistenedTest,
    )

    data object ListenedTest : AsyncTest() {
        override suspend fun execute() {
            val sourceMutableCell = MutableCell.createExternally(initialValue = 0)

            val sampledMutableCell = MutableCell.createExternally(initialValue = 'A')

            // Create a `mapAt` cell that will have a (temporary) listener
            val listenedMapAtCellWeakRef = PlatformWeakReference(
                Actions.external {
                    sourceMutableCell.mapAt { "$it:${sampledMutableCell.sample()}" }
                }.also { listenedMapAtCell ->
                    listenedMapAtCell.newValues.listenExternally { }.cancel()
                },
            )

            listenedMapAtCellWeakRef.awaitCollection(
                tag = "EventStreamMapAtTest/listenedMapAtCell",
            )

            assertNull(
                actual = listenedMapAtCellWeakRef.get(),
            )
        }
    }

    data object UnlistenedTest : AsyncTest() {
        override suspend fun execute() {
            val sourceMutableCell = MutableCell.createExternally(initialValue = 0)

            val sampledMutableCell = MutableCell.createExternally(initialValue = 'B')

            // Create a `mapAt` cell that will not have any listeners at all
            val unlistenedMapAtCellWeakRef = PlatformWeakReference(
                Actions.external {
                    sourceMutableCell.mapAt { "$it:${sampledMutableCell.sample()}" }
                },
            )

            unlistenedMapAtCellWeakRef.awaitCollection(
                tag = "EventStreamMapAtTest/unlistenedMapAtCell",
            )

            assertNull(
                actual = unlistenedMapAtCellWeakRef.get(),
            )
        }
    }
}
