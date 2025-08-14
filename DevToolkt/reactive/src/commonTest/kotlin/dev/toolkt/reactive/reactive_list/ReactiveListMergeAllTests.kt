package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore
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

        eventEmitter0.emit(0)
        eventEmitter2.emit(20)
        eventEmitter1.emit(10)

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

        eventEmitter0.emit(1)
        eventEmitter2.emit(21)
        eventEmitter1.emit(11)

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

        eventEmitter0.emit(1)
        eventEmitter2a.emit(21)
        eventEmitter2b.emit(-21)
        eventEmitter1.emit(11)

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

        eventEmitter0.emit(1)
        eventEmitter2.emit(21)
        eventEmitter1.emit(11)

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
