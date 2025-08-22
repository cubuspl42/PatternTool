package dev.toolkt.reactive.reactive_list

class MapReactiveList<ElementT, TransformedElementT>(
    source: ReactiveList<ElementT>,
    transform: (ElementT) -> TransformedElementT,
) : StatefulReactiveList<TransformedElementT>(
    initialElements = source.currentElementsUnmanaged.map(transform),
    givenChanges = source.changes.map { change ->
        change.map(transform)
    },
)
