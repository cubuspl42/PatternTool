package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream

class Map3Cell<V1, V2, V3, Vr>(
    source1: Cell<V1>,
    source2: Cell<V2>,
    source3: Cell<V3>,
    transform: (V1, V2, V3) -> Vr,
) : StatefulCell<Vr>(
    initialValue = transform(
        source1.currentValue,
        source2.currentValue,
        source3.currentValue,
    ),
    givenValues = EventStream.mergeAll(
        source1.newValues.map { newValue1 ->
            transform(
                newValue1,
                source2.currentValue,
                source3.currentValue,
            )
        },
        source2.newValues.map { newValue2 ->
            transform(
                source1.currentValue,
                newValue2,
                source3.currentValue,
            )
        },
        source3.newValues.map { newValue3 ->
            transform(
                source1.currentValue,
                source2.currentValue,
                newValue3,
            )
        },
    ),
)
