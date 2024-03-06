package com.petqua.common.domain

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.petqua.common.util.setDefaultScale
import jakarta.persistence.Embeddable
import java.math.BigDecimal

private class MoneyDeserializer : JsonDeserializer<Money>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Money {
        val value = p.codec.readValue(p, String::class.java)
        return Money.from(value.toBigDecimal())
    }
}

private class MoneySerializer : JsonSerializer<Money>() {
    override fun serialize(money: Money, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeNumber(money.value.intValueExact()) // TODO INT? BigDecimal?
    }
}

@JsonDeserialize(using = MoneyDeserializer::class)
@JsonSerialize(using = MoneySerializer::class)
@Embeddable
data class Money private constructor (
    val value: BigDecimal
) {

    companion object {
        fun from(value: Long): Money {
            return Money(BigDecimal.valueOf(value).setDefaultScale())
        }

        fun from(value: BigDecimal): Money {
            return Money(value.setDefaultScale())
        }
    }

    operator fun plus(other: Money): Money {
        return Money(this.value + other.value)
    }

    operator fun plus(other: BigDecimal): Money {
        return Money(this.value.plus(other))
    }

    operator fun plus(other: Long): Money {
        return Money(this.value.plus(BigDecimal.valueOf(other)))
    }

    operator fun minus(other: Money): Money {
        return Money(this.value - other.value)
    }

    operator fun minus(other: BigDecimal): Money {
        return Money(this.value.minus(other))
    }

    operator fun minus(other: Long): Money {
        return Money(this.value.minus(BigDecimal.valueOf(other)))
    }

    operator fun div(other: Money): Money {
        return Money(this.value / other.value)
    }

    operator fun div(other: BigDecimal): Money {
        return Money(this.value.div(other))
    }

    operator fun div(divisor: Long): Money {
        return Money(this.value.div(BigDecimal.valueOf(divisor)))
    }

    operator fun times(other: Money): Money {
        return Money(this.value * other.value)
    }

    operator fun times(other: BigDecimal): Money {
        return Money(this.value.times(other))
    }

    operator fun times(other: Long): Money {
        return Money(this.value.times(BigDecimal.valueOf(other)))
    }
}
