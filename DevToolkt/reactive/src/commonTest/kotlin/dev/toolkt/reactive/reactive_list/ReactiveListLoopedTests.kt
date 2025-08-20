package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.effect.Moments
import kotlin.test.Test
import kotlin.test.assertEquals

class ReactiveListLoopedTests {
    class Parent(
        val children: ReactiveList<Child>,
    )

    class Child(
        val id: Int,
        val parent: Parent,
    )

    private data class Setup(
        val parent: Parent,
        val sampledChildren: List<Child>,
    )

    @Test
    fun testLooped() {
        val childIds = MutableReactiveList.createExternally(
            initialContent = listOf(0, 1, 2),
        )

        val setup = Moments.external {
            val parent = ReactiveList.looped(
                placeholderReactiveList = ReactiveList.of(),
            ) { loopedChildren ->
                val parent = Parent(
                    children = loopedChildren,
                )

                val children = childIds.map { id ->
                    Child(
                        id = id,
                        parent = parent,
                    )
                }

                Pair(
                    parent,
                    children,
                )
            }

            Setup(
                parent = parent,
                sampledChildren = parent.children.sampleContent(),
            )
        }

        val parent = setup.parent
        val sampledChildren = setup.sampledChildren

        assertEquals(
            expected = emptyList(),
            actual = sampledChildren,
        )

        val finalChildren = parent.children.sampleContentExternally()

        assertEquals(
            expected = 3,
            actual = finalChildren.size,
        )

        val (child0, child1, child2) = finalChildren

        assertEquals(
            expected = 0,
            actual = child0.id,
        )

        assertEquals(
            expected = parent,
            actual = child0.parent,
        )

        assertEquals(
            expected = 1,
            actual = child1.id,
        )

        assertEquals(
            expected = parent,
            actual = child1.parent,
        )

        assertEquals(
            expected = 2,
            actual = child2.id,
        )

        assertEquals(
            expected = parent,
            actual = child2.parent,
        )
    }
}
