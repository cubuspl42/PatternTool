package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.cell.sampleExternally
import dev.toolkt.reactive.effect.endExternally
import dev.toolkt.reactive.effect.jumpStartExternally
import kotlin.test.Test
import kotlin.test.assertEquals

class EventStreamForEachTests {
    @Test
    fun testForEach_triggeredOnce() {
        val eventEmitter = EventEmitter.createExternally<Int>()

        val mutableCell = MutableCell.createExternally(
            initialValue = 0,
        )

        val effect = eventEmitter.forEach {
            mutableCell.set(it)
        }

        // Emit an event before the effect is started
        eventEmitter.emitExternally(10)

        // Verify that the mutable cell has not changed
        assertEquals(
            expected = 0,
            actual = mutableCell.sampleExternally(),
        )

        val handle = effect.jumpStartExternally()

        // Emit an event after the effect is started
        eventEmitter.emitExternally(10)

        // Verify that the mutable has changed
        assertEquals(
            expected = 10,
            actual = mutableCell.sampleExternally(),
        )

        // Emit another event after the effect is started
        eventEmitter.emitExternally(20)

        // Verify that the mutable has changed again
        assertEquals(
            expected = 20,
            actual = mutableCell.sampleExternally(),
        )

        // End the effect
        handle.endExternally()

        // Emit an event after the effect ended
        eventEmitter.emitExternally(30)

        // Verify that the mutable has not changed
        assertEquals(
            expected = 20,
            actual = mutableCell.sampleExternally(),
        )
    }

    @Test
    fun testForEach_triggeredTwice() {
        val eventEmitter = EventEmitter.createExternally<Int>()

        val mutableCell = MutableCell.createExternally(
            initialValue = 0,
        )

        val effect = eventEmitter.forEach {
            mutableCell.set(it)
        }

        // Start the first effect
        val handle1 = effect.jumpStartExternally()

        // Emit an event after the effect is started
        eventEmitter.emitExternally(10)

        // End the first effect
        handle1.endExternally()

        // Start the effect again, proving that the effect object is reusable
        val handle2 = effect.jumpStartExternally()

        // Emit an event after the effect is started
        eventEmitter.emitExternally(20)

        // Verify that the mutable cell has changed
        assertEquals(
            expected = 20,
            actual = mutableCell.sampleExternally(),
        )

        // End the second effect
        handle2.endExternally()

        // Emit an event after the second effect ended
        eventEmitter.emitExternally(30)

        // Verify that the mutable has not changed
        assertEquals(
            expected = 20,
            actual = mutableCell.sampleExternally(),
        )
    }
}
