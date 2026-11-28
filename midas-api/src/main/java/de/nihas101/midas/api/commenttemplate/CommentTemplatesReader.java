package de.nihas101.midas.api.commenttemplate;

import de.nihas101.midas.commons.BookingType;

import java.util.List;

public interface CommentTemplatesReader {

    CommentTemplate getTemplate(final Integer id);

    CommentTemplates getTemplates();

    List<String> getSuggestions(final BookingType bookingType);
}
