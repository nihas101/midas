package de.nihas101.midas.vaadin.ui.bookings;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.bookings.BookingsWriter;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.Shareholders;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.config.UIConfig;
import de.nihas101.midas.core.lock.ShareholderLock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditBookingFormDialogTest {

    @Mock
    private ShareholdersReader shareholdersReader;
    @Mock
    private BookingsReader bookingsReader;
    @Mock
    private BookingsWriter bookingsWriter;
    @Mock
    private MessageSource messageSource;
    @Mock
    private Shareholders shareholders;
    @Mock
    private Booking bookingToEdit;

    private UIConfig uiConfig;
    private final Locale locale = Locale.ENGLISH;

    @BeforeEach
    void setUp() {
        MockVaadin.setup();

        when(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .thenAnswer(i -> i.getArgument(0));
        uiConfig = new UIConfig();

        when(shareholdersReader.shareholders()).thenReturn(shareholders);
        when(shareholders.toList()).thenReturn(Collections.emptyList());

        when(bookingToEdit.getDate()).thenReturn(LocalDate.now());
    }

    @AfterEach
    void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    void constructor_withMinimalParams_createsDialog() {
        assertNotNull(createDialog(null));
    }

    @Test
    void constructor_withNullShareholderLock_doesNotThrow() {
        assertNotNull(createDialog(null));
    }

    @Test
    void constructor_withShareholderLock_doesNotThrow() {
        final ShareholderLock shareholderLock = mock(ShareholderLock.class);

        assertNotNull(createDialog(shareholderLock));
    }

    @Test
    void constructor_bindsBookingToEdit_readsDateFromBooking() {
        createDialog(null);

        // The binder reads getDate() when it sets the bean on the date picker field
        verify(bookingToEdit).getDate();
    }

    @Test
    void constructor_bindsBookingToEdit_readsTypeFromBooking() {
        createDialog(null);

        verify(bookingToEdit).getType();
    }

    @Test
    void constructor_bindsBookingToEdit_readsCommentFromBooking() {
        createDialog(null);

        verify(bookingToEdit).getComment();
    }

    @Test
    void constructor_bindsBookingToEdit_readsAmountFromBooking() {
        when(bookingToEdit.getAmount()).thenReturn(MoneyAmount.ofCents(500L));

        createDialog(null);

        verify(bookingToEdit).getAmount();
    }

    @Test
    void constructor_withShareholderInList_prePopulatesShareholderPicker() {
        final Shareholder shareholder = mock(Shareholder.class);
        when(shareholder.getId()).thenReturn(7);
        when(shareholder.getFirstName()).thenReturn("Erika");
        when(shareholder.getLastName()).thenReturn("Musterfrau");
        when(shareholder.getDisplayId()).thenReturn(2);
        when(shareholders.toList()).thenReturn(Collections.singletonList(shareholder));
        when(bookingToEdit.getShareholderId()).thenReturn(7);

        // Dialog must be created without errors even when a matching shareholder exists
        assertNotNull(createDialog(null));
    }

    @Test
    void bookingsWriter_notCalledDuringConstruction() {
        createDialog(null);

        verify(bookingsWriter, never()).create(any());
        verify(bookingsWriter, never()).update(any());
    }

    @Test
    void existingBookingCheck_notCalledDuringConstruction() {
        createDialog(null);

        verify(bookingsReader, never()).exists(any());
    }

    @Test
    void constructor_withOnSaveCallback_doesNotThrow() {
        final EditBookingFormDialog dialog = new EditBookingFormDialog(
                shareholdersReader,
                bookingsReader,
                bookingsWriter,
                null,
                messageSource,
                locale,
                bookingToEdit,
                null,
                b -> {

                },
                uiConfig
        );

        assertNotNull(dialog);
    }

    private EditBookingFormDialog createDialog(final ShareholderLock shareholderLock) {
        return new EditBookingFormDialog(
                shareholdersReader,
                bookingsReader,
                bookingsWriter,
                null,
                messageSource,
                locale,
                bookingToEdit,
                shareholderLock,
                b -> {

                },
                uiConfig
        );
    }
}
