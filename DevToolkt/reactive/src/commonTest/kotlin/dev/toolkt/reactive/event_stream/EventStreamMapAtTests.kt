package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.cell.setExternally
import dev.toolkt.reactive.managed_io.Actions
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamMapAtTests {
    @Test
    fun testMapAt() {
        val eventEmitter = EventEmitter.createExternally<Int>()

        val mutableCell = MutableCell.createExternally(initialValue = 'A')

        val mappedStream = Actions.external {
            eventEmitter.mapAt { "$it:${mutableCell.sample()}" }
        }

        val streamVerifier = EventStreamVerifier(
            eventStream = mappedStream,
        )

        eventEmitter.emitExternally(1)

        assertEquals(
            expected = listOf("1:A"),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitExternally(2)

        assertEquals(
            expected = listOf("2:A"),
            actual = streamVerifier.removeReceivedEvents(),
        )

        mutableCell.setExternally('B')

        eventEmitter.emitExternally(3)

        assertEquals(
            expected = listOf("3:B"),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}
