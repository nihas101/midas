package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.commons.BookingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentTemplateDto {
    private Integer id;
    private String text;
    private Set<BookingType> bookingTypes;
}
