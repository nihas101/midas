package de.nihas101.midas.export.pdf;

import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.export.Export;
import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.export.accountstatement.AccountStatementExportDataSource;
import de.nihas101.midas.export.accountstatement.AccountStatementsRowExtractor;
import de.nihas101.midas.export.bookings.BookingsExportDataSource;
import de.nihas101.midas.export.bookings.BookingsRowExtractor;
import de.nihas101.midas.export.interest.InterestExportDataSource;
import de.nihas101.midas.export.interest.InterestRowExtractor;
import de.nihas101.midas.interest.service.InterestRateService;
import de.nihas101.midas.openingbalance.service.DefaultOpeningBalanceService;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    // Services needed for data extraction
    private final BookingsReader bookingsReader;
    private final DefaultOpeningBalanceService openingBalanceService;
    private final InterestRateService interestRateService;
    private final AccountStatementService accountStatementService;
    private final MessageSource messageSource;

    @Override
    public void trigger() {
        int totalFiles = request.shareholders().size() * request.views().size();

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
        Shareholder sh = request.shareholders().getFirst();
        String view = request.views().iterator().next();
        PdfViewData data = extractData(sh, view);
        pdfService.generatePdf(data, locale, outputStream);
    }

    private void generateZipOfPdfs() {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (Shareholder sh : request.shareholders()) {
                for (String view : request.views()) {
                    PdfViewData data = extractData(sh, view);
                    String filename = String.format("%s_%s_%s.pdf",
                            view,
                            (sh.getFirstName() + "_" + sh.getLastName()).replace(" ", "_"),
                            request.startDate().toString());

                    zos.putNextEntry(new ZipEntry(filename));
                    
                    // We need a temporary buffer because PdfService writes to the stream 
                    // and we don't want it to close the ZipOutputStream prematurely 
                    // (though OpenHTMLToPDF shouldn't close it, it's safer this way 
                    // if we wanted to be absolutely sure, but ZipOutputStream expects entries)
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
        PdfExportTarget target = new PdfExportTarget();
        List<Shareholder> singleShList = List.of(shareholder);

        if ("bookings".equals(view)) {
            new BookingsExportDataSource(
                    new BookingsRowExtractor(
                            singleShList,
                            request.startDate(),
                            request.endDate(),
                            bookingsReader,
                            openingBalanceService,
                            messageSource,
                            locale
                    ),
                    messageSource,
                    locale
            ).export(target);
        } else if ("interest".equals(view)) {
            new InterestExportDataSource(
                    new InterestRowExtractor(
                            singleShList,
                            request.startDate(),
                            request.endDate(),
                            bookingsReader,
                            interestRateService
                    ),
                    messageSource,
                    locale
            ).export(target);
        } else if ("account-statements".equals(view)) {
            new AccountStatementExportDataSource(
                    new AccountStatementsRowExtractor(
                            singleShList,
                            request.startDate(),
                            request.endDate(),
                            accountStatementService,
                            messageSource,
                            locale
                    ),
                    messageSource,
                    locale
            ).export(target);
        }

        if (target.getViews().isEmpty()) {
            // Should not happen if data exists, but return empty data to avoid NPE
            return new PdfViewData(view, shareholder.getFirstName() + " " + shareholder.getLastName(), List.of(), List.of());
        }

        PdfExportTarget.ViewData viewData = target.getViews().getFirst();
        return new PdfViewData(
                view,
                shareholder.getFirstName() + " " + shareholder.getLastName(),
                viewData.headers(),
                viewData.rows()
        );
    }
}
