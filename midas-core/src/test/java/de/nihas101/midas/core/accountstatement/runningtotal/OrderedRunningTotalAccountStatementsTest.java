package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.api.accountstatement.AccountStatementSort;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatement;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.accountstatement.dto.DefaultAccountStatement;
import de.nihas101.midas.core.accountstatement.dto.DefaultAccountStatements;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class OrderedRunningTotalAccountStatementsTest {

    @Test
    void runningTotalAccountStatementsNull() {
        final MessageSource messageSource = mock(MessageSource.class);
        final Locale locale = Locale.ENGLISH;
        final Year year = Year.of(2026);

        final OrderedRunningTotalAccountStatements statements = new OrderedRunningTotalAccountStatements(
                new DefaultShareholder(1, 1, "John", "Smith"),
                year,
                null,
                (allStatements, s, y) -> allStatements,
                new NextRunningDefaultStatementFactory(),
                messageSource,
                locale
        );

        assertFalse(statements.isEmpty());
        assertEquals(
                List.of(new OpeningRunningTotalAccountStatement(
                        null,
                        null,
                        year
                )),
                statements.runningTotalAccountStatements()
        );
    }

    @Test
    void runningTotalAccountStatementsEmpty() {
        final MessageSource messageSource = mock(MessageSource.class);
        final Locale locale = Locale.ENGLISH;
        final Year year = Year.of(2026);

        final OrderedRunningTotalAccountStatements statements = new OrderedRunningTotalAccountStatements(
                new DefaultShareholder(1, 1, "John", "Smith"),
                year,
                new DefaultAccountStatements(
                        Map.of(),
                        new DefaultOpeningBalance(),
                        bookingType -> null
                ),
                (allStatements, s, y) -> allStatements,
                new NextRunningDefaultStatementFactory(),
                messageSource,
                locale
        );

        assertFalse(statements.isEmpty());
        assertEquals(
                List.of(new OpeningRunningTotalAccountStatement(
                        new DefaultOpeningBalance(),
                        null,
                        year
                )),
                statements.runningTotalAccountStatements()
        );
    }

    @Test
    void runningTotalAccountStatementsZeroes() {
        final MessageSource messageSource = mock(MessageSource.class);
        final Locale locale = Locale.ENGLISH;
        final Year year = Year.of(2026);

        final OrderedRunningTotalAccountStatements statements = new OrderedRunningTotalAccountStatements(
                new DefaultShareholder(1, 1, "John", "Smith"),
                year,
                new DefaultAccountStatements(
                        Map.of(
                                BookingType.WITHDRAWAL, withdrawal(year, MoneyAmount.ZERO),
                                BookingType.TAX_PREVIOUS_YEAR, taxPreviousYear(year, MoneyAmount.ZERO),
                                BookingType.TAX_CREDIT, taxCredit(year, MoneyAmount.ZERO),
                                BookingType.INTEREST, interest(year, MoneyAmount.ZERO),
                                BookingType.COMPENSATION, compensation(year, MoneyAmount.ZERO)
                        ),
                        new DefaultOpeningBalance(1, 1, MoneyAmount.ZERO, year, Source.USER),
                        bookingType -> null
                ),
                (allStatements, s, y) -> allStatements,
                new NextRunningDefaultStatementFactory(),
                messageSource,
                locale
        );

        assertFalse(statements.isEmpty());
        assertEquals(
                List.of(
                        new OpeningRunningTotalAccountStatement(
                                new DefaultOpeningBalance(1, 1, MoneyAmount.ZERO, year, Source.USER),
                                null,
                                year
                        ),
                        new DefaultRunningTotalAccountStatement(
                                withdrawal(year, MoneyAmount.ZERO),
                                MoneyAmount.ZERO
                        ),
                        new DefaultRunningTotalAccountStatement(
                                taxPreviousYear(year, MoneyAmount.ZERO),
                                MoneyAmount.ZERO
                        ),
                        new DefaultRunningTotalAccountStatement(
                                taxCredit(year, MoneyAmount.ZERO),
                                MoneyAmount.ZERO
                        ),
                        new DefaultRunningTotalAccountStatement(
                                interest(year, MoneyAmount.ZERO),
                                MoneyAmount.ZERO
                        ),
                        new DefaultRunningTotalAccountStatement(
                                compensation(year, MoneyAmount.ZERO),
                                MoneyAmount.ZERO
                        )
                ),
                statements.runningTotalAccountStatements()
        );
    }

    @ParameterizedTest
    @MethodSource("runningTotalAccountStatementsOrderingValues")
    void runningTotalAccountStatementsOrdering(
            final DefaultAccountStatements accountStatements,
            final AccountStatementSort accountStatementSort,
            final List<RunningTotalAccountStatement> expected
    ) {
        final MessageSource messageSource = mock(MessageSource.class);
        final Locale locale = Locale.ENGLISH;
        final Year year = Year.of(2026);

        final OrderedRunningTotalAccountStatements statements = new OrderedRunningTotalAccountStatements(
                new DefaultShareholder(1, 1, "John", "Smith"),
                year,
                accountStatements,
                accountStatementSort,
                new NextRunningDefaultStatementFactory(),
                messageSource,
                locale
        );

        assertFalse(statements.isEmpty());
        assertEquals(expected, statements.runningTotalAccountStatements());
    }

    public static Stream<Arguments> runningTotalAccountStatementsOrderingValues() {
        final Year year = Year.of(2026);
        final DefaultAccountStatements accountStatements = new DefaultAccountStatements(
                Map.of(
                        BookingType.WITHDRAWAL, withdrawal(year, MoneyAmount.ofCents(25L)),
                        BookingType.TAX_PREVIOUS_YEAR, taxPreviousYear(year, MoneyAmount.ofCents(-50L))
                ),
                new DefaultOpeningBalance(1, 1, MoneyAmount.ofCents(100L), year, Source.USER),
                bookingType -> null
        );

        return Stream.of(
                Arguments.of(
                        accountStatements,
                        (AccountStatementSort) (statements, s, y) -> statements,
                        List.of(
                                new OpeningRunningTotalAccountStatement(
                                        new DefaultOpeningBalance(1, 1, MoneyAmount.ofCents(100L), year, Source.USER),
                                        null,
                                        year
                                ),
                                new DefaultRunningTotalAccountStatement(
                                        withdrawal(year, MoneyAmount.ofCents(25L)),
                                        MoneyAmount.ofCents(125L)
                                ),
                                new DefaultRunningTotalAccountStatement(
                                        taxPreviousYear(year, MoneyAmount.ofCents(-50L)),
                                        MoneyAmount.ofCents(75L)
                                )
                        )
                ),
                Arguments.of(
                        accountStatements,
                        (AccountStatementSort) (statements, s, y) -> statements.reversed(),
                        List.of(
                                new OpeningRunningTotalAccountStatement(
                                        new DefaultOpeningBalance(1, 1, MoneyAmount.ofCents(100L), year, Source.USER),
                                        null,
                                        year
                                ),
                                new DefaultRunningTotalAccountStatement(
                                        taxPreviousYear(year, MoneyAmount.ofCents(-50L)),
                                        MoneyAmount.ofCents(50L)
                                ),
                                new DefaultRunningTotalAccountStatement(
                                        withdrawal(year, MoneyAmount.ofCents(25L)),
                                        MoneyAmount.ofCents(75L)
                                )
                        )
                )
        );
    }

    private static DefaultAccountStatement withdrawal(final Year year, final MoneyAmount amount) {
        return defaultAccountStatement(year, BookingType.WITHDRAWAL, amount);
    }

    private static DefaultAccountStatement taxCredit(final Year year, final MoneyAmount amount) {
        return defaultAccountStatement(year, BookingType.TAX_CREDIT, amount);
    }

    private static DefaultAccountStatement interest(final Year year, final MoneyAmount amount) {
        return defaultAccountStatement(year, BookingType.INTEREST, amount);
    }

    private static DefaultAccountStatement taxPreviousYear(final Year year, final MoneyAmount amount) {
        return defaultAccountStatement(year, BookingType.TAX_PREVIOUS_YEAR, amount);
    }

    private static DefaultAccountStatement compensation(final Year year, final MoneyAmount amount) {
        return defaultAccountStatement(year, BookingType.COMPENSATION, amount);
    }

    private static DefaultAccountStatement defaultAccountStatement(
            final Year year,
            final BookingType compensation,
            final MoneyAmount amount
    ) {
        return new DefaultAccountStatement(
                1,
                year,
                compensation,
                amount,
                null,
                amount.equals(MoneyAmount.ZERO),
                amount.equals(MoneyAmount.ZERO) ? Source.SYSTEM : Source.USER
        );
    }
}