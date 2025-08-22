package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.createExternally
import dev.toolkt.reactive.cell.sampleExternally
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.endExternally
import dev.toolkt.reactive.effect.startExternally
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.createExternally
import dev.toolkt.reactive.event_stream.emitExternally
import dev.toolkt.reactive.event_stream.forEach
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore // TODO: Implement `ReactiveList.actuate`
class ReactiveListActuateTests {
    private data class TestEffect(
        val eventEmitter: EventEmitter<Int>,
        val memoryCell: MutableCell<Int>,
        val effect: Effect<String>,
    ) {
        companion object {
            fun create(
                name: String,
            ): TestEffect {
                val eventEmitter = EventEmitter.createExternally<Int>()

                val memoryCell = MutableCell.createExternally(0)

                return TestEffect(
                    eventEmitter = eventEmitter,
                    memoryCell = memoryCell,
                    effect = Effect.pureTriggering(
                        result = name,
                        trigger = eventEmitter.forEach {
                            memoryCell.set(it)
                        },
                    )
                )
            }
        }

        fun stimulate(
            id: Int,
        ) {
            eventEmitter.emitExternally(id)
        }

        fun sampleMemory(): Int = memoryCell.sampleExternally()
    }

    @Test
    fun testActuate_initial() {
        val testEffect0 = TestEffect.create(
            name = "E0",
        )

        val testEffect1 = TestEffect.create(
            name = "E1",
        )

        val testEffect2 = TestEffect.create(
            name = "E2",
        )

        val mutableReactiveList = MutableReactiveList.createExternally(
            initialContent = listOf(
                testEffect0.effect,
                testEffect1.effect,
                testEffect2.effect,
            ),
        )

        // Create the actuation effect
        val actuatedReactiveListEffect = ReactiveList.actuate(
            mutableReactiveList,
        )

        // Stimulate the system before the effect is started

        testEffect0.stimulate(1)
        testEffect1.stimulate(21)
        testEffect2.stimulate(31)

        // Until the effect is started, the stimulation shouldn't be effective

        assertEquals(
            expected = 0,
            actual = testEffect0.sampleMemory(),
        )

        assertEquals(
            expected = 0,
            actual = testEffect1.sampleMemory(),
        )

        assertEquals(
            expected = 0,
            actual = testEffect2.sampleMemory(),
        )

        val (actuatedReactiveList, handle) = actuatedReactiveListEffect.startExternally()

        // Stimulate the system after the effect started

        testEffect0.stimulate(2)
        testEffect1.stimulate(12)

        // Now the stimulation should be effective

        assertEquals(
            expected = listOf(
                "E0",
                "E1",
                "E2",
            ),
            actuatedReactiveList.sampleContentExternally(),
        )

        assertEquals(
            expected = 2,
            actual = testEffect0.sampleMemory(),
        )

        assertEquals(
            expected = 12,
            actual = testEffect1.sampleMemory(),
        )

        assertEquals(
            expected = 0,
            actual = testEffect2.sampleMemory(),
        )

        // Stimulate the system again, while the effect is active

        testEffect1.stimulate(13)
        testEffect2.stimulate(23)

        // The stimulation should still be effective

        assertEquals(
            expected = 2,
            actual = testEffect0.sampleMemory(),
        )

        assertEquals(
            expected = 13,
            actual = testEffect1.sampleMemory(),
        )

        assertEquals(
            expected = 23,
            actual = testEffect2.sampleMemory(),
        )

        // End the effect
        handle.endExternally()

        // Stimulate the system after the effect ended

        testEffect0.stimulate(4)
        testEffect1.stimulate(14)
        testEffect2.stimulate(24)

        // Now the stimulation shouldn't be effective anymore

        assertEquals(
            expected = 2,
            actual = testEffect0.sampleMemory(),
        )

        assertEquals(
            expected = 13,
            actual = testEffect1.sampleMemory(),
        )

        assertEquals(
            expected = 23,
            actual = testEffect2.sampleMemory(),
        )
    }

    @Test
    fun testActuate_added() {
        val testEffect0 = TestEffect.create(
            name = "E0",
        )

        val testEffect1 = TestEffect.create(
            name = "E1",
        )

        val testEffect2 = TestEffect.create(
            name = "E2",
        )

        val mutableReactiveList = MutableReactiveList.createExternally(
            initialContent = listOf(
                testEffect0.effect,
                testEffect1.effect,
            ),
        )

        // Create the actuation effect
        val actuatedReactiveListEffect = ReactiveList.actuate(
            mutableReactiveList,
        )

        // Start the actuation effect
        val (actuatedReactiveList, handle) = actuatedReactiveListEffect.startExternally()

        // Add another inner effect to the list
        mutableReactiveList.addExternally(
            index = 2,
            element = testEffect2.effect,
        )

        assertEquals(
            expected = listOf(
                "E0",
                "E1",
                "E2",
            ),
            actuatedReactiveList.sampleContentExternally(),
        )

        // Stimulate the pre-existing effect
        testEffect1.stimulate(11)

        // Stimulate the newly added effect
        testEffect2.stimulate(21)

        assertEquals(
            expected = 0,
            actual = testEffect0.sampleMemory(),
        )

        assertEquals(
            expected = 11,
            actual = testEffect1.sampleMemory(),
        )

        assertEquals(
            expected = 21,
            actual = testEffect2.sampleMemory(),
        )

        // End the effect
        handle.endExternally()

        // Stimulate the system after the effect ended (now it shouldn't be effective)

        testEffect0.stimulate(4)
        testEffect1.stimulate(14)
        testEffect2.stimulate(24)

        assertEquals(
            expected = 0,
            actual = testEffect0.sampleMemory(),
        )

        assertEquals(
            expected = 13,
            actual = testEffect1.sampleMemory(),
        )

        assertEquals(
            expected = 21,
            actual = testEffect2.sampleMemory(),
        )
    }

    @Test
    fun testActuate_removed() {
        val testEffect0 = TestEffect.create(
            name = "E0",
        )

        val testEffect1 = TestEffect.create(
            name = "E1",
        )

        val testEffect2 = TestEffect.create(
            name = "E2",
        )


        val mutableReactiveList = MutableReactiveList.createExternally(
            initialContent = listOf(
                testEffect0.effect,
                testEffect1.effect,
                testEffect2.effect,
            ),
        )

        // Create the actuation effect
        val actuatedReactiveListEffect = ReactiveList.actuate(
            mutableReactiveList,
        )

        // Start the actuation effect
        val (actuatedReactiveList, handle) = actuatedReactiveListEffect.startExternally()

        // Remove an inner effect to the list
        mutableReactiveList.removeAtExternally(
            index = 0,
        )

        assertEquals(
            expected = listOf(
                "E1",
                "E2",
            ),
            actuatedReactiveList.sampleContentExternally(),
        )

        // Stimulate the just removed effect
        testEffect0.stimulate(1)

        // Stimulate the still present effect
        testEffect1.stimulate(11)
        testEffect2.stimulate(21)

        assertEquals(
            expected = 0,
            actual = testEffect0.sampleMemory(),
        )

        assertEquals(
            expected = 11,
            actual = testEffect1.sampleMemory(),
        )

        assertEquals(
            expected = 21,
            actual = testEffect2.sampleMemory(),
        )

        // End the effect
        handle.endExternally()

        // Stimulate the system after the effect ended (now it shouldn't be effective)

        testEffect0.stimulate(2)
        testEffect1.stimulate(12)
        testEffect2.stimulate(22)

        assertEquals(
            expected = 0,
            actual = testEffect0.sampleMemory(),
        )

        assertEquals(
            expected = 11,
            actual = testEffect1.sampleMemory(),
        )

        assertEquals(
            expected = 21,
            actual = testEffect2.sampleMemory(),
        )
    }

    @Test
    fun testActuate_updated() {
        val testEffect0 = TestEffect.create(
            name = "E0",
        )

        val testEffect1A = TestEffect.create(
            name = "E1a",
        )

        val testEffect1B = TestEffect.create(
            name = "E1b",
        )

        val testEffect2 = TestEffect.create(
            name = "E2",
        )

        val mutableReactiveList = MutableReactiveList.createExternally(
            initialContent = listOf(
                testEffect0.effect,
                testEffect1A.effect,
                testEffect2.effect,
            ),
        )

        // Create the actuation effect
        val actuatedReactiveListEffect = ReactiveList.actuate(
            mutableReactiveList,
        )

        // Start the actuation effect
        val (actuatedReactiveList, handle) = actuatedReactiveListEffect.startExternally()

        // Update an inner effect in the list
        mutableReactiveList.setExternally(
            index = 1,
            newValue = testEffect1B.effect,
        )

        assertEquals(
            expected = listOf(
                "E0",
                "E1b",
                "E2",
            ),
            actuatedReactiveList.sampleContentExternally(),
        )

        // Stimulate all the effects

        testEffect0.stimulate(1)
        testEffect1A.stimulate(11)
        testEffect1B.stimulate(12)
        testEffect2.stimulate(21)

        assertEquals(
            expected = 1,
            actual = testEffect0.sampleMemory(),
        )

        assertEquals(
            expected = 0,
            actual = testEffect1A.sampleMemory(),
        )

        assertEquals(
            expected = 12,
            actual = testEffect1A.sampleMemory(),
        )

        assertEquals(
            expected = 21,
            actual = testEffect2.sampleMemory(),
        )

        // End the effect
        handle.endExternally()

        // Stimulate the system after the effect ended (now it shouldn't be effective)

        testEffect0.stimulate(2)
        testEffect1A.stimulate(13)
        testEffect1B.stimulate(14)
        testEffect2.stimulate(22)

        assertEquals(
            expected = 1,
            actual = testEffect0.sampleMemory(),
        )

        assertEquals(
            expected = 0,
            actual = testEffect1A.sampleMemory(),
        )

        assertEquals(
            expected = 12,
            actual = testEffect1A.sampleMemory(),
        )

        assertEquals(
            expected = 21,
            actual = testEffect2.sampleMemory(),
        )
    }
}
