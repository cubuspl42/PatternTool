package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.createExternally
import dev.toolkt.reactive.event_stream.emitExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListMergeAllTests {
    @Test
    fun testMergeAll_initial() {
        val eventEmitter0 = EventEmitter.createExternally<Int>()
        val eventEmitter1 = EventEmitter.createExternally<Int>()
        val eventEmitter2 = EventEmitter.createExternally<Int>()

        val mutableEventStreams = MutableReactiveList.createExternally(
            initialContent = listOf(
                eventEmitter0,
                eventEmitter1,
                eventEmitter2,
            ),
        )

        val mergedStream = ReactiveList.mergeAll(
            eventStreams = mutableEventStreams,
        )

        val streamVerifier = EventStreamVerifier.setup(
            eventStream = mergedStream,
        )

        eventEmitter0.emitExternally(0)
        eventEmitter2.emitExternally(20)
        eventEmitter1.emitExternally(10)

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
        val eventEmitter0 = EventEmitter.createExternally<Int>()
        val eventEmitter1 = EventEmitter.createExternally<Int>()
        val eventEmitter2 = EventEmitter.createExternally<Int>()

        val mutableEventStreams = MutableReactiveList.createExternally(
            initialContent = listOf(
                eventEmitter0,
                eventEmitter1,
                eventEmitter2,
            ),
        )

        val mergedStream = ReactiveList.mergeAll(
            eventStreams = mutableEventStreams,
        )

        val streamVerifier = EventStreamVerifier.setup(
            eventStream = mergedStream,
        )

        mutableEventStreams.removeAtExternally(1)

        eventEmitter0.emitExternally(1)
        eventEmitter2.emitExternally(21)
        eventEmitter1.emitExternally(11)

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
        val eventEmitter0 = EventEmitter.createExternally<Int>()
        val eventEmitter1 = EventEmitter.createExternally<Int>()
        val eventEmitter2a = EventEmitter.createExternally<Int>()
        val eventEmitter2b = EventEmitter.createExternally<Int>()

        val mutableEventStreams = MutableReactiveList.createExternally(
            initialContent = listOf(
                eventEmitter0,
                eventEmitter1,
                eventEmitter2a,
            ),
        )

        val mergedStream = ReactiveList.mergeAll(
            eventStreams = mutableEventStreams,
        )

        val streamVerifier = EventStreamVerifier.setup(
            eventStream = mergedStream,
        )

        mutableEventStreams.setExternally(2, eventEmitter2b)

        eventEmitter0.emitExternally(1)
        eventEmitter2a.emitExternally(21)
        eventEmitter2b.emitExternally(-21)
        eventEmitter1.emitExternally(11)

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
        val eventEmitter0 = EventEmitter.createExternally<Int>()
        val eventEmitter1 = EventEmitter.createExternally<Int>()
        val eventEmitter2 = EventEmitter.createExternally<Int>()

        val mutableEventStreams = MutableReactiveList.createExternally(
            initialContent = listOf(
                eventEmitter0,
                eventEmitter2,
            ),
        )

        val mergedStream = ReactiveList.mergeAll(
            eventStreams = mutableEventStreams,
        )

        val streamVerifier = EventStreamVerifier.setup(
            eventStream = mergedStream,
        )

        mutableEventStreams.addExternally(1, eventEmitter1)

        eventEmitter0.emitExternally(1)
        eventEmitter2.emitExternally(21)
        eventEmitter1.emitExternally(11)

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
