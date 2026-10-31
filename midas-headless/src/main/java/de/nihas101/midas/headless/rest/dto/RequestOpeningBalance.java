package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestOpeningBalance {

    @NotNull
    @Positive
    private Integer shareholderId;

    @NotNull
    private MoneyAmountDto openingBalance;

    @NotNull
    private Year year;

    public OpeningBalance toDomain(final Integer id) {
        return new DefaultOpeningBalance(
                id,
                this.getShareholderId(),
                this.getOpeningBalance() != null ? this.getOpeningBalance().toDomain() : null,
                this.getYear(),
                Source.USER
        );
    }

    public OpeningBalance toDomain() {
        return new DefaultOpeningBalance(
                null,
                this.getShareholderId(),
                this.getOpeningBalance() != null ? this.getOpeningBalance().toDomain() : null,
                this.getYear(),
                Source.USER
        );
    }
}
