package diy.lingerie.frp.dynamic_list

import diy.lingerie.frp.event_stream.DependentEventStream
import diy.lingerie.frp.event_stream.EventStream
import diy.lingerie.frp.vertices.dynamic_list.DynamicListVertex
import diy.lingerie.frp.vertices.dynamic_list.MapDynamicListVertex

abstract class ActiveDynamicList<E>() : DynamicList<E>() {
    final override val currentElements: List<E>
        get() = vertex.currentElements

    final override val changes: EventStream<Change<E>>
        get() = DependentEventStream(vertex = vertex)

    final override fun <Er> map(
        transform: (E) -> Er,
    ): DynamicList<Er> = DependentDynamicList(
        vertex = MapDynamicListVertex(
            source = this.vertex,
            transform = transform,
        ),
    )

    final override fun <T : Any> pipe(
        target: T,
        mutableList: MutableList<in E>,
    ) {
        copyNow(mutableList = mutableList)

        changes.pipe(
            target = target,
        ) { change ->
            change.applyTo(mutableList = mutableList)
        }
    }

    internal abstract val vertex: DynamicListVertex<E>
}
