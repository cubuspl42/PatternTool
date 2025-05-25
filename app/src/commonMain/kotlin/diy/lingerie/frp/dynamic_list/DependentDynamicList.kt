package diy.lingerie.frp.dynamic_list

import diy.lingerie.frp.vertices.dynamic_list.DependentDynamicListVertex

class DependentDynamicList<E>(
    override val vertex: DependentDynamicListVertex<E>,
) : ActiveDynamicList<E>()
