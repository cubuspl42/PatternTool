package dev.toolkt.reactive.reactive_list

class ReactiveListMap<ElementT, TransformedElementT>(
    source: ReactiveList<ElementT>,
    transform: (ElementT) -> TransformedElementT,
) : StatefulReactiveList<TransformedElementT>(
    initialElements = source.currentElements.map(transform),
    givenChanges = source.changes.map { change ->
        change.map(transform)
    },
)
