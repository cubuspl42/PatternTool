package dev.toolkt.reactive.cell

class Map2Cell<V1, V2, Vr>(
    source1: Cell<V1>,
    source2: Cell<V2>,
    /**
     * A transformation function that may access the time of the transformation
     * (e.g. sample some other cells).
     */
    transform: (V1, V2) -> Vr,
) : StatefulCell<Vr>(
    initialValue = transform(
        source1.currentValue,
        source2.currentValue,
    ),
    givenValues = TODO(),
)
