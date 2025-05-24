package diy.lingerie.frp

class DependentDynamicList<E>(
    override val vertex: DependentDynamicListVertex<E>,
) : ActiveDynamicList<E>()
