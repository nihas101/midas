package de.nihas101.midas.headless.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementOrderRequest {
    @NotNull
    @Positive
    private Integer shareholderId;

    @NotNull
    private Year year;

    @NotNull
    @NotEmpty
    private List<String> rowKeys;
}
