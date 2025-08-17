package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream

class Map2Cell<V1, V2, Vr>(
    source1: Cell<V1>,
    source2: Cell<V2>,
    transform: (V1, V2) -> Vr,
) : StatefulCell<Vr>(
    initialValue = transform(
        source1.currentValueUnmanaged,
        source2.currentValueUnmanaged,
    ),
    givenValues = EventStream.mergeAll(
        source1.newValues.map { newValue1 ->
            transform(
                newValue1,
                source2.currentValueUnmanaged,
            )
        },
        source2.newValues.map { newValue2 ->
            transform(
                source1.currentValueUnmanaged,
                newValue2,
            )
        },
    ),
)

