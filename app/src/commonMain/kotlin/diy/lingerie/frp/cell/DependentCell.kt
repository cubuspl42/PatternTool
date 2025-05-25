package diy.lingerie.frp.cell

import diy.lingerie.frp.vertices.cell.DependentCellVertex

internal class DependentCell<V>(
    override val vertex: DependentCellVertex<V>,
) : ActiveCell<V>()
