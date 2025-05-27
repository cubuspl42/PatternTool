package diy.lingerie.reactive.dynamic_list

import diy.lingerie.reactive.vertices.dynamic_list.DependentDynamicListVertex

class DependentDynamicList<E>(
    override val vertex: DependentDynamicListVertex<E>,
) : ActiveDynamicList<E>()
