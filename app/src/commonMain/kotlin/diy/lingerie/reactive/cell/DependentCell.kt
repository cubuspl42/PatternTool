package diy.lingerie.reactive.cell

import diy.lingerie.reactive.vertices.cell.DependentCellVertex

internal class DependentCell<V>(
    override val vertex: DependentCellVertex<V>,
) : ActiveCell<V>()
