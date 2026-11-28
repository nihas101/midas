package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.commenttemplate.CommentTemplate;
import de.nihas101.midas.commons.BookingType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Comment template details")
public class ResponseCommentTemplate {

    @NotNull
    @Positive
    @Schema(description = "Unique ID of the comment template", example = "1")
    private Integer id;

    @NotNull
    @NotEmpty
    @Schema(description = "Template text", example = "Monthly interest payment")
    private String text;

    @Schema(description = "Booking types applicable to this template", example = "[\"DEPOSIT\", \"WITHDRAWAL\"]")
    private Set<BookingType> bookingTypes;

    public static ResponseCommentTemplate toDto(final CommentTemplate template) {
        if (template == null) {
            return null;
        }
        return ResponseCommentTemplate.builder()
                .id(template.getId())
                .text(template.getText())
                .bookingTypes(template.getBookingTypes())
                .build();
    }
}
