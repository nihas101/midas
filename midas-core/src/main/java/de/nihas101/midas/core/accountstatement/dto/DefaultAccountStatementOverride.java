package de.nihas101.midas.core.accountstatement.dto;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverride;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverrideEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultAccountStatementOverride implements AccountStatementOverride {

    private Integer id;

    private Integer shareholderId;

    private Integer year;

    private BookingType bookingType;

    private String labelOverride;

    private Boolean hidden;

    private MoneyAmount amount;

    public static AccountStatementOverride fromEntity(final AccountStatementOverrideEntity entity) {
        if (entity == null) {
            return null;
        }

        return new DefaultAccountStatementOverride(
                entity.getId(),
                entity.getShareholder() != null ? entity.getShareholder().getId() : null,
                entity.getYear(),
                entity.getBookingType(),
                entity.getLabelOverride(),
                entity.getHidden(),
                entity.getAmount()
        );
    }
}
