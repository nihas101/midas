package de.nihas101.midas.api.commenttemplate;

import de.nihas101.midas.commons.BookingType;

import java.util.Set;

public interface CommentTemplate {

    Integer getId();

    String getText();

    Set<BookingType> getBookingTypes();

}
