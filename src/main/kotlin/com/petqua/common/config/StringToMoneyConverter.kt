package com.petqua.common.config

import com.petqua.common.domain.Money
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToMoneyConverter : Converter<String, Money> {
    override fun convert(source: String): Money? {
        return Money.from(source.toBigDecimal())
    }
}
