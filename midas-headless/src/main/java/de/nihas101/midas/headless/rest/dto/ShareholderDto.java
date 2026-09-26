package de.nihas101.midas.headless.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareholderDto {
    private Integer id;
    private Integer displayId;
    private String firstName;
    private String lastName;
}
