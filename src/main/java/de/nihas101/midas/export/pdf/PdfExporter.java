package de.nihas101.midas.export.pdf;

import de.nihas101.midas.accountstatement.row.AccountStatementRowService;
import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.row.BookingRow;
import de.nihas101.midas.bookings.row.BookingRowService;
import de.nihas101.midas.export.Export;
import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.interest.InterestCalculation;
import de.nihas101.midas.interest.dto.InterestRate;
import de.nihas101.midas.interest.row.InterestRowService;
import de.nihas101.midas.interest.service.InterestBookingsReader;
import de.nihas101.midas.interest.service.InterestRateService;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
public class PdfExporter implements Export {

    private final ExportRequest request;
    private final OutputStream outputStream;
    private final Locale locale;
    private final PdfService pdfService;

    private final InterestBookingsReader bookingsReader;
    private final InterestRateService interestRateService;
    private final AccountStatementService accountStatementService;
    private final MessageSource messageSource;
    private final BookingRowService bookingRowService;
    private final AccountStatementRowService accountStatementRowService;
    private final InterestRowService interestRowService;

    @Override
    public void trigger() {
        final int totalFiles = request.shareholders().size() * request.views().size();

        if (totalFiles == 0) {
            return;
        }

        if (totalFiles == 1) {
            generateSinglePdf();
        } else {
            generateZipOfPdfs();
        }
    }

    private void generateSinglePdf() {
        final Shareholder sh = request.shareholders().getFirst();
        final String view = request.views().iterator().next();
        final PdfViewData data = extractData(sh, view);
        pdfService.generatePdf(data, locale, outputStream);
    }

    private void generateZipOfPdfs() {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (Shareholder sh : request.shareholders()) {
                for (String view : request.views()) {
                    final PdfViewData data = extractData(sh, view);
                    final String filename = String.format("%s_%s_%s.pdf",
                            view,
                            (sh.getFirstName() + "_" + sh.getLastName()).replace(" ", "_"),
                            request.startDate().toString());

                    zos.putNextEntry(new ZipEntry(filename));

                    // We need a temporary buffer because PdfService writes to the stream 
                    // and we don't want it to close the ZipOutputStream prematurely 
                    // (though OpenHTMLToPDF shouldn't close it, it's safer this way 
                    // if we wanted to be absolutely sure, but ZipOutputStream expects entries)
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    pdfService.generatePdf(data, locale, baos);
                    zos.write(baos.toByteArray());

                    zos.closeEntry();
                }
            }
        } catch (IOException e) {
            log.error("Failed to generate ZIP of PDFs", e);
            throw new RuntimeException("Export failed", e);
        }
    }

    private PdfViewData extractData(Shareholder shareholder, String view) {
        if ("bookings".equals(view)) {
            return extractBookingsData(shareholder);
        }
        if ("account-statements".equals(view)) {
            return extractAccountStatementsData(shareholder);
        }
        if ("interest".equals(view)) {
            return extractInterestData(shareholder);
        }

        final Integer year = request.startDate().getYear();
        return new PdfViewData(view, shareholder.getFirstName() + " " + shareholder.getLastName(), shareholder, year, null, List.of(), List.of());
    }

    private PdfViewData extractBookingsData(Shareholder shareholder) {
        List<String> headers = List.of(
                messageSource.getMessage("bookings.table.id", null, locale),
                messageSource.getMessage("bookings.table.date", null, locale),
                messageSource.getMessage("bookings.table.comment", null, locale),
                messageSource.getMessage("bookings.table.total", null, locale),
                messageSource.getMessage("bookings.type.withdrawal", null, locale),
                messageSource.getMessage("bookings.type.tax-previous-year", null, locale),
                messageSource.getMessage("bookings.type.tax-credit", null, locale),
                messageSource.getMessage("bookings.type.interest", null, locale),
                messageSource.getMessage("bookings.type.compensation", null, locale),
                messageSource.getMessage("bookings.table.balance", null, locale)
        );

        final Year year = Year.of(request.startDate().getYear());
        final Bookings bookings = bookingsReader.interestRelatedBookingsForShareholderAndYear(shareholder.getId(), year);
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

    private PdfViewData extractAccountStatementsData(Shareholder shareholder) {
        List<String> headers = List.of(
                messageSource.getMessage("account-statements.table.id", null, locale),
                messageSource.getMessage("account-statements.table.date", null, locale),
                messageSource.getMessage("account-statements.table.type", null, locale),
                messageSource.getMessage("account-statements.table.debit", null, locale),
                messageSource.getMessage("account-statements.table.credit", null, locale),
                messageSource.getMessage("account-statements.table.balance", null, locale)
        );

        final Year year = Year.of(request.startDate().getYear());
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

    private PdfViewData extractInterestData(Shareholder shareholder) {
        List<String> headers = List.of(
                messageSource.getMessage("interest.table.month", null, locale),
                messageSource.getMessage("interest.table.transactions", null, locale),
                messageSource.getMessage("interest.table.sh", null, locale),
                messageSource.getMessage("interest.table.balance", null, locale),
                messageSource.getMessage("interest.table.sh", null, locale),
                messageSource.getMessage("interest.table.days", null, locale),
                messageSource.getMessage("interest.table.interest-amount", null, locale)
        );

        final Year year = Year.of(request.startDate().getYear());
        final InterestRate rate = interestRateService.interestRate(shareholder.getId(), year);
        final BigDecimal interestRate = rate != null ? rate.getInterestRate() : BigDecimal.ZERO;

        final Bookings bookings = bookingsReader.interestRelatedBookingsForShareholderAndYear(shareholder.getId(), year);
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
