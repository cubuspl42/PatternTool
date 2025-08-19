package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.sampleExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class FutureJoinTests {
    /**
     * Both the outer and the inner future are fulfilled at the time of join.
     * The joined future never observes the actual fulfillment.
     *
     * Tested order: Outer -> Inner -> Join
     * Equivalent order (assumed): Inner -> Outer -> Join
     */
    @Test
    fun testJoin_outer_preFulfilled_inner_preFulfilled() {
        val outerFutureCompleter = FutureCompleter<Future<Int>>()
        val innerFutureCompleter = FutureCompleter<Int>()

        innerFutureCompleter.completeExternally(10)
        outerFutureCompleter.completeExternally(innerFutureCompleter)

        val joinedFuture = Future.join(outerFutureCompleter)

        val onResultVerifier = EventStreamVerifier.setup(
            eventStream = joinedFuture.onResult,
        )

        assertEquals(
            expected = Future.Fulfilled(
                result = 10,
            ),
            actual = joinedFuture.state.sampleExternally(),
        )

        assertEquals(
            expected = emptyList(),
            actual = onResultVerifier.removeReceivedEvents(),
        )
    }

    /**
     * The outer future is fulfilled at the time of join, but the inner future is completed later.
     *
     * The order: Outer -> Join -> Inner
     */
    @Test
    fun testJoin_outer_preFulfilled_inner_postFulfilled() {
        val outerFutureCompleter = FutureCompleter<Future<Int>>()
        val innerFutureCompleter = FutureCompleter<Int>()

        outerFutureCompleter.completeExternally(innerFutureCompleter)

        val joinedFuture = Future.join(outerFutureCompleter)

        val onResultVerifier = EventStreamVerifier.setup(
            eventStream = joinedFuture.onResult,
        )

        assertEquals(
            expected = Future.Pending,
            actual = joinedFuture.state.sampleExternally(),
        )

        assertEquals(
            expected = emptyList(),
            actual = onResultVerifier.removeReceivedEvents(),
        )

        innerFutureCompleter.completeExternally(10)

        assertEquals(
            expected = Future.Fulfilled(
                result = 10,
            ),
            actual = joinedFuture.state.sampleExternally(),
        )

        assertEquals(
            expected = listOf(10),
            actual = onResultVerifier.removeReceivedEvents(),
        )
    }

    /**
     * The outer future is pending at the time of join, but it's completed later (and by then, the inner future is
     * already completed).
     *
     * Tested order: Inner -> Join -> Outer
     * Equivalent order (assumed): Join -> Inner -> Outer
     */
    @Test
    @Ignore // TODO: Simplify the Future class, make onResult a util, or something
    fun testJoin_outer_postFulfilled_inner_preFulfilled() {
        val outerFutureCompleter = FutureCompleter<Future<Int>>()
        val innerFutureCompleter = FutureCompleter<Int>()

        innerFutureCompleter.completeExternally(10)

        val joinedFuture = Future.join(outerFutureCompleter)

        val onResultVerifier = EventStreamVerifier.setup(
            eventStream = joinedFuture.onResult,
        )

        assertEquals(
            expected = Future.Pending,
            actual = joinedFuture.state.sampleExternally(),
        )

        assertEquals(
            expected = emptyList(),
            actual = onResultVerifier.removeReceivedEvents(),
        )

        outerFutureCompleter.completeExternally(innerFutureCompleter)

        assertEquals(
            expected = Future.Fulfilled(
                result = 10,
            ),
            actual = joinedFuture.state.sampleExternally(),
        )

        assertEquals(
            expected = listOf(10),
            actual = onResultVerifier.removeReceivedEvents(),
        )
    }

    /**
     * The outer future is pending at the time of join, but it's completed later (but the inner future is completed
     * even later).
     *
     * The order: Join -> Outer -> Inner
     */
    @Test
    fun testJoin_last() {
        val outerFutureCompleter = FutureCompleter<Future<Int>>()
        val innerFutureCompleter = FutureCompleter<Int>()

        val joinedFuture = Future.join(outerFutureCompleter)

        val onResultVerifier = EventStreamVerifier.setup(
            eventStream = joinedFuture.onResult,
        )

        assertEquals(
            expected = Future.Pending,
            actual = joinedFuture.state.sampleExternally(),
        )

        assertEquals(
            expected = emptyList(),
            actual = onResultVerifier.removeReceivedEvents(),
        )

        outerFutureCompleter.completeExternally(innerFutureCompleter)

        assertEquals(
            expected = Future.Pending,
            actual = joinedFuture.state.sampleExternally(),
        )

        assertEquals(
            expected = emptyList(),
            actual = onResultVerifier.removeReceivedEvents(),
        )

        innerFutureCompleter.completeExternally(10)

        assertEquals(
            expected = Future.Fulfilled(
                result = 10,
            ),
            actual = joinedFuture.state.sampleExternally(),
        )

        assertEquals(
            expected = listOf(10),
            actual = onResultVerifier.removeReceivedEvents(),
        )
    }
}
