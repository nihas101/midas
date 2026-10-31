package de.nihas101.midas.headless.rest;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.headless.rest.dto.ExportApiRequest;
import de.nihas101.midas.headless.rest.dto.MoneyAmountDto;
import de.nihas101.midas.headless.rest.dto.RequestBooking;
import de.nihas101.midas.headless.rest.dto.RequestOpeningBalance;
import de.nihas101.midas.headless.rest.dto.RequestShareholder;
import de.nihas101.midas.headless.rest.dto.ResponseShareholder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;

import static de.nihas101.midas.core.export.ExportViewName.BOOKINGS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    private ResponseShareholder shareholder;

    @BeforeEach
    void setUp() throws Exception {
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Export")
                .lastName("Tester")
                .build();
        final MvcResult shResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        shareholder = objectMapper.readValue(shResult.getResponse().getContentAsString(), ResponseShareholder.class);

        // Add opening balance
        final RequestOpeningBalance ob = RequestOpeningBalance.builder()
                .shareholderId(shareholder.getId())
                .year(Year.of(2026))
                .openingBalance(MoneyAmountDto.of("1000.00"))
                .build();
        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ob)))
                .andExpect(status().isCreated());

        // Add a booking
        final RequestBooking booking = RequestBooking.builder()
                .shareholderId(shareholder.getId())
                .date(LocalDate.of(2026, 4, 1))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmountDto.of("250.00"))
                .comment("Q2 Deposit")
                .build();
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated());
    }

    @Test
    void testPdfExportReturnsPdfBytes() throws Exception {
        final ExportApiRequest request = ExportApiRequest.builder()
                .shareholderIds(List.of(shareholder.getId()))
                .views(Set.of(BOOKINGS))
                .from(LocalDate.of(2026, 1, 1))
                .until(LocalDate.of(2026, 12, 31))
                .formats(Set.of("pdf"))
                .build();

        final MvcResult result = mockMvc.perform(post("/api/v1/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andReturn();

        final byte[] bytes = result.getResponse().getContentAsByteArray();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0, "PDF byte array should not be empty");
    }

    @Test
    void testXlsxExportReturnsExcelBytes() throws Exception {
        final ExportApiRequest request = ExportApiRequest.builder()
                .shareholderIds(List.of(shareholder.getId()))
                .views(Set.of(BOOKINGS))
                .from(LocalDate.of(2026, 1, 1))
                .until(LocalDate.of(2026, 12, 31))
                .formats(Set.of("xlsx"))
                .build();

        final MvcResult result = mockMvc.perform(post("/api/v1/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andReturn();

        final byte[] bytes = result.getResponse().getContentAsByteArray();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0, "Excel byte array should not be empty");
    }

    @Test
    void testMultipleFormatsReturnsZip() throws Exception {
        final ExportApiRequest request = ExportApiRequest.builder()
                .shareholderIds(List.of(shareholder.getId()))
                .views(Set.of(BOOKINGS))
                .from(LocalDate.of(2026, 1, 1))
                .until(LocalDate.of(2026, 12, 31))
                .formats(Set.of("pdf", "xlsx"))
                .build();

        final MvcResult result = mockMvc.perform(post("/api/v1/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/zip"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"export.zip\""))
                .andReturn();

        final byte[] bytes = result.getResponse().getContentAsByteArray();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0, "Zip byte array should not be empty");
    }

    @Test
    void testExportAllShareholdersWhenNoListProvided() throws Exception {
        final ExportApiRequest request = ExportApiRequest.builder()
                .shareholderIds(null)
                .views(Set.of(BOOKINGS))
                .from(LocalDate.of(2026, 1, 1))
                .until(LocalDate.of(2026, 12, 31))
                .formats(Set.of("xlsx"))
                .build();

        final MvcResult result = mockMvc.perform(post("/api/v1/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andReturn();

        final byte[] bytes = result.getResponse().getContentAsByteArray();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }
}
