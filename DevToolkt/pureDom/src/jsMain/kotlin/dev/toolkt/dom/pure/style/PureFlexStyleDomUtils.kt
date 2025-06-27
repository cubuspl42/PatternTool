package dev.toolkt.dom.pure.style

import org.w3c.dom.css.CSSStyleDeclaration

fun PureFlexStyle.applyTo(
    styleDeclaration: CSSStyleDeclaration,
) {
    styleDeclaration.setOrRemoveProperty(
        kind = PurePropertyKind.FlexDirection,
        value = direction,
    )

    styleDeclaration.setOrRemoveProperty(
        kind = PurePropertyKind.AlignItems,
        value = alignItems,
    )

    styleDeclaration.setOrRemoveProperty(
        kind = PurePropertyKind.JustifyContent,
        value = justifyContent,
    )

    styleDeclaration.setOrRemoveProperty(
        kind = PurePropertyKind.Gap,
        value = gap,
    )
}
