package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.managed_io.invokeEach
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamInvokeEachTests {
    @Test
    fun testInvokeEach() {
        val eventEmitter = EventEmitter<Unit>()

        var variable = 10

        val program = eventEmitter.invokeEach {
            val newVariable = variable + 1

            variable = newVariable

            newVariable.toString()
        }

        eventEmitter.emitUnmanaged(Unit)

        assertEquals(
            expected = 10,
            actual = variable,
        )

        val (resultStream, processHandle) = program.execute()

        val streamVerifier = EventStreamVerifier(
            eventStream = resultStream,
        )

        eventEmitter.emitUnmanaged(Unit)

        assertEquals(
            expected = 11,
            actual = variable,
        )

        assertEquals(
            expected = listOf("11"),
            actual = streamVerifier.removeReceivedEvents(),
        )

        eventEmitter.emitUnmanaged(Unit)

        assertEquals(
            expected = 12,
            actual = variable,
        )

        assertEquals(
            expected = listOf("12"),
            actual = streamVerifier.removeReceivedEvents(),
        )

        processHandle.stop()

        eventEmitter.emitUnmanaged(Unit)

        assertEquals(
            expected = 12,
            actual = variable,
        )

        assertEquals(
            expected = emptyList(),
            actual = streamVerifier.removeReceivedEvents(),
        )
    }
}
