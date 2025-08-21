package dev.toolkt.reactive.system_tests

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.createExternally
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.listenExternally
import dev.toolkt.reactive.system_tests.utils.assertNull
import dev.toolkt.reactive.system_tests.utils.awaitCollection

data object EventStreamHoldTest : ReactiveSystemTest {
    override suspend fun executeTest() {
        val eventEmitter = EventEmitter.createExternally<Int>()

        // Create a `hold` cell that will have a (temporary) listener
        val listenedHoldCellWeakRef = PlatformWeakReference(
            Actions.external {
                eventEmitter.hold(0)
            }.also { listenedHoldCell ->
                listenedHoldCell.newValues.listenExternally { }.cancel()
            },
        )

        // Create a `hold` cell that will not have any listeners at all
        val unlistenedHoldCellWeakRef = PlatformWeakReference(
            Actions.external {
                eventEmitter.hold(1)
            },
        )

        // Ensure that both cells are collectable

        listenedHoldCellWeakRef.awaitCollection(
            tag = "EventStreamHoldTest/listenedHoldCell",
        )

        assertNull(
            actual = listenedHoldCellWeakRef.get(),
        )

        unlistenedHoldCellWeakRef.awaitCollection(
            tag = "EventStreamHoldTest/unlistenedHoldCell",
        )

        assertNull(
            actual = unlistenedHoldCellWeakRef.get(),
        )
    }
}
