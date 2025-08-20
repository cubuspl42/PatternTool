package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.reactive.components.Component
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.event_stream.forEach
import org.w3c.dom.Document
import org.w3c.dom.Text

fun Document.createReactiveTextNode(
    data: Cell<String>,
): Text = data.formAndForget(
    create = { initialValue: String ->
        this.createTextNode(
            data = initialValue,
        )
    },
    update = { textNode: Text, newValue: String ->
        textNode.data = newValue
    },
)


fun Document.createReactiveTextComponent(
    data: Cell<String>,
): Component<Text> = object : Component<Text> {
    override fun buildLeaf(): Effect<Text> = Effect.prepared {
        val textNode = this@createReactiveTextComponent.createTextNode(
            data = data.sample(),
        )

        Effect.pureTriggering(
            result = textNode,
            trigger = data.newValues.forEach { newData ->
                Actions.mutate {
                    textNode.data = newData
                }
            },
        )
    }
}
