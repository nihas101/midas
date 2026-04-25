package de.nihas101.midas.export.xlsx;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XlsxExportTargetTest {

    private XlsxExportTarget target;

    @BeforeEach
    void setUp() {
        target = new XlsxExportTarget();
    }

    @Test
    void export_writesDataCorrectToWorkbook() throws Exception {
        String sheetName = "Test Sheet";
        List<String> headers = List.of("Name", "Amount", "Date", "Empty");
        LocalDate date = LocalDate.of(2023, 5, 20);
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList("Alice", 100, date, null),
                Arrays.asList("Bob", new BigDecimal("123.45"), date.plusDays(1), "")
        );

        target.export(sheetName, headers, rows);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        target.write(bos);
        
        try (XSSFWorkbook resultWorkbook = new XSSFWorkbook(new ByteArrayInputStream(bos.toByteArray()))) {
            assertEquals(1, resultWorkbook.getNumberOfSheets());
            Sheet sheet = resultWorkbook.getSheet(sheetName);
            assertNotNull(sheet);

            Row headerRow = sheet.getRow(0);
            assertEquals("Name", headerRow.getCell(0).getStringCellValue());
            assertEquals("Amount", headerRow.getCell(1).getStringCellValue());
            assertEquals("Date", headerRow.getCell(2).getStringCellValue());
            assertEquals("Empty", headerRow.getCell(3).getStringCellValue());

            Row row1 = sheet.getRow(1);
            assertEquals("Alice", row1.getCell(0).getStringCellValue());
            assertEquals(100.0, row1.getCell(1).getNumericCellValue());
            assertNotNull(row1.getCell(2).getDateCellValue());
            assertEquals(CellType.BLANK, row1.getCell(3).getCellType());

            Row row2 = sheet.getRow(2);
            assertEquals("Bob", row2.getCell(0).getStringCellValue());
            assertEquals(123.45, row2.getCell(1).getNumericCellValue());
        }
    }

    @Test
    void export_withEmptyData_createsSheetWithOnlyHeaders() throws Exception {
        String sheetName = "Empty Rows";
        List<String> headers = List.of("Header1", "Header2");

        target.export(sheetName, headers, List.of());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        target.write(bos);

        try (XSSFWorkbook resultWorkbook = new XSSFWorkbook(new ByteArrayInputStream(bos.toByteArray()))) {
            Sheet sheet = resultWorkbook.getSheet(sheetName);
            assertEquals(0, sheet.getLastRowNum());
            assertNotNull(sheet.getRow(0));
            assertNull(sheet.getRow(1));
        }
    }

    @Test
    void export_multipleSheets_addsThemToWorkbook() throws Exception {
        target.export("Sheet1", List.of("H1"), List.of(List.of("V1")));
        target.export("Sheet2", List.of("H2"), List.of(List.of("V2")));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        target.write(bos);

        try (XSSFWorkbook resultWorkbook = new XSSFWorkbook(new ByteArrayInputStream(bos.toByteArray()))) {
            assertEquals(2, resultWorkbook.getNumberOfSheets());
            assertNotNull(resultWorkbook.getSheet("Sheet1"));
            assertNotNull(resultWorkbook.getSheet("Sheet2"));
        }
    }

    @Test
    void close_closesTheUnderlyingWorkbook() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XlsxExportTarget target = new XlsxExportTarget(workbook);

        target.close();

        // POI Workbook throws an exception if trying to write after close
        assertThrows(IOException.class, () -> workbook.write(new ByteArrayOutputStream()));
    }
}
