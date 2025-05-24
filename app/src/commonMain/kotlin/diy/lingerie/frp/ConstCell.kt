package diy.lingerie.frp

internal class ConstCell<V>(
    val constValue: V,
) : Cell<V>() {
    override val currentValue: V
        get() = constValue

    override val newValues: EventStream<V> = EventStream.Never

    override val changes: EventStream<Nothing> = EventStream.Never

    override fun <Vr> map(
        transform: (V) -> Vr,
    ): Cell<Vr> = ConstCell(
        constValue = transform(constValue),
    )

    override fun <T: Any> form(
        create: (V) -> T,
        update: (T, V) -> Unit,
    ): T = create(constValue)
}
