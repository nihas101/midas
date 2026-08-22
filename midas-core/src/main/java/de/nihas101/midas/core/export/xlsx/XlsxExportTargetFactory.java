package de.nihas101.midas.core.export.xlsx;

import de.nihas101.midas.core.config.DatesConfig;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RequiredArgsConstructor
public class XlsxExportTargetFactory {

    private final DatesConfig datesConfig;

    public XlsxExportTarget exportTarget() {
        return new XlsxExportTarget(new XSSFWorkbook(), datesConfig);
    }
}
