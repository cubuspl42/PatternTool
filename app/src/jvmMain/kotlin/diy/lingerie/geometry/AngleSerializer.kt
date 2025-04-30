package diy.lingerie.geometry

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

object AngleSerializer : KSerializer<RelativeAngle> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Angle") {
        element<Double>("fi")
    }

    override fun serialize(encoder: Encoder, value: RelativeAngle) {
        encoder.encodeStructure(descriptor) {
            encodeDoubleElement(descriptor, 0, value.fi)
        }
    }

    override fun deserialize(
        decoder: Decoder,
    ): RelativeAngle = decoder.decodeStructure(descriptor) {
        var fi = 0.0

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> fi = decodeDoubleElement(descriptor, 0)
                CompositeDecoder.DECODE_DONE -> break
                else -> throw SerializationException("Unexpected index: $index")
            }
        }

        RelativeAngle.Radial(fi = fi)
    }
}
