package de.nihas101.midas.headless.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockDto {
    private Integer shareholderId;
    private Year year;
    private boolean locked;
}
