package diy.lingerie.frp

internal class DependentCell<V>(
    override val vertex: DependentCellVertex<V>,
) : ActiveCell<V>()
