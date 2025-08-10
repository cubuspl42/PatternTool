package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamAccumTests {
    @Test
    fun testAccum() {
        val eventEmitter = EventEmitter<Char>()

        val accumCell = eventEmitter.accum(
            initialValue = "abc",
            transform = { string, char ->
                string + char
            },
        )

        val changesVerifier = EventStreamVerifier(
            eventStream = accumCell.changes,
        )

        assertEquals(
            expected = "abc",
            actual = accumCell.currentValue,
        )

        assertEquals(
            expected = emptyList(),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit('0')

        assertEquals(
            expected = "abc0",
            actual = accumCell.currentValue,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = "abc",
                    newValue = "abc0",
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )

        eventEmitter.emit('1')

        assertEquals(
            expected = "abc01",
            actual = accumCell.currentValue,
        )

        assertEquals(
            expected = listOf(
                Cell.Change(
                    oldValue = "abc0",
                    newValue = "abc01",
                ),
            ),
            actual = changesVerifier.removeReceivedEvents(),
        )
    }
}
