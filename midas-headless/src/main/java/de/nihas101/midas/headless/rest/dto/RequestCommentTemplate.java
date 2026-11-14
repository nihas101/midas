package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.commenttemplate.CommentTemplate;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.core.commenttemplate.dto.DefaultCommentTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCommentTemplate {

    @NotNull
    @NotEmpty
    @Schema(description = "Comment template text", example = "Monthly interest payment")
    private String text;

    @Schema(description = "Booking types this template applies to", example = "[\"DEPOSIT\", \"WITHDRAWAL\"]")
    private Set<BookingType> bookingTypes;

    public CommentTemplate toDomain() {
        return DefaultCommentTemplate.builder()
                .text(this.getText())
                .bookingTypes(this.getBookingTypes())
                .build();
    }
}
