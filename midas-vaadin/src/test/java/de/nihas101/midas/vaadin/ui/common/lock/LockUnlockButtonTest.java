package de.nihas101.midas.vaadin.ui.common.lock;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.button.ButtonVariant;
import de.nihas101.midas.api.lock.LockWriter;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.vaadin.ui.common.ShareholderPicker;
import de.nihas101.midas.vaadin.ui.common.YearPicker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static com.github.mvysny.kaributesting.v10.ButtonKt._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.pro.ConfirmDialogKt._fireConfirm;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LockUnlockButtonTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private ShareholderLock shareholderLock;

    @Mock
    private ShareholderPicker shareholderPicker;

    @Mock
    private YearPicker yearPicker;

    @Mock
    private LockWriter lockWriter;

    private final Locale locale = Locale.GERMAN;
    private final Shareholder shareholder = new DefaultShareholder(1, 1, "Max", "Mustermann");
    private final Year year = Year.of(2026);

    @BeforeEach
    void setUp() {
        MockVaadin.setup();
    }

    @AfterEach
    void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    void constructor_initializesWithContrastVariant() {
        final LockUnlockButton button = createButton(() -> {
        });

        assertTrue(button.getThemeNames().contains(ButtonVariant.LUMO_CONTRAST.getVariantName()));
    }

    @Test
    void unlock_updatesTooltipIconAndThemeVariants() {
        when(messageSource.getMessage("bookings.lock-year", null, locale)).thenReturn("Lock year");

        final LockUnlockButton button = createButton(() -> {
        });
        button.addThemeVariants(ButtonVariant.LUMO_ERROR);

        button.unlock();

        assertEquals("Lock year", button.getTooltip().getText());
        assertTrue(button.getThemeNames().contains(ButtonVariant.LUMO_CONTRAST.getVariantName()));
        assertFalse(button.getThemeNames().contains(ButtonVariant.LUMO_ERROR.getVariantName()));
    }

    @Test
    void lock_updatesTooltipIconAndThemeVariants() {
        when(messageSource.getMessage("bookings.unlock-year", null, locale)).thenReturn("Unlock year");

        final LockUnlockButton button = createButton(() -> {
        });

        button.lock();

        assertEquals("Unlock year", button.getTooltip().getText());
        assertTrue(button.getThemeNames().contains(ButtonVariant.LUMO_ERROR.getVariantName()));
        assertFalse(button.getThemeNames().contains(ButtonVariant.LUMO_CONTRAST.getVariantName()));
    }

    @ParameterizedTest
    @MethodSource("nullSelectionProvider")
    void onLockUnlockClicked_doesNothing_whenShareholderOrYearIsNull(
            final Shareholder pickerShareholder,
            final Integer pickerYear
    ) {
        when(shareholderPicker.getValue()).thenReturn(pickerShareholder);
        if (pickerShareholder != null) {
            when(yearPicker.getValue()).thenReturn(pickerYear);
        }

        final LockUnlockButton button = createButton(() -> {
        });

        button.onLockUnlockClicked();

        verifyNoInteractions(shareholderLock);
        verifyNoInteractions(lockWriter);
    }

    private static Stream<Arguments> nullSelectionProvider() {
        return Stream.of(
                Arguments.of(null, 2026),
                Arguments.of(new DefaultShareholder(1, 1, "Max", "Mustermann"), null),
                Arguments.of(null, null)
        );
    }

    @Test
    void onLockUnlockClicked_whenCurrentlyLocked_opensUnlockDialogAndHandlesConfirmation() {
        when(shareholderPicker.getValue()).thenReturn(shareholder);
        when(yearPicker.getValue()).thenReturn(year.getValue());
        when(shareholderLock.isLocked(shareholder, year)).thenReturn(true);

        when(messageSource.getMessage(eq("bookings.unlock.confirmation.title"), any(), eq(locale)))
                .thenReturn("Unlock 2026");
        when(messageSource.getMessage(eq("bookings.unlock.confirmation.message"), any(), eq(locale)))
                .thenReturn("Unlock 2026 for Max Mustermann?");
        when(messageSource.getMessage("bookings.unlock-year", null, locale))
                .thenReturn("Unlock");
        when(messageSource.getMessage("global.cancel", null, locale))
                .thenReturn("Cancel");
        when(messageSource.getMessage("bookings.lock-year", null, locale))
                .thenReturn("Lock year");

        final AtomicBoolean updated = new AtomicBoolean(false);
        final LockUnlockButton button = createButton(() -> updated.set(true));

        button.onLockUnlockClicked();

        final UnlockDialog dialog = _get(UnlockDialog.class);
        assertNotNull(dialog);
        assertTrue(dialog.isOpened());
        verify(lockWriter, never()).unlock(shareholder, year);
        assertFalse(updated.get());

        // Simulate confirmation using Karibu ConfirmDialogKt helper
        _fireConfirm(dialog);

        verify(lockWriter).unlock(shareholder, year);
        assertTrue(updated.get());
        assertEquals("Lock year", button.getTooltip().getText());
    }

    @Test
    void onLockUnlockClicked_whenCurrentlyUnlocked_opensLockDialogAndHandlesConfirmation() {
        when(shareholderPicker.getValue()).thenReturn(shareholder);
        when(yearPicker.getValue()).thenReturn(year.getValue());
        when(shareholderLock.isLocked(shareholder, year)).thenReturn(false);

        when(messageSource.getMessage(eq("bookings.lock.confirmation.title"), any(), eq(locale)))
                .thenReturn("Lock 2026");
        when(messageSource.getMessage(eq("bookings.lock.confirmation.message"), any(), eq(locale)))
                .thenReturn("Lock 2026 for Max Mustermann?");
        when(messageSource.getMessage("bookings.lock-year", null, locale))
                .thenReturn("Lock");
        when(messageSource.getMessage("global.cancel", null, locale))
                .thenReturn("Cancel");
        when(messageSource.getMessage("bookings.unlock-year", null, locale))
                .thenReturn("Unlock year");

        final AtomicBoolean updated = new AtomicBoolean(false);
        final LockUnlockButton button = createButton(() -> updated.set(true));

        button.onLockUnlockClicked();

        final LockDialog dialog = _get(LockDialog.class);
        assertNotNull(dialog);
        assertTrue(dialog.isOpened());
        verify(lockWriter, never()).lock(shareholder, year);
        assertFalse(updated.get());

        // Simulate confirmation using Karibu ConfirmDialogKt helper
        _fireConfirm(dialog);

        verify(lockWriter).lock(shareholder, year);
        assertTrue(updated.get());
        assertEquals("Unlock year", button.getTooltip().getText());
    }

    @Test
    void clickListener_triggersOnLockUnlockClicked() {
        when(shareholderPicker.getValue()).thenReturn(null);

        final LockUnlockButton button = createButton(() -> {
        });
        _click(button);

        verify(shareholderPicker).getValue();
    }

    private LockUnlockButton createButton(final Runnable onUpdate) {
        return new LockUnlockButton(
                messageSource,
                locale,
                shareholderLock,
                shareholderPicker,
                yearPicker,
                lockWriter,
                onUpdate
        );
    }
}