package diy.lingerie.reactive.reactive_list

import diy.lingerie.reactive.vertices.reactive_list.DependentReactiveListVertex

class DependentReactiveList<E>(
    override val vertex: DependentReactiveListVertex<E>,
) : ActiveReactiveList<E>()
