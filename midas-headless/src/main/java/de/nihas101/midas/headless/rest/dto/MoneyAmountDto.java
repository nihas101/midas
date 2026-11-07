package de.nihas101.midas.headless.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.nihas101.midas.commons.MoneyAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Schema(
        type = "number",
        format = "decimal",
        example = "100.50",
        description = "Money amount represented as decimal"
)
public class MoneyAmountDto {

    @JsonValue
    private BigDecimal value;

    @JsonCreator
    public MoneyAmountDto(final BigDecimal value) {
        this.value = value != null ? value : ZERO;
    }

    public MoneyAmountDto(final MoneyAmount moneyAmount) {
        this(moneyAmount != null ? moneyAmount.toBigDecimalForInput() : ZERO);
    }

    public static MoneyAmountDto of(final BigDecimal value) {
        return new MoneyAmountDto(value);
    }

    public static MoneyAmountDto of(final String value) {
        return new MoneyAmountDto(value != null ? new BigDecimal(value) : ZERO);
    }

    public static MoneyAmountDto fromDomain(final MoneyAmount moneyAmount) {
        if (moneyAmount == null) {
            return null;
        }
        return new MoneyAmountDto(moneyAmount);
    }

    public MoneyAmount toDomain() {
        return MoneyAmount.of(this.value);
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
