package io.ktlab.bshelper.model.dto.serializer

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.format.DateTimeFormatter

// serialize 2023-03-14T14:23:21.456254Z
class LocalDateTimeAsStringSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) = encoder.encodeString(
        value.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
    )

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val str = decoder.decodeString()
        //    2023-08-09T11:18:14.812561Z, remove the last Z
        return LocalDateTime.parse(str.removeSuffix("Z"))
    }
}