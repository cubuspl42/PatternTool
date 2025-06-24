package dev.toolkt.reactive.reactive_list

import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListChangeTests {
    @Test
    fun testChangeApply_modify() {
        val originalList = listOf(
            0,
            10,
            20,
            30,
            40,
            50,
        )

        val mutableList = originalList.toMutableList()

        val change = ReactiveList.Change.single(
            ReactiveList.Change.Update.change(
                indexRange = 2..3,
                changedElements = listOf(21, 31),
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
                50,
            ),
            actual = mutableList,
        )
    }

    @Test
    fun testChangeApply_remove() {
        val originalList = listOf(
            0,
            10,
            20,
            30,
            40,
            50,
        )

        val mutableList = originalList.toMutableList()

        val change = ReactiveList.Change.single(
            ReactiveList.Change.Update.remove(
                indexRange = 3..4,
            ),
        )

        change.applyTo(mutableList)

        assertEquals(
            expected = listOf(
                0,
                10,
                20,
                50,
            ),
            actual = mutableList,
        )
    }

    @Test
    fun testChangeApply_insert() {
        val originalList = listOf(
            0,
            10,
            20,
            30,
        )

        val mutableList = originalList.toMutableList()

        val change = ReactiveList.Change.single(
            ReactiveList.Change.Update.insert(
                index = 2,
                newElements = listOf(11, 12, 13),
            ),
        )

        change.applyTo(mutableList)

        assertEquals(
            expected = listOf(
                0,
                10,
                11,
                12,
                13,
                20,
                30,
            ),
            actual = mutableList,
        )
    }

    @Test
    fun testChangeApply_fillEmpty() {
        val originalList = emptyList<Int>()

        val mutableList = originalList.toMutableList()

        val change = ReactiveList.Change.single(
            ReactiveList.Change.Update.change(
                indexRange = 0 until 0,
                changedElements = listOf(10, 20),
            ),
        )

        change.applyTo(mutableList)

        assertEquals(
            expected = listOf(10, 20),
            actual = mutableList,
        )
    }
}
