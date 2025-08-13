package dev.toolkt.geometry

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object PointSerializer : KSerializer<Point> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Point") {
        element<Double>("x")
        element<Double>("y")
    }

    override fun serialize(encoder: Encoder, value: Point) {
        encoder.encodeStructure(descriptor) {
            encodeDoubleElement(descriptor, 0, value.x)
            encodeDoubleElement(descriptor, 1, value.y)
        }
    }

    override fun deserialize(decoder: Decoder): Point {
        return decoder.decodeStructure(descriptor) {
            var x = 0.0
            var y = 0.0
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> x = decodeDoubleElement(descriptor, 0)
                    1 -> y = decodeDoubleElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw SerializationException("Unexpected index: $index")
                }
            }
            Point(x, y)
        }
    }
}
