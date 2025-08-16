package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.completeExternally
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.cell.sampleExternally
import dev.toolkt.reactive.cell.setExternally
import dev.toolkt.reactive.managed_io.Reactions
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

// Thought: Should we add GC stress to the tests?
class FutureMapAtTests {
    @Test
    fun testMapAt_completedLater() {
        val futureCompleter = FutureCompleter<Int>()

        val mutableCell = MutableCell.createExternally(initialValue = 'A')

        val mappedFuture = Reactions.external {
            futureCompleter.mapAt { "$it:${mutableCell.sample()}" }
        }

        val onResultVerifier = EventStreamVerifier(
            eventStream = mappedFuture.onResult,
        )

        assertEquals(
            expected = Future.Pending,
            actual = mappedFuture.state.sampleExternally(),
        )

        mutableCell.setExternally('B')

        futureCompleter.completeExternally(1)

        assertFalse(
            actual = futureCompleter.hasListeners,
        )

        assertFalse(
            actual = mutableCell.hasListeners,
        )

        assertEquals(
            expected = Future.Fulfilled(
                result = "1:B",
            ),
            actual = mappedFuture.state.sampleExternally(),
        )

        assertEquals(
            expected = listOf("1:B"),
            actual = onResultVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testMapAt_completedBefore() {
        val futureCompleter = FutureCompleter<Int>()

        futureCompleter.completeExternally(2)

        val mutableCell = MutableCell.createExternally(initialValue = 'A')

        mutableCell.setExternally('B')

        val mappedFuture = Reactions.external {
            futureCompleter.mapAt { "$it:${mutableCell.sample()}" }
        }

        val onResultVerifier = EventStreamVerifier(
            eventStream = mappedFuture.onResult,
        )

        mutableCell.setExternally('C')

        assertFalse(
            actual = futureCompleter.hasListeners,
        )

        assertFalse(
            actual = mutableCell.hasListeners,
        )

        assertEquals(
            expected = Future.Fulfilled(
                result = "2:B",
            ),
            actual = mappedFuture.state.sampleExternally(),
        )

        assertEquals(
            expected = emptyList(),
            actual = onResultVerifier.removeReceivedEvents(),
        )
    }
}
