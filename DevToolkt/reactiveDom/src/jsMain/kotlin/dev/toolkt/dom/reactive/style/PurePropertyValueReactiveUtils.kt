package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.style.PurePropertyKind
import dev.toolkt.dom.pure.style.PurePropertyValue
import dev.toolkt.dom.pure.style.setOrRemoveProperty
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import org.w3c.dom.css.CSSStyleDeclaration

fun Cell<PurePropertyValue>.bind(
    styleDeclaration: CSSStyleDeclaration,
    kind: PurePropertyKind,
): Subscription = bind(
    target = styleDeclaration,
) { styleDeclaration, propertyValue ->
    styleDeclaration.setOrRemoveProperty(
        kind = kind,
        value = propertyValue,
    )
}
