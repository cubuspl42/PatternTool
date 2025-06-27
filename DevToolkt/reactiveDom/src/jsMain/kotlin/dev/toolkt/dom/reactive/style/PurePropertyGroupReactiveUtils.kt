package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.style.PurePropertyGroup
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import org.w3c.dom.css.CSSStyleDeclaration

fun Cell<PurePropertyGroup>.bind(
    styleDeclaration: CSSStyleDeclaration,
): Subscription = bind(
    target = styleDeclaration,
) { styleDeclaration, propertyGroup ->
    propertyGroup.applyTo(
        styleDeclaration = styleDeclaration,
    )
}
