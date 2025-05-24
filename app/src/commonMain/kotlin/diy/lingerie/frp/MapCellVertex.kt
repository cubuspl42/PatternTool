package diy.lingerie.frp

internal class MapCellVertex<V, Vr>(
    private val source: CellVertex<V>,
    private val transform: (V) -> Vr,
) : DependentCellVertex<Vr>(
    initialValue = transform(source.currentValue),
) {
    override fun buildInitialSubscription(): Subscription = source.subscribe(
         listener = object : Listener<Cell.Change<V>> {
             override fun handle(change: Cell.Change<V>) {
                 val newValue = change.newValue

                 update(transform(newValue))
             }
         },
     )

    init {
        init()
    }
}
