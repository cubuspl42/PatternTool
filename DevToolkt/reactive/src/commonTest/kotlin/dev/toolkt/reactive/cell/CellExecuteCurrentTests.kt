package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.setExternally
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.managed_io.executeCurrent
import dev.toolkt.reactive.managed_io.forEachInvoke
import kotlin.test.Test
import kotlin.test.assertEquals

class CellExecuteCurrentTests {
    @Test
    fun testExecuteCurrent() {
        val eventSEmitter = EventEmitter<Unit>()

        var variable0 = 10
        var variable1 = -10

        val program0 = eventSEmitter.forEachInvoke {
            ++variable0
        }

        val program1 = eventSEmitter.forEachInvoke {
            --variable1
        }

        val mutableCell = MutableCell(
            initialValue = program0,
        )

        val resultCell = mutableCell.executeCurrent()

        eventSEmitter.emitUnmanaged(Unit)

        assertEquals(
            expected = 10,
            actual = variable0,
        )

        assertEquals(
            expected = -10,
            actual = variable1,
        )

        val (_, processHandle) = resultCell.execute()

        eventSEmitter.emitUnmanaged(Unit)

        assertEquals(
            expected = 11,
            actual = variable0,
        )

        assertEquals(
            expected = -10,
            actual = variable1,
        )

        mutableCell.setExternally(program1)

        eventSEmitter.emitUnmanaged(Unit)

        assertEquals(
            expected = 11,
            actual = variable0,
        )

        assertEquals(
            expected = -11,
            actual = variable1,
        )

        processHandle.stop()

        eventSEmitter.emitUnmanaged(Unit)

        assertEquals(
            expected = 11,
            actual = variable0,
        )

        assertEquals(
            expected = -11,
            actual = variable1,
        )
    }
}
