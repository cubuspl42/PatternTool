package dev.toolkt.reactive.event_stream

import dev.toolkt.core.async_tests.AsyncTest
import dev.toolkt.core.async_tests.AsyncTestGroup
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.awaitCollection
import dev.toolkt.reactive.effect.Actions
import kotlin.test.assertNull

data object EventStreamHoldGarbageCollectionTestGroup : AsyncTestGroup() {
    override val tests = listOf(
        ListenedTest,
        UnlistenedTest,
    )

    data object ListenedTest : AsyncTest() {
        override suspend fun execute() {
            val eventEmitter = EventEmitter.createExternally<Int>()

            // Create a `hold` cell that will have a (temporary) listener
            val listenedHoldCellWeakRef = PlatformWeakReference(
                Actions.external {
                    eventEmitter.hold(0)
                }.also { listenedHoldCell ->
                    listenedHoldCell.newValues.listenExternally { }.cancel()
                },
            )

            listenedHoldCellWeakRef.awaitCollection(
                tag = "EventStreamHoldTest/listenedHoldCell",
            )

            assertNull(
                actual = listenedHoldCellWeakRef.get(),
            )
        }
    }

    data object UnlistenedTest : AsyncTest() {
        override suspend fun execute() {
            val eventEmitter = EventEmitter.createExternally<Int>()

            // Create a `hold` cell that will not have any listeners at all
            val unlistenedHoldCellWeakRef = PlatformWeakReference(
                Actions.external {
                    eventEmitter.hold(1)
                },
            )

            unlistenedHoldCellWeakRef.awaitCollection(
                tag = "EventStreamHoldTest/unlistenedHoldCell",
            )

            assertNull(
                actual = unlistenedHoldCellWeakRef.get(),
            )
        }
    }
}
