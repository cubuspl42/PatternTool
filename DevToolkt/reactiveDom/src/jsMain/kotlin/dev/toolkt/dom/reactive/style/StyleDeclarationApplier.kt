package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.style.PurePropertyApplier
import dev.toolkt.dom.pure.style.PurePropertyGroup
import dev.toolkt.dom.pure.style.PurePropertyKind
import dev.toolkt.dom.pure.style.PurePropertyValue
import dev.toolkt.dom.pure.style.setOrRemoveProperty
import org.w3c.dom.css.CSSStyleDeclaration

fun PurePropertyGroup.applyTo(
    styleDeclaration: CSSStyleDeclaration,
) {
    applyProperties(
        applier = StyleDeclarationApplier(
            styleDeclaration = styleDeclaration,
        ),
    )
}

internal class StyleDeclarationApplier(
    private val styleDeclaration: CSSStyleDeclaration,
) : PurePropertyApplier {
    override fun applyProperty(
        kind: PurePropertyKind,
        value: PurePropertyValue?,
    ) {
        styleDeclaration.setOrRemoveProperty(
            kind = kind,
            value = value,
        )
    }
}
