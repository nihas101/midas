package de.nihas101.midas.core.interest.service;

import de.nihas101.midas.api.interest.InterestRate;
import de.nihas101.midas.core.interest.dto.DefaultInterestRate;
import de.nihas101.midas.persistance.interest.InterestRateEntity;
import de.nihas101.midas.persistance.interest.InterestRateRepository;
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
class DefaultInterestRateServiceTest {

    @Mock
    private InterestRateRepository interestRateRepository;

    @Mock
    private ShareholdersRepository shareholdersRepository;

    @InjectMocks
    private DefaultInterestRateService interestRateService;

    private ShareholderEntity shareholder;

    @BeforeEach
    void setUp() {
        shareholder = new ShareholderEntity();
        shareholder.setId(1);
    }

    @Test
    void interestRate_found() {
        final InterestRateEntity entity = new InterestRateEntity();
        entity.setId(10);
        entity.setShareholder(shareholder);
        entity.setDate(LocalDate.of(2026, 1, 1));
        entity.setRate(250L);

        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));
        when(interestRateRepository.findByShareholderAndDate(shareholder, LocalDate.of(2026, 1, 1)))
                .thenReturn(Optional.of(entity));

        final InterestRate result = interestRateService.interestRate(1, Year.of(2026));

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals(1, result.getShareholderId());
        assertEquals(new BigDecimal("2.50"), result.getInterestRate());
        assertEquals(Year.of(2026), result.getYear());
    }

    @Test
    void interestRate_notFound() {
        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));
        when(interestRateRepository.findByShareholderAndDate(shareholder, LocalDate.of(2026, 1, 1)))
                .thenReturn(Optional.empty());

        final InterestRate result = interestRateService.interestRate(1, Year.of(2026));

        assertNull(result);
    }

    @Test
    void create_withIdThrowsException() {
        final InterestRate dto = DefaultInterestRate.builder().id(5).build();

        assertThrows(IllegalArgumentException.class, () -> interestRateService.create(dto));
        verify(interestRateRepository, never()).save(any());
    }

    @Test
    void create_alreadyExists_throwsIllegalStateException() {
        final InterestRateEntity existingEntity = new InterestRateEntity();
        existingEntity.setId(10);
        existingEntity.setShareholder(shareholder);
        existingEntity.setDate(LocalDate.of(2026, 1, 1));
        existingEntity.setRate(250L);

        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));
        when(interestRateRepository.findByShareholderAndDate(shareholder, LocalDate.of(2026, 1, 1)))
                .thenReturn(Optional.of(existingEntity));

        final InterestRate dto = DefaultInterestRate.builder()
                .shareholderId(1)
                .year(Year.of(2026))
                .interestRate(new BigDecimal("3.00"))
                .build();

        assertThrows(IllegalStateException.class, () -> interestRateService.create(dto));
        verify(interestRateRepository, never()).save(any());
    }

    @Test
    void create_success() {
        final InterestRate dto = DefaultInterestRate.builder()
                .shareholderId(1)
                .year(Year.of(2026))
                .interestRate(new BigDecimal("2.50"))
                .build();

        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));
        when(interestRateRepository.findByShareholderAndDate(shareholder, LocalDate.of(2026, 1, 1)))
                .thenReturn(Optional.empty());

        final InterestRateEntity savedEntity = new InterestRateEntity();
        savedEntity.setId(10);
        savedEntity.setShareholder(shareholder);
        savedEntity.setDate(LocalDate.of(2026, 1, 1));
        savedEntity.setRate(250L);
        when(interestRateRepository.save(any(InterestRateEntity.class))).thenReturn(savedEntity);

        final InterestRate created = interestRateService.create(dto);

        assertNotNull(created);
        assertEquals(10, created.getId());
        assertEquals(new BigDecimal("2.50"), created.getInterestRate());

        final ArgumentCaptor<InterestRateEntity> captor = ArgumentCaptor.forClass(InterestRateEntity.class);
        verify(interestRateRepository).save(captor.capture());
        final InterestRateEntity entity = captor.getValue();

        assertNull(entity.getId());
        assertEquals(shareholder, entity.getShareholder());
        assertEquals(LocalDate.of(2026, 1, 1), entity.getDate());
        assertEquals(250L, entity.getRate());
    }

    @Test
    void update_withoutIdThrowsException() {
        final InterestRate dto = DefaultInterestRate.builder().id(null).build();

        assertThrows(IllegalArgumentException.class, () -> interestRateService.update(dto));
        verify(interestRateRepository, never()).save(any());
    }

    @Test
    void update_success() {
        final InterestRate dto = DefaultInterestRate.builder()
                .id(10)
                .shareholderId(1)
                .year(Year.of(2026))
                .interestRate(new BigDecimal("3.50"))
                .build();

        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));

        final InterestRateEntity savedEntity = new InterestRateEntity();
        savedEntity.setId(10);
        savedEntity.setShareholder(shareholder);
        savedEntity.setDate(LocalDate.of(2026, 1, 1));
        savedEntity.setRate(350L);
        when(interestRateRepository.save(any(InterestRateEntity.class))).thenReturn(savedEntity);

        final InterestRate updated = interestRateService.update(dto);

        assertNotNull(updated);
        assertEquals(10, updated.getId());
        assertEquals(new BigDecimal("3.50"), updated.getInterestRate());

        final ArgumentCaptor<InterestRateEntity> captor = ArgumentCaptor.forClass(InterestRateEntity.class);
        verify(interestRateRepository).save(captor.capture());
        final InterestRateEntity entity = captor.getValue();

        assertEquals(10, entity.getId());
        assertEquals(shareholder, entity.getShareholder());
        assertEquals(LocalDate.of(2026, 1, 1), entity.getDate());
        assertEquals(350L, entity.getRate());
    }
}
