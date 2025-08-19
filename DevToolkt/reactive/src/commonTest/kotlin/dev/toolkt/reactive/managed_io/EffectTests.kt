package dev.toolkt.reactive.managed_io

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.cell.sampleExternally
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.createExternally
import dev.toolkt.reactive.event_stream.emitExternally
import dev.toolkt.reactive.event_stream.forEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EffectTests {
    @Test
    @Suppress("AssignedValueIsNeverRead")
    fun testStartBound() = runTestDefault {
        val eventEmitter = EventEmitter.createExternally<Int>()

        val memoryMutableCell = MutableCell.createExternally(initialValue = 0)

        val sourceEffect = eventEmitter.forEach {
            memoryMutableCell.set(it)
        }

        @Suppress("VariableNeverRead") var mutableTarget: Any? = Actions.external {
            List(100_000) { 1 }.also {
                // Start the effect bound to the target object
                sourceEffect.startBound(target = it)
            }
        }

        // Verify that the effect subscribed to the source stream
        assertTrue(
            actual = eventEmitter.hasListeners,
        )

        // Emit a source event
        eventEmitter.emitExternally(1)

        // Verify that the effect is active
        assertEquals(
            expected = 1,
            actual = memoryMutableCell.sampleExternally(),
        )

        // Emit another source event
        eventEmitter.emitExternally(2)

        // Verify that the effect is (still) active
        assertEquals(
            expected = 2,
            actual = memoryMutableCell.sampleExternally(),
        )

        // Make the target object collectable
        mutableTarget = null

        // Force the garbage collection, which should collect the target and, in consequence,
        // stop the effect
        PlatformSystem.collectGarbageForced()

        // Emit a source event after the effect should've stopped
        eventEmitter.emitExternally(3)

        // Verify that the effect is no longer active
        assertEquals(
            expected = 2,
            actual = memoryMutableCell.sampleExternally(),
        )

        // Verify that the effect unsubscribed from the source stream
        assertFalse(
            actual = eventEmitter.hasListeners,
        )
    }
}
