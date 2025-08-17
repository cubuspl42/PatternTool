package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListMergeAllTests {
    @Test
    fun testMergeAll_initial() {
        val eventEmitter0 = EventEmitter<Int>()
        val eventEmitter1 = EventEmitter<Int>()
        val eventEmitter2 = EventEmitter<Int>()

        val mutableEventStreams = MutableReactiveList(
            initialContent = listOf(
                eventEmitter0,
                eventEmitter1,
                eventEmitter2,
            ),
        )

        val mergedStream = ReactiveList.mergeAll(
            eventStreams = mutableEventStreams,
        )

        val streamVerifier = EventStreamVerifier(
            eventStream = mergedStream,
        )

        eventEmitter0.emitUnmanaged(0)
        eventEmitter2.emitUnmanaged(20)
        eventEmitter1.emitUnmanaged(10)

        assertEquals(
            expected = listOf(
                0,
                20,
                10,
            ),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testMergeAll_removed() {
        val eventEmitter0 = EventEmitter<Int>()
        val eventEmitter1 = EventEmitter<Int>()
        val eventEmitter2 = EventEmitter<Int>()

        val mutableEventStreams = MutableReactiveList(
            initialContent = listOf(
                eventEmitter0,
                eventEmitter1,
                eventEmitter2,
            ),
        )

        val mergedStream = ReactiveList.mergeAll(
            eventStreams = mutableEventStreams,
        )

        val streamVerifier = EventStreamVerifier(
            eventStream = mergedStream,
        )

        mutableEventStreams.removeAt(1)

        eventEmitter0.emitUnmanaged(1)
        eventEmitter2.emitUnmanaged(21)
        eventEmitter1.emitUnmanaged(11)

        assertEquals(
            expected = listOf(
                1,
                21,
            ),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testMergeAll_updated() {
        val eventEmitter0 = EventEmitter<Int>()
        val eventEmitter1 = EventEmitter<Int>()
        val eventEmitter2a = EventEmitter<Int>()
        val eventEmitter2b = EventEmitter<Int>()

        val mutableEventStreams = MutableReactiveList(
            initialContent = listOf(
                eventEmitter0,
                eventEmitter1,
                eventEmitter2a,
            ),
        )

        val mergedStream = ReactiveList.mergeAll(
            eventStreams = mutableEventStreams,
        )

        val streamVerifier = EventStreamVerifier(
            eventStream = mergedStream,
        )

        mutableEventStreams.set(2, eventEmitter2b)

        eventEmitter0.emitUnmanaged(1)
        eventEmitter2a.emitUnmanaged(21)
        eventEmitter2b.emitUnmanaged(-21)
        eventEmitter1.emitUnmanaged(11)

        assertEquals(
            expected = listOf(
                1,
                -21,
                11,
            ),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }

    @Test
    fun testMergeAll_added() {
        val eventEmitter0 = EventEmitter<Int>()
        val eventEmitter1 = EventEmitter<Int>()
        val eventEmitter2 = EventEmitter<Int>()

        val mutableEventStreams = MutableReactiveList(
            initialContent = listOf(
                eventEmitter0,
                eventEmitter2,
            ),
        )

        val mergedStream = ReactiveList.mergeAll(
            eventStreams = mutableEventStreams,
        )

        val streamVerifier = EventStreamVerifier(
            eventStream = mergedStream,
        )

        mutableEventStreams.add(1, eventEmitter1)

        eventEmitter0.emitUnmanaged(1)
        eventEmitter2.emitUnmanaged(21)
        eventEmitter1.emitUnmanaged(11)

        assertEquals(
            expected = listOf(
                1,
                21,
                11,
            ),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}
