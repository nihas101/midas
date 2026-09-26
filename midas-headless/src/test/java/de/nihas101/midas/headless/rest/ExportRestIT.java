package de.nihas101.midas.headless.rest;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.headless.rest.dto.BookingDto;
import de.nihas101.midas.headless.rest.dto.ExportApiRequest;
import de.nihas101.midas.headless.rest.dto.InterestRateDto;
import de.nihas101.midas.headless.rest.dto.OpeningBalanceDto;
import de.nihas101.midas.headless.rest.dto.ShareholderDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExportRestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ShareholderDto createShareholderWithData(String first, String last, Year year) throws Exception {
        final MvcResult createShResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ShareholderDto.builder()
                                .firstName(first)
                                .lastName(last)
                                .build())))
                .andExpect(status().isCreated())
                .andReturn();
        final ShareholderDto sh = objectMapper.readValue(createShResult.getResponse().getContentAsString(), ShareholderDto.class);

        // Opening balance
        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OpeningBalanceDto.builder()
                                .shareholderId(sh.getId())
                                .year(year)
                                .openingBalance(MoneyAmount.of(new BigDecimal("1000.00")))
                                .build())))
                .andExpect(status().isOk());

        // Interest rate
        mockMvc.perform(post("/api/v1/interest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(InterestRateDto.builder()
                                .shareholderId(sh.getId())
                                .year(year)
                                .interestRate(new BigDecimal("3.00"))
                                .build())))
                .andExpect(status().isOk());

        // Booking
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingDto.builder()
                                .shareholderId(sh.getId())
                                .date(year.atMonth(4).atDay(15))
                                .type(BookingType.WITHDRAWAL)
                                .amount(MoneyAmount.of(new BigDecimal("200.00")))
                                .comment("Sample Withdrawal " + year)
                                .build())))
                .andExpect(status().isCreated());

        return sh;
    }

    @Test
    void testSinglePdfExport_singleShareholderSingleViewSingleYear() throws Exception {
        final ShareholderDto sh = createShareholderWithData("Diana", "Prince", Year.of(2026));

        final ExportApiRequest request = ExportApiRequest.builder()
                .shareholderIds(List.of(sh.getId()))
                .views(Set.of(ExportViewName.BOOKINGS))
                .from(LocalDate.of(2026, 1, 1))
                .until(LocalDate.of(2026, 12, 31))
                .formats(Set.of("pdf"))
                .build();

        final MvcResult result = mockMvc.perform(post("/api/v1/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andReturn();

        assertTrue(result.getResponse().getContentAsByteArray().length > 0);
    }

    @Test
    void testXlsxExport_singleYear() throws Exception {
        final ShareholderDto sh = createShareholderWithData("Arthur", "Curry", Year.of(2026));

        final ExportApiRequest request = ExportApiRequest.builder()
                .shareholderIds(List.of(sh.getId()))
                .views(Set.of(ExportViewName.BOOKINGS, ExportViewName.ACCOUNT_STATEMENTS, ExportViewName.INTEREST))
                .from(LocalDate.of(2026, 1, 1))
                .until(LocalDate.of(2026, 12, 31))
                .formats(Set.of("xlsx"))
                .build();

        final MvcResult result = mockMvc.perform(post("/api/v1/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andReturn();

        assertTrue(result.getResponse().getContentAsByteArray().length > 0);
    }

    @Test
    void testBothPdfAndXlsxExport_createsZip() throws Exception {
        final ShareholderDto sh = createShareholderWithData("Victor", "Stone", Year.of(2026));

        final ExportApiRequest request = ExportApiRequest.builder()
                .shareholderIds(List.of(sh.getId()))
                .views(Set.of(ExportViewName.BOOKINGS, ExportViewName.ACCOUNT_STATEMENTS))
                .from(LocalDate.of(2026, 1, 1))
                .until(LocalDate.of(2026, 12, 31))
                .formats(Set.of("pdf", "xlsx"))
                .build();

        final MvcResult result = mockMvc.perform(post("/api/v1/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/zip"))
                .andReturn();

        assertTrue(result.getResponse().getContentAsByteArray().length > 0);
    }

    @Test
    void testExportValidation() throws Exception {
        // Invalid date range (from > until)
        final ExportApiRequest invalidDates = ExportApiRequest.builder()
                .from(LocalDate.of(2026, 12, 31))
                .until(LocalDate.of(2026, 1, 1))
                .formats(Set.of("pdf"))
                .build();

        mockMvc.perform(post("/api/v1/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDates)))
                .andExpect(status().isBadRequest());

        // Empty formats
        final ExportApiRequest emptyFormats = ExportApiRequest.builder()
                .from(LocalDate.of(2026, 1, 1))
                .until(LocalDate.of(2026, 12, 31))
                .formats(Set.of())
                .build();

        mockMvc.perform(post("/api/v1/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyFormats)))
                .andExpect(status().isBadRequest());
    }
}
