package dev.toolkt.reactive.cell

/**
 * A mapped cell with a possibly-impure transformation function.
 * TODO: Make this pure
 */
class MapCell<V, Vr>(
    source: Cell<V>,
    /**
     * A transformation function that may access the time of the transformation
     * (e.g. sample some other cells).
     */
    transform: (V) -> Vr,
) : StatefulCell<Vr>(
    initialValue = transform(source.currentValueUnmanaged),
    givenValues = source.newValues.map(transform),
)
