package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.managed_io.MomentContext

class MapAtCell<TransformedValueT> private constructor(
    initialTransformedValue: TransformedValueT,
    givenTransformedValues: EventStream<TransformedValueT>,
) : StatefulCell<TransformedValueT>(
    initialValue = initialTransformedValue,
    givenValues = givenTransformedValues,
) {
    companion object {
        context(momentContext: MomentContext) fun <ValueT, TransformedValueT> construct(
            source: Cell<ValueT>,
            transform: context(MomentContext) (ValueT) -> TransformedValueT,
        ): MapAtCell<TransformedValueT> = MapAtCell(
            initialTransformedValue = transform(source.currentValueUnmanaged),
            givenTransformedValues = source.newValues.mapAt(transform),
        )
    }
}
