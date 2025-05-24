package diy.lingerie.frp

internal class ConstCell<V>(
    constValue: V,
) : Cell<V>() {
    override val currentValue: V = constValue

    override val changes: EventStream<Nothing> = EventStream.Never
}
