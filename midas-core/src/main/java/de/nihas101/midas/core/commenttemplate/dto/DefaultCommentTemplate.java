package de.nihas101.midas.core.commenttemplate.dto;

import de.nihas101.midas.api.commenttemplate.CommentTemplate;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.persistance.commenttemplate.CommentTemplateEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultCommentTemplate implements CommentTemplate {
    private Integer id;
    private String text;

    @Builder.Default
    private final Set<BookingType> bookingTypes = new HashSet<>();

    public static DefaultCommentTemplate fromEntity(final CommentTemplateEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DefaultCommentTemplate(
                entity.getId(),
                entity.getText(),
                entity.getBookingTypes() != null ? new HashSet<>(entity.getBookingTypes()) : new HashSet<>()
        );
    }

    public static CommentTemplateEntity fromDto(final CommentTemplate commentTemplate) {
        if (commentTemplate == null) {
            return null;
        }
        return new CommentTemplateEntity(
                commentTemplate.getId(),
                commentTemplate.getText(),
                commentTemplate.getBookingTypes() != null ? new HashSet<>(commentTemplate.getBookingTypes()) : new HashSet<>()
        );
    }
}
