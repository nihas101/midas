package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.commenttemplate.CommentTemplate;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesReader;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesWriter;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.headless.rest.dto.RequestCommentTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comment-templates")
@RequiredArgsConstructor
@Tag(name = "Comment Templates", description = "Controller for comment templates CRUD.")
public class CommentTemplatesController {

    private final CommentTemplatesReader commentTemplatesReader;
    private final CommentTemplatesWriter commentTemplatesWriter;

    @Operation(
            summary = "Get suggestions for the comment of a booking type",
            description = "Returns suggestions for the comment of a booking type."
    )
    @GetMapping("/suggestions")
    public List<String> getSuggestions(@RequestParam(required = false) final BookingType bookingType) {
        return commentTemplatesReader.getSuggestions(bookingType);
    }

    @Operation(
            summary = "Save a comment template",
            description = "Saves a comment template."
    )
    @PostMapping
    public ResponseEntity<Void> saveTemplate(
            @NotNull
            @RequestBody final RequestCommentTemplate commentTemplate
    ) {
        commentTemplatesWriter.save(commentTemplate.toDomain());
        return ResponseEntity.ok()
                .build();
    }

    @Operation(
            summary = "Delete a comment template",
            description = "Deletes a comment template."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(
            @NotNull
            @Positive
            @PathVariable final Integer id
    ) {
        final CommentTemplate toDelete = commentTemplatesReader.getTemplate(id);
        if (toDelete == null) {
            return ResponseEntity.notFound()
                    .build();
        }

        commentTemplatesWriter.delete(toDelete.getId());
        return ResponseEntity.ok()
                .build();
    }

}
