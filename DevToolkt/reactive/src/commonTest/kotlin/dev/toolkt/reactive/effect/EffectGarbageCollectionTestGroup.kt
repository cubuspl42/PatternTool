package dev.toolkt.reactive.effect

import dev.toolkt.core.async_tests.AsyncTest
import dev.toolkt.core.async_tests.AsyncTestGroup
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.awaitCollection
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.cell.sampleExternally
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.createExternally
import dev.toolkt.reactive.event_stream.emitExternally
import dev.toolkt.reactive.event_stream.forEach
import kotlinx.coroutines.delay
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.microseconds

data object EffectGarbageCollectionTestGroup : AsyncTestGroup() {
    override val tests = listOf(
        StartBoundTest,
    )

    data object StartBoundTest : AsyncTest() {
        override suspend fun execute() {
            val eventEmitter = EventEmitter.createExternally<Int>()

            val memoryMutableCell = MutableCell.createExternally(initialValue = 0)

            val sourceEffect = eventEmitter.forEach {
                memoryMutableCell.set(it)
            }

            val targetWeakRef = PlatformWeakReference(
                Actions.external {
                    List(100_000) { 1 }.also { target ->
                        // Start the effect bound to the target object
                        sourceEffect.startBound(target = target)
                    }
                }
            )

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

            targetWeakRef.awaitCollection(
                tag = "EffectGarbageCollectionTestGroup/StartBoundTest/mutableTarget",
            )

            // Ensure that all finalization callbacks had a chance to execute
            delay(1.microseconds)

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
}
