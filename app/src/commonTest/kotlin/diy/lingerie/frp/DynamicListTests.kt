package diy.lingerie.frp

import kotlin.test.Test
import kotlin.test.assertEquals

class DynamicListTests {
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

        val change = DynamicList.Change(
            updates = setOf(
                DynamicList.Change.Update.change(
                    indexRange = 2..3,
                    changedElements = listOf(21, 31),
                ),
                DynamicList.Change.Update.remove(
                    indexRange = 5..6,
                ),
                DynamicList.Change.Update.insert(
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
}
