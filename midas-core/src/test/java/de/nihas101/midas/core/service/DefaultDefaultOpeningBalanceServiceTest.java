package de.nihas101.midas.core.service;

import de.nihas101.midas.commons.Source;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.core.openingbalance.service.DefaultOpeningBalanceService;
import de.nihas101.midas.persistance.openingbalance.OpeningBalanceEntity;
import de.nihas101.midas.persistance.openingbalance.OpeningBalanceRepository;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import de.nihas101.midas.persistance.shareholders.ShareholdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultDefaultOpeningBalanceServiceTest {

    @Mock
    private OpeningBalanceRepository openingBalanceRepository;

    @Mock
    private ShareholdersRepository shareholdersRepository;

    @InjectMocks
    private DefaultOpeningBalanceService openingBalanceService;

    private ShareholderEntity shareholder;

    @BeforeEach
    void setUp() {
        shareholder = new ShareholderEntity();
        shareholder.setId(1);
    }

    @Test
    void openingBalance_shareholderNotFound() {
        when(shareholdersRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                openingBalanceService.openingBalance(1, Year.of(2026))
        );
    }

    @Test
    void openingBalance_found() {
        final OpeningBalanceEntity entity = new OpeningBalanceEntity();
        entity.setId(10);
        entity.setShareholder(shareholder);
        entity.setDate(LocalDate.of(2026, 1, 1));
        entity.setAmount(MoneyAmount.of(new BigDecimal("100.00")));
        entity.setSource(Source.USER);

        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));
        when(openingBalanceRepository.findByShareholderAndDate(shareholder, LocalDate.of(2026, 1, 1)))
                .thenReturn(Optional.of(entity));

        final OpeningBalance result = openingBalanceService.openingBalance(1, Year.of(2026));

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals(1, result.getShareholderId());
        assertEquals(0, new BigDecimal("100.00").compareTo(result.getOpeningBalance().toBigDecimalForInput()));
        assertEquals(Year.of(2026), result.getYear());
        assertEquals(Source.USER, result.getSource());
    }

    @Test
    void openingBalance_notFound() {
        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));
        when(openingBalanceRepository.findByShareholderAndDate(shareholder, LocalDate.of(2026, 1, 1)))
                .thenReturn(Optional.empty());

        final OpeningBalance result = openingBalanceService.openingBalance(1, Year.of(2026));

        assertNull(result);
    }

    @Test
    void create_withIdThrowsException() {
        final OpeningBalance dto = DefaultOpeningBalance.builder().id(5).build();

        assertThrows(IllegalArgumentException.class, () -> openingBalanceService.create(dto));
        verify(openingBalanceRepository, never()).save(any());
    }

    @Test
    void create_success() {
        final OpeningBalance dto = DefaultOpeningBalance.builder()
                .shareholderId(1)
                .openingBalance(MoneyAmount.of(new BigDecimal("150.00")))
                .year(Year.of(2026))
                .source(Source.SYSTEM)
                .build();

        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));

        openingBalanceService.create(dto);

        final ArgumentCaptor<OpeningBalanceEntity> captor = ArgumentCaptor.forClass(OpeningBalanceEntity.class);
        verify(openingBalanceRepository).save(captor.capture());
        final OpeningBalanceEntity saved = captor.getValue();

        assertNull(saved.getId());
        assertEquals(shareholder, saved.getShareholder());
        assertEquals(LocalDate.of(2026, 1, 1), saved.getDate());
        assertEquals(0, new BigDecimal("150.00").compareTo(saved.getAmount().toBigDecimalForInput()));
        assertEquals(Source.SYSTEM, saved.getSource());
    }

    @Test
    void update_withoutIdThrowsException() {
        final OpeningBalance dto = DefaultOpeningBalance.builder().id(null).build();

        assertThrows(IllegalArgumentException.class, () -> openingBalanceService.update(dto));
        verify(openingBalanceRepository, never()).save(any());
    }

    @Test
    void update_success() {
        final OpeningBalance dto = DefaultOpeningBalance.builder()
                .id(10)
                .shareholderId(1)
                .openingBalance(MoneyAmount.of(new BigDecimal("200.00")))
                .year(Year.of(2026))
                .source(Source.USER)
                .build();

        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));

        openingBalanceService.update(dto);

        final ArgumentCaptor<OpeningBalanceEntity> captor = ArgumentCaptor.forClass(OpeningBalanceEntity.class);
        verify(openingBalanceRepository).save(captor.capture());
        final OpeningBalanceEntity saved = captor.getValue();

        assertEquals(10, saved.getId());
        assertEquals(shareholder, saved.getShareholder());
        assertEquals(LocalDate.of(2026, 1, 1), saved.getDate());
        assertEquals(0, new BigDecimal("200.00").compareTo(saved.getAmount().toBigDecimalForInput()));
        assertEquals(Source.USER, saved.getSource());
    }
}
