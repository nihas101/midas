package de.nihas101.midas.headless.rest;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.headless.rest.dto.AccountStatementOrderRequest;
import de.nihas101.midas.headless.rest.dto.AccountStatementsResponse;
import de.nihas101.midas.headless.rest.dto.CreateManualExtraRequest;
import de.nihas101.midas.headless.rest.dto.ExcludeAccountStatementRequest;
import de.nihas101.midas.headless.rest.dto.MoneyAmountDto;
import de.nihas101.midas.headless.rest.dto.RequestBooking;
import de.nihas101.midas.headless.rest.dto.RequestOpeningBalance;
import de.nihas101.midas.headless.rest.dto.RequestShareholder;
import de.nihas101.midas.headless.rest.dto.ResponseShareholder;
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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountStatementRestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAccountStatementWorkflow() throws Exception {
        // 1. Create a shareholder
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Statement")
                .lastName("Tester")
                .build();
        final MvcResult shResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(shResult.getResponse().getContentAsString(), ResponseShareholder.class);

        // 2. Set Opening Balance
        final RequestOpeningBalance ob = RequestOpeningBalance.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .openingBalance(MoneyAmountDto.of("1000.00"))
                .build();
        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ob)))
                .andExpect(status().isCreated());

        // 3. Create a Booking
        final RequestBooking booking = RequestBooking.builder()
                .shareholderId(sh.getId())
                .date(LocalDate.of(2026, 3, 10))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmountDto.of("500.00"))
                .comment("March Deposit")
                .build();
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated());

        // 4. Retrieve Account Statements
        final MvcResult stmtsResult = mockMvc.perform(get("/api/v1/account-statements?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatements", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.overrides").isArray())
                .andReturn();

        final AccountStatementsResponse resp = objectMapper.readValue(stmtsResult.getResponse().getContentAsString(), AccountStatementsResponse.class);

        // 5. Add a Manual Statement
        final CreateManualExtraRequest manualReq = CreateManualExtraRequest.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .label("Bonus adjustment")
                .amount(MoneyAmountDto.of("75.00"))
                .build();
        mockMvc.perform(post("/api/v1/account-statements/manual-extra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(manualReq)))
                .andExpect(status().isOk());

        // 6. Verify manual extra is now in statements
        final MvcResult updatedStmts = mockMvc.perform(get("/api/v1/account-statements?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andReturn();
        final AccountStatementsResponse updatedResp = objectMapper.readValue(updatedStmts.getResponse().getContentAsString(), AccountStatementsResponse.class);
        final boolean hasManual = updatedResp.getAccountStatements().stream()
                .anyMatch(s -> "Bonus adjustment".equals(s.getLabel()));
        assertTrue(hasManual);

        // 7. Update Statement Order
        // TODO: Do not base this order update on the rowKey
        /*
        final String firstKey = updatedResp.getAccountStatements().getFirst().getRowKey();
        final AccountStatementOrderRequest orderReq = AccountStatementOrderRequest.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .rowKeys(List.of(firstKey))
                .build();
        mockMvc.perform(put("/api/v1/account-statements/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isOk());
         */

        // 8. Exclude a Statement
        final ExcludeAccountStatementRequest excludeReq = ExcludeAccountStatementRequest.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .exclude(true)
                .build();
        mockMvc.perform(post("/api/v1/account-statements/exclude")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(excludeReq)))
                .andExpect(status().isOk());
    }
}
