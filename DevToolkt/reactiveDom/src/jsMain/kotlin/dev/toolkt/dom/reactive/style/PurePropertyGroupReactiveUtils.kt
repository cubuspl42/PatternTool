package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.style.PurePropertyGroup
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.forEach
import dev.toolkt.reactive.managed_io.Actions
import dev.toolkt.reactive.managed_io.Trigger
import org.w3c.dom.css.CSSStyleDeclaration

fun Cell<PurePropertyGroup>.bind(
    styleDeclaration: CSSStyleDeclaration,
): Trigger = forEach { propertyGroup ->
    Actions.mutate {
        propertyGroup.applyTo(
            styleDeclaration = styleDeclaration,
        )
    }
}
