package de.nihas101.midas.core.commontemplate.service;

import de.nihas101.midas.api.commenttemplate.CommentTemplate;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.core.commenttemplate.dto.DefaultCommentTemplate;
import de.nihas101.midas.core.commenttemplate.service.CommentTemplatesService;
import de.nihas101.midas.persistance.commenttemplate.CommentTemplateEntity;
import de.nihas101.midas.persistance.commenttemplate.CommentTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentTemplatesServiceTest {

    @Mock
    private CommentTemplateRepository commentTemplateRepository;

    @InjectMocks
    private CommentTemplatesService commentTemplatesService;

    @Test
    void getSuggestions_whenBookingTypeIsNull_returnsAllSuggestions() {
        final CommentTemplateEntity template1 = CommentTemplateEntity.builder()
                .id(1)
                .text("General Note")
                .bookingTypes(emptySet())
                .build();

        final CommentTemplateEntity template2 = CommentTemplateEntity.builder()
                .id(2)
                .text("Withdrawal Note")
                .bookingTypes(Set.of(BookingType.WITHDRAWAL))
                .build();

        when(commentTemplateRepository.findAllByOrderByTextAsc()).thenReturn(List.of(template1, template2));

        final List<String> suggestions = commentTemplatesService.getSuggestions(null);

        assertEquals(2, suggestions.size());
        assertTrue(suggestions.contains("General Note"));
        assertTrue(suggestions.contains("Withdrawal Note"));
    }

    @Test
    void getSuggestions_whenBookingTypeIsSpecified_returnsMatchingAndGeneralSuggestions() {
        final CommentTemplateEntity generalTemplate = CommentTemplateEntity.builder()
                .id(1)
                .text("General Note")
                .bookingTypes(emptySet())
                .build();

        final CommentTemplateEntity withdrawalTemplate = CommentTemplateEntity.builder()
                .id(2)
                .text("Withdrawal Note")
                .bookingTypes(Set.of(BookingType.WITHDRAWAL, BookingType.COMPENSATION))
                .build();

        final CommentTemplateEntity interestTemplate = CommentTemplateEntity.builder()
                .id(3)
                .text("Interest Only Note")
                .bookingTypes(Set.of(BookingType.INTEREST))
                .build();

        when(commentTemplateRepository.findAllByOrderByTextAsc()).thenReturn(List.of(generalTemplate, withdrawalTemplate, interestTemplate));

        final List<String> suggestions = commentTemplatesService.getSuggestions(BookingType.WITHDRAWAL);

        assertEquals(2, suggestions.size());
        assertTrue(suggestions.contains("General Note"));
        assertTrue(suggestions.contains("Withdrawal Note"));
    }

    @Test
    void save_success() {
        final CommentTemplate dto = DefaultCommentTemplate.builder()
                .text("New Template")
                .bookingTypes(Set.of(BookingType.WITHDRAWAL))
                .build();

        final CommentTemplateEntity savedEntity = CommentTemplateEntity.builder()
                .id(1)
                .text("New Template")
                .bookingTypes(Set.of(BookingType.WITHDRAWAL))
                .build();

        when(commentTemplateRepository.save(any(CommentTemplateEntity.class))).thenReturn(savedEntity);

        final CommentTemplate result = commentTemplatesService.save(dto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("New Template", result.getText());
        assertTrue(result.getBookingTypes().contains(BookingType.WITHDRAWAL));
    }

    @Test
    void delete_success() {
        commentTemplatesService.delete(5);
        verify(commentTemplateRepository).deleteById(5);
    }
}
