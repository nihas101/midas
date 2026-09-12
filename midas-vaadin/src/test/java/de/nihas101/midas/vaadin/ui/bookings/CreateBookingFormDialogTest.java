package de.nihas101.midas.vaadin.ui.bookings;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingFactory;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.bookings.BookingsWriter;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.Shareholders;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.config.UIConfig;
import de.nihas101.midas.core.lock.ShareholderLock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.util.Locale;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateBookingFormDialogTest {

    @Mock
    private ShareholdersReader shareholdersReader;
    @Mock
    private BookingsReader bookingsReader;
    @Mock
    private BookingsWriter bookingsWriter;
    @Mock
    private BookingFactory bookingFactory;
    @Mock
    private MessageSource messageSource;
    @Mock
    private Shareholders shareholders;
    @Mock
    private Booking booking;

    private UIConfig uiConfig;
    private final Locale locale = Locale.ENGLISH;

    @BeforeEach
    void setUp() {
        MockVaadin.setup();

        when(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .thenAnswer(i -> i.getArgument(0));
        uiConfig = new UIConfig();

        when(shareholdersReader.shareholders()).thenReturn(shareholders);
        when(shareholders.toList()).thenReturn(emptyList());
        when(bookingFactory.create(any(LocalDate.class), any(Source.class))).thenReturn(booking);
        when(booking.getDate()).thenReturn(LocalDate.now());
    }

    @AfterEach
    void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    void constructor_withNullsCreatesDialog() {
        assertNotNull(createDialog(null, null));
    }

    @Test
    void constructor_withInitialShareholder_setsShareholderOnBooking() {
        final Shareholder shareholder = mock(Shareholder.class);
        when(shareholder.getId()).thenReturn(42);
        when(shareholder.getFirstName()).thenReturn("Max");
        when(shareholder.getLastName()).thenReturn("Mustermann");
        when(shareholder.getDisplayId()).thenReturn(1);
        when(shareholders.toList()).thenReturn(singletonList(shareholder));

        createDialog(shareholder, null);

        verify(booking).setShareholderId(42);
    }

    @Test
    void constructor_withNullInitialShareholder_doesNotSetShareholderOnBooking() {
        createDialog(null, null);

        verify(booking, never()).setShareholderId(any());
    }

    @Test
    void constructor_callsBookingFactory_withTodayAndUserSource() {
        createDialog(null, null);

        verify(bookingFactory).create(any(LocalDate.class), eq(Source.USER));
    }

    @Test
    void constructor_withOnSaveCallback_doesNotThrow() {
        final CreateBookingFormDialog dialog = new CreateBookingFormDialog(
                shareholdersReader,
                bookingsReader,
                bookingsWriter,
                null,
                messageSource,
                locale,
                null,
                null,
                b -> {
                },
                uiConfig,
                bookingFactory
        );

        assertNotNull(dialog);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void constructor_addAnotherCheckbox_reflectsUIConfigDefault(final boolean defaultState) {
        uiConfig.setDefaultAddAnotherCheckboxState(defaultState);

        assertNotNull(createDialog(null, null));
    }

    @Test
    void existingBookingCheck_notCalledDuringConstruction() {
        createDialog(null, null);

        verify(bookingsReader, never()).exists(any());
    }

    @Test
    void bookingsWriter_notCalledDuringConstruction() {
        createDialog(null, null);

        verify(bookingsWriter, never()).create(any());
        verify(bookingsWriter, never()).update(any());
    }

    @Test
    void constructor_withShareholderLock_doesNotThrow() {
        final ShareholderLock shareholderLock = mock(ShareholderLock.class);

        assertNotNull(createDialog(null, shareholderLock));
    }

    private CreateBookingFormDialog createDialog(
            final Shareholder initialShareholder,
            final ShareholderLock shareholderLock
    ) {
        return new CreateBookingFormDialog(
                shareholdersReader,
                bookingsReader,
                bookingsWriter,
                null,
                messageSource,
                locale,
                initialShareholder,
                shareholderLock,
                b -> {
                },
                uiConfig,
                bookingFactory
        );
    }
}
