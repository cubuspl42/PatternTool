package dev.toolkt.dom.pure.style

import org.w3c.dom.css.CSSStyleDeclaration

var CSSStyleDeclaration.gap: String
    get() = this.getPropertyValue("gap")
    set(value) {
        this.setProperty("gap", value)
    }

fun CSSStyleDeclaration.setOrRemoveProperty(
    /**
     * The name of the CSS property to set or remove.
     */
    kind: PurePropertyKind,
    /**
     * The value to set for the property. If null (or empty), the property will be removed.
     */
    value: PurePropertyValue?,
) {
    when {
        value == null || value.cssString.isEmpty() -> {
            this.removeProperty(kind.cssName)
        }

        else -> {
            this.setProperty(kind.cssName, value.cssString)
        }
    }
}
