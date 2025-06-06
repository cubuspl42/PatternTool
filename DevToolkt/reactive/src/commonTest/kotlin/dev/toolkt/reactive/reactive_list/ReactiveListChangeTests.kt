package dev.toolkt.reactive.reactive_list

import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListChangeTests {
    @Test
    fun testChangeApply() {
        val originalList = listOf(
            0,
            10,
            20,
            30,
            40,
            50,
            60,
            70,
            80,
            90,
        )

        val mutableList = originalList.toMutableList()

        val change = ReactiveList.Change(
            updates = setOf(
                ReactiveList.Change.Update.change(
                    indexRange = 2..3,
                    changedElements = listOf(21, 31),
                ),
                ReactiveList.Change.Update.remove(
                    indexRange = 5..6,
                ),
                ReactiveList.Change.Update.insert(
                    index = 9,
                    newElements = listOf(81, 82, 83),
                ),
            ),
        )

        change.applyTo(mutableList)

        assertEquals(
            expected = listOf(
                0,
                10,
                21,
                31,
                40,
                70,
                80,
                81,
                82,
                83,
                90,
            ),
            actual = mutableList,
        )
    }

    @Test
    fun testChangeApply_fillEmpty() {
        val originalList = emptyList<Int>()

        val mutableList = originalList.toMutableList()

        val change = ReactiveList.Change(
            updates = setOf(
                ReactiveList.Change.Update.change(
                    indexRange = 0 until 0,
                    changedElements = listOf(10, 20),
                ),
            ),
        )

        change.applyTo(mutableList)

        assertEquals(
            expected = listOf(10, 20),
            actual = mutableList,
        )
    }
}
