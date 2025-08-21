package dev.toolkt.reactive.system_tests

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.event_stream.listenExternally
import dev.toolkt.reactive.system_tests.utils.assertNull
import dev.toolkt.reactive.system_tests.utils.awaitCollection

data object EventStreamMapAtTest : ReactiveSystemTest {
    override suspend fun executeTest() {
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

        // Create a `mapAt` cell that will not have any listeners at all
        val unlistenedMapAtCellWeakRef = PlatformWeakReference(
            Actions.external {
                sourceMutableCell.mapAt { "$it:${sampledMutableCell.sample()}" }
            },
        )

        // Ensure that both cells are collectable

        listenedMapAtCellWeakRef.awaitCollection(
            tag = "EventStreamMapAtTest/listenedMapAtCell",
        )

        assertNull(
            actual = listenedMapAtCellWeakRef.get(),
        )

        unlistenedMapAtCellWeakRef.awaitCollection(
            tag = "EventStreamMapAtTest/unlistenedMapAtCell",
        )

        assertNull(
            actual = unlistenedMapAtCellWeakRef.get(),
        )
    }
}
