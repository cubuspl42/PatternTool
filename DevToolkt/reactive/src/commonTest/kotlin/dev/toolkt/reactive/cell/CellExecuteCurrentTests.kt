package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.emitExternally
import dev.toolkt.reactive.managed_io.executeCurrent
import dev.toolkt.reactive.managed_io.forEachInvoke
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore // TODO: Nuke in favor of effects
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

        eventSEmitter.emitExternally(Unit)

        assertEquals(
            expected = 10,
            actual = variable0,
        )

        assertEquals(
            expected = -10,
            actual = variable1,
        )

        val (_, processHandle) = resultCell.execute()

        eventSEmitter.emitExternally(Unit)

        assertEquals(
            expected = 11,
            actual = variable0,
        )

        assertEquals(
            expected = -10,
            actual = variable1,
        )

        mutableCell.setExternally(program1)

        eventSEmitter.emitExternally(Unit)

        assertEquals(
            expected = 11,
            actual = variable0,
        )

        assertEquals(
            expected = -11,
            actual = variable1,
        )

        processHandle.stop()

        eventSEmitter.emitExternally(Unit)

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
