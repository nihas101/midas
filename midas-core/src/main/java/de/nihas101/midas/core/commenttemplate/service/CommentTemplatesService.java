package de.nihas101.midas.core.commenttemplate.service;

import de.nihas101.midas.api.commenttemplate.CommentTemplate;
import de.nihas101.midas.api.commenttemplate.CommentTemplates;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesReader;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesWriter;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.core.commenttemplate.dto.DefaultCommentTemplate;
import de.nihas101.midas.core.commenttemplate.dto.DefaultCommentTemplates;
import de.nihas101.midas.persistance.commenttemplate.CommentTemplateEntity;
import de.nihas101.midas.persistance.commenttemplate.CommentTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentTemplatesService implements CommentTemplatesReader, CommentTemplatesWriter {

    private final CommentTemplateRepository commentTemplateRepository;

    @Override
    @Transactional(readOnly = true)
    public CommentTemplates getTemplates() {
        return new DefaultCommentTemplates(
                commentTemplateRepository.findAllByOrderByTextAsc()
                        .stream()
                        .map(DefaultCommentTemplate::fromEntity)
                        .toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getSuggestions(final BookingType bookingType) {
        final List<CommentTemplateEntity> allTemplates = commentTemplateRepository.findAllByOrderByTextAsc();

        return allTemplates.stream()
                .filter(template -> {
                    if (bookingType == null) {
                        return true;
                    }
                    return template.getBookingTypes() == null ||
                            template.getBookingTypes().isEmpty() ||
                            template.getBookingTypes().contains(bookingType);
                })
                .map(CommentTemplateEntity::getText)
                .filter(text -> text != null && !text.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    @Transactional
    public CommentTemplate save(final CommentTemplate template) {
        if (template == null) {
            throw new IllegalArgumentException("Template must not be null");
        }
        final CommentTemplateEntity entity = DefaultCommentTemplate.fromDto(template);
        final CommentTemplateEntity saved = commentTemplateRepository.save(entity);
        return DefaultCommentTemplate.fromEntity(saved);
    }

    @Override
    @Transactional
    public void delete(final Integer id) {
        if (id == null) {
            return;
        }
        commentTemplateRepository.deleteById(id);
    }
}
