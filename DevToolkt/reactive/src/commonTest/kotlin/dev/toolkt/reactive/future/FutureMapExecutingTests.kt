package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.cell.sampleExternally
import dev.toolkt.reactive.cell.setExternally
import dev.toolkt.reactive.managed_io.Actions
import dev.toolkt.reactive.managed_io.endExternally
import dev.toolkt.reactive.managed_io.startExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class FutureMapExecutingTests {
    @Test
    fun testMapExecuting_completedLater_unlistened_endedLater() {
        val futureCompleter = FutureCompleter<Int>()

        val sourceMutableCell = MutableCell.createExternally(initialValue = 'A')

        val targetMutableCell = MutableCell.createExternally(initialValue = "x")

        val targetNewValuesVerifier = EventStreamVerifier.setup(
            eventStream = targetMutableCell.newValues,
        )

        val mappedFutureEffect = Actions.external {
            futureCompleter.mapExecuting {
                val string = "$it:${sourceMutableCell.sample()}"
                targetMutableCell.set(string)
                string.lowercase()
            }
        }

        sourceMutableCell.setExternally('B')

        // Start the effect, but don't start listening to the future
        val (mappedFuture, handle) = mappedFutureEffect.startExternally()

        val mappedFutureOnResultVerifier = EventStreamVerifier.setup(
            eventStream = mappedFuture.onResult,
        )

        // Set the relevant source value
        sourceMutableCell.setExternally('C')

        assertEquals(
            expected = emptyList(),
            actual = targetNewValuesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = Future.Pending,
            actual = mappedFuture.state.sampleExternally(),
        )

        Actions.external {
            // Complete the future
            futureCompleter.complete(4)

            assertEquals(
                expected = "x",
                actual = targetMutableCell.sampleExternally(),
            )
        }

        assertEquals(
            expected = "4:C",
            actual = targetMutableCell.sampleExternally(),
        )

        assertEquals(
            expected = Future.Fulfilled(
                result = "4:c",
            ),
            actual = mappedFuture.state.sampleExternally(),
        )

        assertEquals(
            expected = listOf("4:C"),
            actual = targetNewValuesVerifier.removeReceivedEvents(),
        )

        assertEquals(
            expected = listOf("4:c"),
            actual = mappedFutureOnResultVerifier.removeReceivedEvents(),
        )

        // End the effect
        handle.endExternally()
    }

    @Test
    fun testMapExecuting_completedBefore_unlistened_endedLater() {
        val futureCompleter = FutureCompleter<Int>()

        val sourceMutableCell = MutableCell.createExternally(initialValue = 'A')

        val targetMutableCell = MutableCell.createExternally(initialValue = "x")

        val targetNewValuesVerifier = EventStreamVerifier.setup(
            eventStream = targetMutableCell.newValues,
        )

        val mappedFutureEffect = Actions.external {
            futureCompleter.mapExecuting {
                val string = "$it:${sourceMutableCell.sample()}"
                targetMutableCell.set(string)
                string.lowercase()
            }
        }

        // Set the relevant source value
        sourceMutableCell.setExternally('B')

        // Complete the future
        futureCompleter.completeExternally(6)

        // Start the effect, but don't start listening to the future
        val (mappedFuture, handle) = mappedFutureEffect.startExternally()

        sourceMutableCell.setExternally('C')

        Actions.external {
            // Verify that the mapped future immediately has the expected value
            assertEquals(
                expected = Future.Fulfilled(
                    result = "6:b",
                ),
                actual = mappedFuture.state.sample(),
            )
        }

        // Verify that the target cell now has its new value
        assertEquals(
            expected = "6:B",
            actual = targetMutableCell.sampleExternally(),
        )

        // Verify that the target cell new values eventually received the event
        // (?)
        assertEquals(
            expected = listOf("6:B"),
            actual = targetNewValuesVerifier.removeReceivedEvents(),
        )

        // Verify that the future is (still) fulfilled with the expected value
        assertEquals(
            expected = Future.Fulfilled(
                result = "6:b",
            ),
            actual = mappedFuture.state.sampleExternally(),
        )

        // End the effect
        handle.endExternally()
    }

    @Test
    fun testMapExecuting_unlistened_endedBefore() {
        val futureCompleter = FutureCompleter<Int>()

        val sourceMutableCell = MutableCell.createExternally(initialValue = 'A')

        val targetMutableCell = MutableCell.createExternally(initialValue = "x")

        val targetNewValuesVerifier = EventStreamVerifier.setup(
            eventStream = targetMutableCell.newValues,
        )

        val mappedFutureEffect = Actions.external {
            futureCompleter.mapExecuting {
                val string = "$it:${sourceMutableCell.sample()}"
                targetMutableCell.set(string)
                string.lowercase()
            }
        }

        // Start the effect
        val (mappedFuture, handle) = mappedFutureEffect.startExternally()

        // End the effect early
        handle.endExternally()

        // Verify that the cell wasn't updated
        assertEquals(
            expected = "x",
            actual = targetMutableCell.sampleExternally(),
        )

        // Verify that the cell new values didn't receive any events
        assertEquals(
            expected = emptyList(),
            actual = targetNewValuesVerifier.removeReceivedEvents(),
        )

        // Verify that the future is still pending
        assertEquals(
            expected = Future.Pending,
            actual = mappedFuture.state.sampleExternally(),
        )
    }

    @Test
    fun testMapExecuting_listened_endedLater() {
        val futureCompleter = FutureCompleter<Int>()

        val sourceMutableCell = MutableCell.createExternally(initialValue = 'A')

        val targetMutableCell = MutableCell.createExternally(initialValue = "x")

        val targetNewValuesVerifier = EventStreamVerifier.setup(
            eventStream = targetMutableCell.newValues,
        )

        val mappedFutureEffect = Actions.external {
            futureCompleter.mapExecuting {
                val string = "$it:${sourceMutableCell.sample()}"
                targetMutableCell.set(string)
                string.lowercase()
            }
        }

        // Start the effect
        val (mappedFuture, handle) = mappedFutureEffect.startExternally()

        // Start listening to the future
        val onResultVerifier = EventStreamVerifier.setup(
            eventStream = mappedFuture.onResult,
        )

        // Set the relevant source value
        sourceMutableCell.setExternally('F')

        // Complete the future
        futureCompleter.completeExternally(7)

        // Verify that the target cell has the expected value
        assertEquals(
            expected = "7:F",
            actual = targetMutableCell.sampleExternally(),
        )

        // Verify that the target cell new values received the expected event
        assertEquals(
            expected = listOf("7:F"),
            actual = targetNewValuesVerifier.removeReceivedEvents(),
        )

        // Verify that the future is fulfilled with the expected value
        assertEquals(
            expected = Future.Fulfilled(
                result = "7:f",
            ),
            actual = mappedFuture.state.sampleExternally(),
        )

        // Verify that the future onResult received the expected value
        assertEquals(
            expected = listOf(
                "7:f",
            ),
            actual = onResultVerifier.removeReceivedEvents(),
        )

        // End the effect
        handle.endExternally()

        // Update the source cell again after the effect ended
        sourceMutableCell.setExternally('G')

        // Ensure that the future state didn't change
        assertEquals(
            expected = Future.Fulfilled(
                result = "7:f",
            ),
            actual = mappedFuture.state.sampleExternally(),
        )

        // Verify that the target cell wasn't updated
        assertEquals(
            expected = "7:F",
            actual = targetMutableCell.sampleExternally(),
        )

        // Ensure that the target cell didn't receive any new values
        assertEquals(
            expected = emptyList(),
            actual = onResultVerifier.removeReceivedEvents(),
        )
    }
}
