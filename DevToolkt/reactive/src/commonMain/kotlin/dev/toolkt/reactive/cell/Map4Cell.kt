package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream

class Map4Cell<V1, V2, V3, V4, Vr>(
    source1: Cell<V1>,
    source2: Cell<V2>,
    source3: Cell<V3>,
    source4: Cell<V4>,
    transform: (V1, V2, V3, V4) -> Vr,
) : StatefulCell<Vr>(
    initialValue = transform(
        source1.currentValue,
        source2.currentValue,
        source3.currentValue,
        source4.currentValue,
    ),
    givenValues = EventStream.mergeAll(
        source1.newValues.map { newValue1 ->
            transform(
                newValue1,
                source2.currentValue,
                source3.currentValue,
                source4.currentValue,
            )
        },
        source2.newValues.map { newValue2 ->
            transform(
                source1.currentValue,
                newValue2,
                source3.currentValue,
                source4.currentValue,
            )
        },
        source3.newValues.map { newValue3 ->
            transform(
                source1.currentValue,
                source2.currentValue,
                newValue3,
                source4.currentValue,
            )
        },
        source4.newValues.map { newValue4 ->
            transform(
                source1.currentValue,
                source2.currentValue,
                source3.currentValue,
                newValue4,
            )
        },
    ),
)
