package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.cell.setExternally
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamDivertTests {
    @Test
    fun testDivert() {
        val eventEmitter1 = EventEmitter.createExternally<Int>()
        val eventEmitter2 = EventEmitter.createExternally<Int>()

        val mutableStreamCell = MutableCell.createExternally<EventStream<Int>>(
            initialValue = eventEmitter1,
        )

        val divertedStream = EventStream.divert(
            nestedEventStream = mutableStreamCell,
        )

        val streamVerifier = EventStreamVerifier.setup(
            eventStream = divertedStream,
        )

        eventEmitter1.emitExternally(2)

        eventEmitter2.emitExternally(-1)

        eventEmitter1.emitExternally(5)

        eventEmitter2.emitExternally(-7)

        assertEquals(
            expected = listOf(2, 5),
            actual = streamVerifier.removeReceivedEvents(),
        )

        mutableStreamCell.setExternally(eventEmitter2)

        eventEmitter1.emitExternally(3)

        eventEmitter2.emitExternally(-2)

        eventEmitter1.emitExternally(8)

        eventEmitter2.emitExternally(-9)

        assertEquals(
            expected = listOf(-2, -9),
            actual = streamVerifier.removeReceivedEvents(),
        )

        mutableStreamCell.setExternally(eventEmitter1)

        eventEmitter1.emitExternally(4)

        eventEmitter2.emitExternally(-12)

        eventEmitter1.emitExternally(11)

        eventEmitter2.emitExternally(-77)

        assertEquals(
            expected = listOf(4, 11),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}
