package diy.lingerie.utils.iterable

import kotlin.test.Test
import kotlin.test.assertEquals

class ClusterSimilarTests {
    @Test
    fun testClusterSimilar_empty() {
        val actual = emptyList<Char>().clusterSimilar { a, b -> a == b }

        assertEquals(
            expected = emptyList(),
            actual = actual,
        )
    }

    @Test
    fun testClusterSimilar_simple() {
        val actual = listOf(
            'a', 'x', 'y', 'B', '!', '_', 'C', 'P', 'T', '=', '1', '2', '3',
        ).clusterSimilar { a, b ->
            when {
                a.isDigit() && b.isDigit() -> true
                a.isLowerCase() && b.isLowerCase() -> true
                a.isUpperCase() && b.isUpperCase() -> true
                else -> false
            }
        }

        assertEquals(
            expected = listOf(
                listOf('a', 'x', 'y'),
                listOf('B'),
                listOf('!'),
                listOf('_'),
                listOf('C', 'P', 'T'),
                listOf('='),
                listOf('1', '2', '3'),
            ),
            actual = actual,
        )
    }

    @Test
    fun testClusterSimilar_singleElement() {
        val actual = listOf('a').clusterSimilar { a, b -> a == b }

        assertEquals(
            expected = listOf(listOf('a')),
            actual = actual,
        )
    }

    @Test
    fun testClusterSimilar_allSimilar() {
        val actual = listOf(1, 1, 1, 1).clusterSimilar { a, b -> a == b }

        assertEquals(
            expected = listOf(listOf(1, 1, 1, 1)),
            actual = actual,
        )
    }

    @Test
    fun testClusterSimilar_noSimilar() {
        val actual = listOf(1, 2, 3, 4).clusterSimilar { a, b -> false }

        assertEquals(
            expected = listOf(
                listOf(1),
                listOf(2),
                listOf(3),
                listOf(4),
            ),
            actual = actual,
        )
    }

    @Test
    fun testClusterSimilar_complexCondition() {
        val actual = listOf(1, 2, 2, 3, 5, 8, 13, 21).clusterSimilar { a, b ->
            (a + b) % 2 == 0
        }

        assertEquals(
            expected = listOf(
                listOf(1),
                listOf(2, 2),
                listOf(3, 5),
                listOf(8),
                listOf(13, 21),
            ),
            actual = actual,
        )
    }
}
