package de.nihas101.midas.export.xlsx;

import de.nihas101.midas.export.ExportTarget;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class XlsxExportTarget implements ExportTarget, AutoCloseable {

    private final XSSFWorkbook workbook;
    private final CellStyle dateStyle;
    private final CellStyle headerStyle;

    public XlsxExportTarget() {
        this(new XSSFWorkbook());
    }

    public XlsxExportTarget(final XSSFWorkbook workbook) {
        this.workbook = workbook;
        dateStyle = createDateStyle(workbook);
        headerStyle = createHeaderStyle(workbook);
    }

    private CellStyle createDateStyle(final Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        final CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd.mm.yyyy"));
        return style;
    }

    private CellStyle createHeaderStyle(final Workbook workbook) {
        final CellStyle style = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    @Override
    public void export(
            final String sheetName,
            final List<String> headers,
            final List<List<Object>> rows
    ) {
        final Sheet sheet = createSheet(sheetName);
        writeHeader(sheet, headers);
        writeRows(sheet, rows);
        autoSizeColumns(sheet, headers.size());
    }

    private void autoSizeColumns(final Sheet sheet, final int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    @Override
    public void close() throws Exception {
        workbook.close();
    }

    @Override
    public void write(final OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
    }

    private Sheet createSheet(final String sheetName) {
        return workbook.createSheet(sheetName);
    }

    private void writeHeader(final Sheet sheet, final List<String> headers) {
        final Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            final Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }
    }

    private void writeRows(final Sheet sheet, final List<List<Object>> rows) {
        int rowNum = 1;
        for (List<Object> rowData : rows) {
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

}
