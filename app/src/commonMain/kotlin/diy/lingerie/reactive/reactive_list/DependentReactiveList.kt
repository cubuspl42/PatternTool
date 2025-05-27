package diy.lingerie.reactive.reactive_list

import diy.lingerie.reactive.vertices.dynamic_list.DependentDynamicListVertex

class DependentReactiveList<E>(
    override val vertex: DependentDynamicListVertex<E>,
) : ActiveReactiveList<E>()
