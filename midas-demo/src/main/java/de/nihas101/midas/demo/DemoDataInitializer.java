package de.nihas101.midas.demo;

import de.nihas101.midas.api.accountstatement.AccountStatementService;
import de.nihas101.midas.api.backup.BackupStatusWriter;
import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingFactory;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesWriter;
import de.nihas101.midas.api.interest.InterestBookingsReader;
import de.nihas101.midas.api.interest.InterestCalculation;
import de.nihas101.midas.api.lock.LockWriter;
import de.nihas101.midas.api.openingbalance.OpeningBalanceFactory;
import de.nihas101.midas.api.openingbalance.OpeningBalanceService;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.ShareholderFactory;
import de.nihas101.midas.api.shareholder.ShareholdersWriter;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.commenttemplate.dto.DefaultCommentTemplate;
import de.nihas101.midas.core.config.CoreConfig;
import de.nihas101.midas.core.interest.DefaultInterestCalculation;
import de.nihas101.midas.core.interest.dto.InterestRate;
import de.nihas101.midas.core.interest.service.InterestRateService;
import de.nihas101.midas.core.interest.service.bookingupdate.LockingInterestUpdatingBookingsService;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoDataInitializer {

    public static final Map<Integer, Map<String, DemoShareholderName>> SHAREHOLDERS = Map.of(
            101, Map.of(
                    "de", new DemoShareholderName("Anna", "Schmidt"),
                    "en", new DemoShareholderName("Alice", "Johnson")
            ),
            102, Map.of(
                    "de", new DemoShareholderName("Bernd", "Weber"),
                    "en", new DemoShareholderName("Bob", "Williams")
            ),
            103, Map.of(
                    "de", new DemoShareholderName("Clara", "Fischer"),
                    "en", new DemoShareholderName("Clara", "Davis")
            )
    );

    public static final Year DEMO_YEAR = Year.of(2026);

    public static final Map<DemoBookingKey, Map<String, DemoBooking>> DEMO_BOOKINGS = Map.of(
            new DemoBookingKey(1, DEMO_YEAR.atMonth(1).atDay(15), BookingType.WITHDRAWAL),
            Map.of(
                    "de", new DemoBooking("Monatliche Entnahme Januar", new BigDecimal("-2500.00"), Source.USER),
                    "en", new DemoBooking("Monthly withdrawal January", new BigDecimal("-2500.00"), Source.USER)
            ),
            new DemoBookingKey(1, DEMO_YEAR.atMonth(2).atDay(20), BookingType.WITHDRAWAL),
            Map.of(
                    "de", new DemoBooking("Monatliche Entnahme Februar", new BigDecimal("-2500.00"), Source.USER),
                    "en", new DemoBooking("Monthly withdrawal February", new BigDecimal("-2500.00"), Source.USER)
            ),
            new DemoBookingKey(1, DEMO_YEAR.atMonth(3).atDay(10), BookingType.TAX_PREVIOUS_YEAR),
            Map.of(
                    "de", new DemoBooking("Kapitalertragsteuer Vorjahr", new BigDecimal("-1200.00"), Source.USER),
                    "en", new DemoBooking("Capital yield tax previous year", new BigDecimal("-1200.00"), Source.USER)
            ),
            new DemoBookingKey(1, DEMO_YEAR.atMonth(4).atDay(18), BookingType.WITHDRAWAL),
            Map.of(
                    "de", new DemoBooking("Monatliche Entnahme April", new BigDecimal("-2500.00"), Source.USER),
                    "en", new DemoBooking("Monthly withdrawal April", new BigDecimal("-2500.00"), Source.USER)
            ),
            new DemoBookingKey(1, DEMO_YEAR.atMonth(6).atDay(25), BookingType.TAX_CREDIT),
            Map.of(
                    "de", new DemoBooking("Körperschaftsteuererstattung", new BigDecimal("-3450.00"), Source.USER),
                    "en", new DemoBooking("Corporate tax credit refund", new BigDecimal("-3450.00"), Source.USER)
            ),
            new DemoBookingKey(1, DEMO_YEAR.atMonth(7).atDay(12), BookingType.WITHDRAWAL),
            Map.of(
                    "de", new DemoBooking("Sonderentnahme Sommer", new BigDecimal("-5000.00"), Source.USER),
                    "en", new DemoBooking("Special summer withdrawal", new BigDecimal("-5000.00"), Source.USER)
            ),
            new DemoBookingKey(1, DEMO_YEAR.atMonth(9).atDay(30), BookingType.COMPENSATION),
            Map.of(
                    "de", new DemoBooking("Tätigkeitsvergütung Q3", new BigDecimal("-6000.00"), Source.USER),
                    "en", new DemoBooking("Director compensation Q3", new BigDecimal("-6000.00"), Source.USER)
            ),
            new DemoBookingKey(1, DEMO_YEAR.atMonth(11).atDay(15), BookingType.WITHDRAWAL),
            Map.of(
                    "de", new DemoBooking("Monatliche Entnahme November", new BigDecimal("-2500.00"), Source.USER),
                    "en", new DemoBooking("Monthly withdrawal November", new BigDecimal("-2500.00"), Source.USER)
            ),
            new DemoBookingKey(2, DEMO_YEAR.minusYears(1).atMonth(6).atDay(1), BookingType.COMPENSATION),
            Map.of(
                    "de", new DemoBooking("Gewinnvorabvergütung", new BigDecimal("7500.00"), Source.USER),
                    "en", new DemoBooking("Advance profit compensation", new BigDecimal("7500.00"), Source.USER)
            )
    );

    public static final Map<String, BigDecimal> OVERRIDES = Map.of(
            "de", new BigDecimal("-1200.00"),
            "en", new BigDecimal("-1200.00")
    );

    public static final Map<String, String> MANUAL_EXTRAS = Map.of(
            "de", "Sonderkorrektur Abrechnung",
            "en", "Special statement adjustment"
    );

    private final MessageSource messageSource;
    private final JdbcTemplate jdbcTemplate;
    private final ShareholdersService shareholdersService;
    private final ShareholdersWriter shareholdersWriter;
    private final ShareholderFactory shareholderFactory;
    private final OpeningBalanceService openingBalanceService;
    private final OpeningBalanceFactory openingBalanceFactory;
    private final LockingInterestUpdatingBookingsService bookingsWriter;
    private final BookingFactory bookingFactory;
    private final InterestRateService interestRateService;
    private final InterestBookingsReader interestBookingsReader;
    private final AccountStatementService accountStatementService;
    private final LockWriter lockWriter;
    private final CommentTemplatesWriter commentTemplatesWriter;
    private final BackupStatusWriter backupStatusWriter;
    private final CoreConfig coreConfig;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationReady() {
        clearExistingData();
        log.info("Initializing demo data (language: {})...", coreConfig.getI18n().getDefaultLocale());

        seedDemoData();
        log.info("Demo data successfully initialized!");
    }

    private void clearExistingData() {
        log.info("Clearing existing data for fresh demo setup...");
        final List<String> tables = Arrays.asList(
                "account_statement_orders",
                "account_statement_overrides",
                "locked_years",
                "comment_template_booking_types",
                "comment_templates",
                "interest_rates",
                "bookings",
                "opening_balances",
                "shareholders"
        );
        for (String table : tables) {
            log.info("Clearing {}", table);
            jdbcTemplate.execute("DELETE FROM %s;".formatted(table));
            jdbcTemplate.execute("DELETE FROM sqlite_sequence WHERE name = '%s';".formatted(table));
        }
    }

    private void seedDemoData() {
        final Year previousYear = DEMO_YEAR.minusYears(1);

        // 1. Shareholders
        final Shareholder sh1 = createShareholder(101);
        shareholdersWriter.create(sh1);

        final Shareholder sh2 = createShareholder(102);
        shareholdersWriter.create(sh2);

        final Shareholder sh3 = createShareholder(103);
        shareholdersWriter.create(sh3);

        // Fetch refreshed shareholders with IDs
        final List<Shareholder> shareholders = shareholdersService.shareholders().toList();

        final Shareholder alice = shareholders.stream().filter(s -> Integer.valueOf(101).equals(s.getDisplayId())).findFirst().orElseThrow();
        final Integer aliceId = alice.getId();

        final Shareholder bob = shareholders.stream().filter(s -> Integer.valueOf(102).equals(s.getDisplayId())).findFirst().orElseThrow();
        final Integer bobId = bob.getId();

        // 2. Opening balances
        openingBalanceService.create(openingBalanceFactory.create(
                aliceId,
                MoneyAmount.of(new BigDecimal("50000.00")),
                DEMO_YEAR,
                Source.USER
        ));

        openingBalanceService.create(openingBalanceFactory.create(
                bobId,
                MoneyAmount.of(new BigDecimal("35000.00")),
                previousYear,
                Source.USER
        ));
        openingBalanceService.create(openingBalanceFactory.create(
                bobId,
                MoneyAmount.of(new BigDecimal("42500.00")),
                DEMO_YEAR,
                Source.SYSTEM
        ));

        // 3. Bookings for Alice (current year)
        createBooking(aliceId, DEMO_YEAR.atMonth(1).atDay(15), BookingType.WITHDRAWAL);
        createBooking(aliceId, DEMO_YEAR.atMonth(2).atDay(20), BookingType.WITHDRAWAL);
        createBooking(aliceId, DEMO_YEAR.atMonth(3).atDay(10), BookingType.TAX_PREVIOUS_YEAR);
        createBooking(aliceId, DEMO_YEAR.atMonth(4).atDay(18), BookingType.WITHDRAWAL);
        createBooking(aliceId, DEMO_YEAR.atMonth(6).atDay(25), BookingType.TAX_CREDIT);
        createBooking(aliceId, DEMO_YEAR.atMonth(7).atDay(12), BookingType.WITHDRAWAL);
        createBooking(aliceId, DEMO_YEAR.atMonth(9).atDay(30), BookingType.COMPENSATION);
        createBooking(aliceId, DEMO_YEAR.atMonth(11).atDay(15), BookingType.WITHDRAWAL);

        // 4. Interest Rate & System Interest Booking for Alice
        final BigDecimal ratePercent = new BigDecimal("3.50");
        interestRateService.create(new InterestRate(null, aliceId, ratePercent, DEMO_YEAR));

        final InterestCalculation calc = new DefaultInterestCalculation(
                interestBookingsReader.interestRelatedBookingsForShareholderAndYear(alice.getId(), DEMO_YEAR),
                DEMO_YEAR,
                ratePercent
        );

        createInterestBooking(
                alice.getId(),
                LocalDate.of(DEMO_YEAR.getValue(), 12, 31),
                calc.interest().toBigDecimalForInput()
        );

        // 5. Account Statement Overrides & Ordering for Alice
        // Exclude one entry from statement
        createOverride(alice);

        // Add manual extra adjustment entry
        addManualExtraAdjustmentEntry(alice);

        // 6. Bookings & Lock for Bob (Previous Year locked)
        createBooking(bobId, LocalDate.of(previousYear.getValue(), 6, 1), BookingType.COMPENSATION);
        lockWriter.lock(bob, previousYear);

        // 7. Comment Templates
        seedCommentTemplates();

        // 8. Backup Status
        backupStatusWriter.updateLastSuccessAt(LocalDateTime.now().minusDays(2).minusHours(3));
    }

    private void createOverride(final Shareholder alice) {
        final String locale = coreConfig.getI18n().getDefaultLocale();
        final BigDecimal amount = Optional.of(OVERRIDES.get(locale))
                .orElseThrow(() -> new RuntimeException("No overrides configured for " + locale));
        accountStatementService.saveOverride(
                alice,
                DEMO_YEAR,
                BookingType.TAX_PREVIOUS_YEAR,
                MoneyAmount.of(amount),
                true
        );
    }

    private void addManualExtraAdjustmentEntry(final Shareholder alice) {
        final String locale = coreConfig.getI18n().getDefaultLocale();
        final String comment = Optional.ofNullable(MANUAL_EXTRAS.get(locale))
                .orElseThrow(() -> new RuntimeException("No manual extra adjustment entry configured for " + locale));
        accountStatementService.saveManualExtra(
                null,
                alice,
                DEMO_YEAR,
                comment,
                MoneyAmount.of(new BigDecimal("1200.00"))
        );
    }

    private void createBooking(
            final Integer shareholderId,
            final LocalDate date,
            final BookingType type
    ) {
        final DemoBooking demoBooking = getDemoBooking(shareholderId, date, type);

        final Booking booking = bookingFactory.create(date, Source.USER);
        booking.setShareholderId(shareholderId);
        booking.setType(type);
        booking.setComment(demoBooking.comment());
        booking.setAmount(MoneyAmount.of(demoBooking.bigDecimal()));
        bookingsWriter.create(booking);
    }

    private DemoBooking getDemoBooking(final Integer shareholderId, final LocalDate date, final BookingType type) {
        final String locale = coreConfig.getI18n().getDefaultLocale();
        final DemoBookingKey key = new DemoBookingKey(shareholderId, date, type);
        return Optional.ofNullable(DEMO_BOOKINGS.get(key))
                .map(bc -> bc.get(locale))
                .orElseThrow(() -> new RuntimeException("No demo booking configured for (" + key + ", " + locale + ")"));
    }

    private void createInterestBooking(
            final Integer shareholderId,
            final LocalDate date,
            final BigDecimal amount
    ) {
        final String locale = coreConfig.getI18n().getDefaultLocale();
        final Booking booking = bookingFactory.create(date, Source.SYSTEM);
        booking.setShareholderId(shareholderId);
        booking.setType(BookingType.INTEREST);
        final String comment = messageSource.getMessage("bookings.type.interest", null, Locale.of(locale));
        booking.setComment(comment);
        booking.setAmount(MoneyAmount.of(amount));
        bookingsWriter.create(booking);
    }

    private Shareholder createShareholder(final Integer displayId) {
        final DemoShareholderName demoShareholder = getDemoShareholder(displayId);

        final Shareholder shareholder = shareholderFactory.create();
        shareholder.setDisplayId(displayId);
        shareholder.setFirstName(demoShareholder.firstName());
        shareholder.setLastName(demoShareholder.lastName());
        return shareholder;
    }

    private DemoShareholderName getDemoShareholder(final Integer displayId) {
        final String locale = coreConfig.getI18n().getDefaultLocale();
        return Optional.ofNullable(SHAREHOLDERS.get(displayId))
                .map(sc -> sc.get(locale))
                .orElseThrow(() -> new RuntimeException("No demo shareholder configured for (" + displayId + ", " + locale + ")"));
    }

    private void seedCommentTemplates() {
        final String locale = coreConfig.getI18n().getDefaultLocale();

        if (Objects.equals(locale, "de")) {
            commentTemplatesWriter.save(DefaultCommentTemplate.builder()
                    .text("Monatliche Entnahme")
                    .bookingTypes(Set.of(BookingType.WITHDRAWAL))
                    .build());
            commentTemplatesWriter.save(DefaultCommentTemplate.builder()
                    .text("Kapitalertragsteuer")
                    .bookingTypes(Set.of(BookingType.TAX_PREVIOUS_YEAR))
                    .build());
            commentTemplatesWriter.save(DefaultCommentTemplate.builder()
                    .text("Steuererstattung Finanzamt")
                    .bookingTypes(Set.of(BookingType.TAX_CREDIT))
                    .build());
            commentTemplatesWriter.save(DefaultCommentTemplate.builder()
                    .text("Tätigkeitsvergütung Geschäftsführung")
                    .bookingTypes(Set.of(BookingType.COMPENSATION))
                    .build());
            commentTemplatesWriter.save(DefaultCommentTemplate.builder()
                    .text("Reisekostenabrechnung")
                    .bookingTypes(Set.of())
                    .build());
        } else if (Objects.equals(locale, "en")) {
            commentTemplatesWriter.save(DefaultCommentTemplate.builder()
                    .text("Monthly withdrawal")
                    .bookingTypes(Set.of(BookingType.WITHDRAWAL))
                    .build());
            commentTemplatesWriter.save(DefaultCommentTemplate.builder()
                    .text("Capital yield tax")
                    .bookingTypes(Set.of(BookingType.TAX_PREVIOUS_YEAR))
                    .build());
            commentTemplatesWriter.save(DefaultCommentTemplate.builder()
                    .text("Tax authority refund")
                    .bookingTypes(Set.of(BookingType.TAX_CREDIT))
                    .build());
            commentTemplatesWriter.save(DefaultCommentTemplate.builder()
                    .text("Director compensation")
                    .bookingTypes(Set.of(BookingType.COMPENSATION))
                    .build());
            commentTemplatesWriter.save(DefaultCommentTemplate.builder()
                    .text("Travel expense reimbursement")
                    .bookingTypes(Set.of())
                    .build());
        } else {
            throw new RuntimeException("No comment templates configured for " + locale);
        }
    }
}
