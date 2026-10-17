package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.commenttemplate.CommentTemplate;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.core.commenttemplate.dto.DefaultCommentTemplate;
import de.nihas101.midas.core.commenttemplate.service.CommentTemplatesService;
import de.nihas101.midas.headless.rest.dto.CommentTemplateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comment-templates")
@RequiredArgsConstructor
public class CommentTemplatesController {

    private final CommentTemplatesService commentTemplatesService;

    @GetMapping("/suggestions")
    public List<String> getSuggestions(@RequestParam(required = false) final BookingType bookingType) {
        return commentTemplatesService.getSuggestions(bookingType);
    }

    @PostMapping
    public ResponseEntity<Void> saveTemplate(@RequestBody final CommentTemplateDto dto) {
        final CommentTemplate template = DefaultCommentTemplate.builder()
                .id(dto.getId())
                .text(dto.getText())
                .bookingTypes(dto.getBookingTypes())
                .build();
        commentTemplatesService.save(template);
        return ResponseEntity.ok().build();
    }
}
