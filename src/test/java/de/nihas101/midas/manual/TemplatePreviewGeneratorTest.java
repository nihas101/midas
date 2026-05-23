package de.nihas101.midas.manual;

import de.nihas101.midas.accountstatement.row.AccountStatementRowService;
import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.row.BookingRow;
import de.nihas101.midas.bookings.row.BookingRowService;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.export.pdf.PdfViewData;
import de.nihas101.midas.interest.InterestCalculation;
import de.nihas101.midas.interest.dto.InterestRate;
import de.nihas101.midas.interest.row.InterestRowService;
import de.nihas101.midas.interest.service.InterestBookingsReader;
import de.nihas101.midas.interest.service.InterestRateService;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Disabled("For generating previews and debugging templates quickly")
@SpringBootTest
public class TemplatePreviewGeneratorTest {

    @Autowired
    private SpringTemplateEngine pdfTemplateEngine;

    @Autowired
    private ShareholdersService shareholdersService;

    @Autowired
    private BookingsReader bookingsReader;

    @Autowired
    private InterestBookingsReader interestBookingsReader;

    @Autowired
    private InterestRateService interestRateService;

    @Autowired
    private AccountStatementService accountStatementService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private BookingRowService bookingRowService;

    @Autowired
    private AccountStatementRowService accountStatementRowService;

    @Autowired
    private InterestRowService interestRowService;

    @Test
    public void generatePreviews() throws IOException {
        final List<Shareholder> shareholders = shareholdersService.shareholders().toList();
        if (shareholders.isEmpty()) {
            System.out.println("No shareholders found in midas.db. Please add some data first.");
            return;
        }

        final Path outputDir = Paths.get("target/template-previews");
        Files.createDirectories(outputDir);

        final Locale locale = Locale.GERMAN; // Or whatever you prefer
        final LocalDate startDate = LocalDate.of(2026, 1, 1); // Sample date

        for (Shareholder sh : shareholders) {
            System.out.println("Generating previews for: " + sh.getFirstName() + " " + sh.getLastName());
            generatePreview(sh, "bookings", startDate, locale, outputDir);
            generatePreview(sh, "account-statements", startDate, locale, outputDir);
            generatePreview(sh, "interest", startDate, locale, outputDir);
        }

        System.out.println("Previews generated in: " + outputDir.toAbsolutePath());
    }

    private void generatePreview(Shareholder sh, String view, LocalDate startDate, Locale locale, Path outputDir) throws IOException {
        final PdfViewData data = extractData(sh, view, startDate, locale);
        
        Context context = new Context(locale);
        context.setVariable("data", data);
        context.setVariable("content", data.viewName());

        String html = pdfTemplateEngine.process("base-layout", context);
        
        String filename = String.format("%s_%s_%s.html",
                view,
                (sh.getFirstName() + "_" + sh.getLastName()).replace(" ", "_"),
                startDate.getYear());
        
        Files.writeString(outputDir.resolve(filename), html);
    }

    private PdfViewData extractData(Shareholder shareholder, String view, LocalDate startDate, Locale locale) {
        if ("bookings".equals(view)) {
            return extractBookingsData(shareholder, startDate, locale);
        }
        if ("account-statements".equals(view)) {
            return extractAccountStatementsData(shareholder, startDate, locale);
        }
        if ("interest".equals(view)) {
            return extractInterestData(shareholder, startDate, locale);
        }
        return new PdfViewData(view, shareholder.getFirstName() + " " + shareholder.getLastName(), shareholder, startDate.getYear(), null, List.of(), List.of());
    }

    private PdfViewData extractBookingsData(Shareholder shareholder, LocalDate startDate, Locale locale) {
        List<String> headers = List.of(
                messageSource.getMessage("export.pdf.bookings.table.id", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.date", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.comment", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.total", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.withdrawal", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.tax-previous-year", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.tax-credit", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.interest", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.compensation", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.balance", null, locale)
        );

        final Year year = Year.of(startDate.getYear());
        final Bookings bookings = bookingsReader.bookingsForShareholderAndYear(shareholder.getId(), year);
        final List<BookingRow> rows = bookingRowService.generateRows(bookings, locale);

        return new PdfViewData(
                "bookings",
                shareholder.getFirstName() + " " + shareholder.getLastName(),
                shareholder,
                year.getValue(),
                null,
                headers,
                new ArrayList<>(rows)
        );
    }

    private PdfViewData extractAccountStatementsData(Shareholder shareholder, LocalDate startDate, Locale locale) {
        List<String> headers = List.of(
                messageSource.getMessage("account-statements.table.id", null, locale),
                messageSource.getMessage("account-statements.table.date", null, locale),
                messageSource.getMessage("account-statements.table.type", null, locale),
                messageSource.getMessage("account-statements.table.debit", null, locale),
                messageSource.getMessage("account-statements.table.credit", null, locale),
                messageSource.getMessage("account-statements.table.balance", null, locale)
        );

        final Year year = Year.of(startDate.getYear());
        final RunningTotalAccountStatements statements = accountStatementService.runningTotalAccountStatements(shareholder, year, messageSource, locale);

        final List<Object> rows = new ArrayList<>(accountStatementRowService.generateRows(statements));
        rows.add(accountStatementRowService.generateClosingRow(statements, locale));

        return new PdfViewData(
                "account-statements",
                shareholder.getFirstName() + " " + shareholder.getLastName(),
                shareholder,
                year.getValue(),
                null,
                headers,
                rows
        );
    }

    private PdfViewData extractInterestData(Shareholder shareholder, LocalDate startDate, Locale locale) {
        List<String> headers = List.of(
                messageSource.getMessage("interest.table.month", null, locale),
                messageSource.getMessage("interest.table.transactions", null, locale),
                messageSource.getMessage("interest.table.sh", null, locale),
                messageSource.getMessage("interest.table.balance", null, locale),
                messageSource.getMessage("interest.table.sh", null, locale),
                messageSource.getMessage("interest.table.days", null, locale),
                messageSource.getMessage("interest.table.interest-amount", null, locale)
        );

        final Year year = Year.of(startDate.getYear());
        final InterestRate rate = interestRateService.interestRate(shareholder.getId(), year);
        final BigDecimal interestRate = rate != null ? rate.getInterestRate() : BigDecimal.ZERO;

        final Bookings bookings = interestBookingsReader.interestRelatedBookingsForShareholderAndYear(shareholder.getId(), year);
        final InterestCalculation interestCalculation = new InterestCalculation(bookings, year, interestRate);

        final List<Object> rows = new ArrayList<>(interestRowService.generateRows(year, bookings, interestRate, interestCalculation, locale));

        return new PdfViewData(
                "interest",
                shareholder.getFirstName() + " " + shareholder.getLastName(),
                shareholder,
                year.getValue(),
                interestRate,
                headers,
                rows
        );
    }
}
