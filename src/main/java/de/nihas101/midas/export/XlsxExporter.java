package de.nihas101.midas.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of Export for Excel (XLSX) format.
 * Writes data from multiple ExportDataSources into separate sheets of a single workbook.
 */
@Slf4j
@RequiredArgsConstructor
public class XlsxExporter implements Export {

    private final List<ExportDataSource> dataSources;
    private final OutputStream outputStream;
    private final MessageSource messageSource;
    private final Locale locale;

    @Override
    public void trigger() {
        try (Workbook workbook = new XSSFWorkbook()) {
            final CellStyle dateStyle = createDateStyle(workbook);
            final CellStyle headerStyle = createHeaderStyle(workbook);

            for (ExportDataSource dataSource : dataSources) {
                final Sheet sheet = workbook.createSheet(dataSource.getSheetName(messageSource, locale));
                writeHeader(sheet, dataSource.getHeaders(messageSource, locale), headerStyle);
                writeRows(sheet, dataSource.getRows(), dateStyle);
                autoSizeColumns(sheet, dataSource.getHeaders(messageSource, locale).size());
            }

            workbook.write(outputStream);
        } catch (IOException e) {
            log.error("Failed to generate XLSX export", e);
            throw new RuntimeException("Export failed", e);
        }
    }

    private void writeHeader(Sheet sheet, List<String> headers, CellStyle style) {
        final Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            final Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(style);
        }
    }

    private void writeRows(Sheet sheet, List<List<Object>> data, CellStyle dateStyle) {
        int rowNum = 1;
        for (List<Object> rowData : data) {
            final Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < rowData.size(); i++) {
                final Cell cell = row.createCell(i);
                final Object value = rowData.get(i);

                if (value instanceof LocalDate localDate) {
                    cell.setCellValue(localDate);
                    cell.setCellStyle(dateStyle);
                } else if (value instanceof BigDecimal bigDecimal) {
                    cell.setCellValue(bigDecimal.doubleValue());
                } else if (value instanceof Number number) {
                    cell.setCellValue(number.doubleValue());
                } else if (value != null) {
                    cell.setCellValue(value.toString());
                }
            }
        }
    }

    private CellStyle createDateStyle(Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        final CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd.mm.yyyy"));
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
