package de.nihas101.midas.core.accountstatement.service;

import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.accountstatement.RowKey;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOrderEntity;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOrdersRepository;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class AccountStatementSortTest {

    public static final Random RANDOM = new Random();
    @Mock
    private AccountStatementOrdersRepository accountStatementOrdersRepository;

    @InjectMocks
    private AccountStatementSort sort;

    @Test
    void sortEmpty() {
        Mockito.when(accountStatementOrdersRepository.findByShareholderIdAndYearOrderByPositionAsc(1, 2026))
                .thenReturn(List.of());

        final List<LabeledAccountStatement> allStatements = sort.sort(
                List.of(),
                new DefaultShareholder(1, null, null, null),
                Year.of(2026)
        );

        Assertions.assertEquals(List.of(), allStatements);
    }

    @Test
    void sortOnlyBookingTypes() {
        Mockito.when(accountStatementOrdersRepository.findByShareholderIdAndYearOrderByPositionAsc(1, 2026))
                .thenReturn(List.of());

        final List<LabeledAccountStatement> allStatements = sort.sort(
                List.of(
                        new TestLabeledAccountStatement(BookingType.WITHDRAWAL),
                        new TestLabeledAccountStatement(BookingType.INTEREST),
                        new TestLabeledAccountStatement(BookingType.COMPENSATION),
                        new TestLabeledAccountStatement(BookingType.TAX_CREDIT),
                        new TestLabeledAccountStatement(BookingType.TAX_PREVIOUS_YEAR)
                ),
                new DefaultShareholder(1, null, null, null),
                Year.of(2026)
        );

        Assertions.assertEquals(
                List.of(
                        new TestLabeledAccountStatement(BookingType.WITHDRAWAL),
                        new TestLabeledAccountStatement(BookingType.TAX_PREVIOUS_YEAR),
                        new TestLabeledAccountStatement(BookingType.TAX_CREDIT),
                        new TestLabeledAccountStatement(BookingType.COMPENSATION),
                        new TestLabeledAccountStatement(BookingType.INTEREST)
                ),
                allStatements
        );
    }

    @Test
    void sortBookingTypesReordered() {
        final Shareholder shareholder = new DefaultShareholder(1, null, null, null);
        Mockito.when(accountStatementOrdersRepository.findByShareholderIdAndYearOrderByPositionAsc(shareholder.getId(), 2026))
                .thenReturn(
                        List.of(
                                getAccountStatementOrder(shareholder, BookingType.INTEREST, 0),
                                getAccountStatementOrder(shareholder, BookingType.COMPENSATION, 1),
                                getAccountStatementOrder(shareholder, BookingType.TAX_CREDIT, 2),
                                getAccountStatementOrder(shareholder, BookingType.TAX_PREVIOUS_YEAR, 3),
                                getAccountStatementOrder(shareholder, BookingType.WITHDRAWAL, 4)
                        )
                );

        final List<LabeledAccountStatement> allStatements = sort.sort(
                List.of(
                        new TestLabeledAccountStatement(BookingType.WITHDRAWAL),
                        new TestLabeledAccountStatement(BookingType.INTEREST),
                        new TestLabeledAccountStatement(BookingType.COMPENSATION),
                        new TestLabeledAccountStatement(BookingType.TAX_CREDIT),
                        new TestLabeledAccountStatement(BookingType.TAX_PREVIOUS_YEAR)
                ),
                shareholder,
                Year.of(2026)
        );

        Assertions.assertEquals(
                List.of(
                        new TestLabeledAccountStatement(BookingType.INTEREST),
                        new TestLabeledAccountStatement(BookingType.COMPENSATION),
                        new TestLabeledAccountStatement(BookingType.TAX_CREDIT),
                        new TestLabeledAccountStatement(BookingType.TAX_PREVIOUS_YEAR),
                        new TestLabeledAccountStatement(BookingType.WITHDRAWAL)
                ),
                allStatements
        );
    }

    @Test
    void sortBookingTypesAndManualReordered() {
        final Shareholder shareholder = new DefaultShareholder(1, null, null, null);
        Mockito.when(accountStatementOrdersRepository.findByShareholderIdAndYearOrderByPositionAsc(shareholder.getId(), 2026))
                .thenReturn(
                        List.of(
                                getAccountStatementOrder(shareholder, 1, 0),
                                getAccountStatementOrder(shareholder, BookingType.INTEREST, 1),
                                getAccountStatementOrder(shareholder, BookingType.COMPENSATION, 2),
                                getAccountStatementOrder(shareholder, BookingType.TAX_CREDIT, 3),
                                getAccountStatementOrder(shareholder, BookingType.TAX_PREVIOUS_YEAR, 4),
                                getAccountStatementOrder(shareholder, BookingType.WITHDRAWAL, 5),
                                getAccountStatementOrder(shareholder, 2, 6)
                        )
                );

        final List<LabeledAccountStatement> allStatements = sort.sort(
                List.of(
                        new TestLabeledAccountStatement(BookingType.WITHDRAWAL),
                        new TestLabeledAccountStatement(BookingType.INTEREST),
                        new TestLabeledAccountStatement(BookingType.COMPENSATION),
                        new TestLabeledAccountStatement(BookingType.TAX_CREDIT),
                        new TestLabeledAccountStatement(BookingType.TAX_PREVIOUS_YEAR),
                        new TestLabeledAccountStatement(1),
                        new TestLabeledAccountStatement(2)
                ),
                shareholder,
                Year.of(2026)
        );

        Assertions.assertEquals(
                List.of(
                        new TestLabeledAccountStatement(1),
                        new TestLabeledAccountStatement(BookingType.INTEREST),
                        new TestLabeledAccountStatement(BookingType.COMPENSATION),
                        new TestLabeledAccountStatement(BookingType.TAX_CREDIT),
                        new TestLabeledAccountStatement(BookingType.TAX_PREVIOUS_YEAR),
                        new TestLabeledAccountStatement(BookingType.WITHDRAWAL),
                        new TestLabeledAccountStatement(2)
                ),
                allStatements
        );
    }

    @Test
    void sortDoesntBreakWithNegativeAndHugeNumbers() {
        final Shareholder shareholder = new DefaultShareholder(1, null, null, null);
        Mockito.when(accountStatementOrdersRepository.findByShareholderIdAndYearOrderByPositionAsc(shareholder.getId(), 2026))
                .thenReturn(
                        List.of(
                                getAccountStatementOrder(shareholder, 1, -100),
                                getAccountStatementOrder(shareholder, BookingType.INTEREST, 1),
                                getAccountStatementOrder(shareholder, BookingType.COMPENSATION, 2),
                                getAccountStatementOrder(shareholder, BookingType.TAX_CREDIT, 3),
                                getAccountStatementOrder(shareholder, BookingType.TAX_PREVIOUS_YEAR, 4),
                                getAccountStatementOrder(shareholder, BookingType.WITHDRAWAL, 5),
                                getAccountStatementOrder(shareholder, 2, Integer.MAX_VALUE)
                        )
                );

        final List<LabeledAccountStatement> allStatements = sort.sort(
                List.of(
                        new TestLabeledAccountStatement(BookingType.WITHDRAWAL),
                        new TestLabeledAccountStatement(BookingType.INTEREST),
                        new TestLabeledAccountStatement(BookingType.COMPENSATION),
                        new TestLabeledAccountStatement(BookingType.TAX_CREDIT),
                        new TestLabeledAccountStatement(BookingType.TAX_PREVIOUS_YEAR),
                        new TestLabeledAccountStatement(1),
                        new TestLabeledAccountStatement(2)
                ),
                shareholder,
                Year.of(2026)
        );

        Assertions.assertEquals(
                List.of(
                        new TestLabeledAccountStatement(1),
                        new TestLabeledAccountStatement(BookingType.INTEREST),
                        new TestLabeledAccountStatement(BookingType.COMPENSATION),
                        new TestLabeledAccountStatement(BookingType.TAX_CREDIT),
                        new TestLabeledAccountStatement(BookingType.TAX_PREVIOUS_YEAR),
                        new TestLabeledAccountStatement(BookingType.WITHDRAWAL),
                        new TestLabeledAccountStatement(2)
                ),
                allStatements
        );
    }

    @Test
    void sortIsStableForSamePositions() {
        final Shareholder shareholder = new DefaultShareholder(1, null, null, null);
        Mockito.when(accountStatementOrdersRepository.findByShareholderIdAndYearOrderByPositionAsc(shareholder.getId(), 2026))
                .thenReturn(
                        List.of(
                                getAccountStatementOrder(shareholder, 1, 5),
                                getAccountStatementOrder(shareholder, BookingType.INTEREST, 5),
                                getAccountStatementOrder(shareholder, BookingType.COMPENSATION, 5),
                                getAccountStatementOrder(shareholder, BookingType.TAX_CREDIT, 5),
                                getAccountStatementOrder(shareholder, BookingType.TAX_PREVIOUS_YEAR, 5),
                                getAccountStatementOrder(shareholder, BookingType.WITHDRAWAL, 5),
                                getAccountStatementOrder(shareholder, 2, 5)
                        )
                );

        final List<LabeledAccountStatement> allStatements = sort.sort(
                List.of(
                        new TestLabeledAccountStatement(BookingType.WITHDRAWAL),
                        new TestLabeledAccountStatement(BookingType.INTEREST),
                        new TestLabeledAccountStatement(BookingType.COMPENSATION),
                        new TestLabeledAccountStatement(BookingType.TAX_CREDIT),
                        new TestLabeledAccountStatement(BookingType.TAX_PREVIOUS_YEAR),
                        new TestLabeledAccountStatement(1),
                        new TestLabeledAccountStatement(2)
                ),
                shareholder,
                Year.of(2026)
        );

        Assertions.assertEquals(
                List.of(
                        new TestLabeledAccountStatement(BookingType.WITHDRAWAL),
                        new TestLabeledAccountStatement(BookingType.TAX_PREVIOUS_YEAR),
                        new TestLabeledAccountStatement(BookingType.TAX_CREDIT),
                        new TestLabeledAccountStatement(BookingType.COMPENSATION),
                        new TestLabeledAccountStatement(BookingType.INTEREST),
                        new TestLabeledAccountStatement(1),
                        new TestLabeledAccountStatement(2)
                ),
                allStatements
        );
    }

    private AccountStatementOrderEntity getAccountStatementOrder(
            final Shareholder shareholder,
            final BookingType interest,
            final int position
    ) {
        return new AccountStatementOrderEntity(
                RANDOM.nextInt(),
                DefaultShareholder.fromDto(shareholder),
                2026,
                new RowKey(interest, null).toString(),
                position
        );
    }

    private AccountStatementOrderEntity getAccountStatementOrder(
            final Shareholder shareholder,
            final Integer id,
            final int position
    ) {
        return new AccountStatementOrderEntity(
                RANDOM.nextInt(),
                DefaultShareholder.fromDto(shareholder),
                2026,
                new RowKey(null, id).toString(),
                position
        );
    }

    @EqualsAndHashCode
    private static class TestLabeledAccountStatement implements LabeledAccountStatement {

        private Integer id;
        private BookingType bookingType;

        public TestLabeledAccountStatement(BookingType bookingType) {
            this.bookingType = bookingType;
        }

        public TestLabeledAccountStatement(int id) {
            this.id = id;
        }

        @Override
        public BookingType bookingType() {
            return bookingType;
        }

        @Override
        public Integer id() {
            return id;
        }

        @Override
        public String label() {
            return "";
        }

        @Override
        public boolean isManualExtra() {
            return false;
        }

        @Override
        public boolean isHidden() {
            return false;
        }

        @Override
        public LocalDate date() {
            return LocalDate.of(2026, Month.JANUARY, 1);
        }

        @Override
        public MoneyAmount amount() {
            return MoneyAmount.ofCents(100L);
        }

        @Override
        public String toString() {
            if (bookingType != null) {
                return bookingType.toString();
            } else {
                return String.valueOf(id);
            }
        }
    }
}