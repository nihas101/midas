package de.nihas101.midas.headless.rest;

import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.headless.rest.dto.AccountStatementOrderRequest;
import de.nihas101.midas.headless.rest.dto.AccountStatementOverrideRequest;
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
import java.time.Year;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        // 1. Create shareholder
        final ShareholderDto dto = ShareholderDto.builder()
                .firstName("Bob")
                .lastName("Jones")
                .build();
        final MvcResult createShResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ShareholderDto sh = objectMapper.readValue(createShResult.getResponse().getContentAsString(), ShareholderDto.class);

        // 2. Add manual override
        final AccountStatementOverrideRequest overrideReq = AccountStatementOverrideRequest.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .labelOverride("Bonus Dividend")
                .amount(MoneyAmount.of(new BigDecimal("250.00")))
                .build();

        mockMvc.perform(post("/api/v1/account-statements/overrides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overrideReq)))
                .andExpect(status().isOk());

        // 3. Verify manual entry is present in account statements
        mockMvc.perform(get("/api/v1/account-statements?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overrides", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.overrides[*].labelOverride", hasItem("Bonus Dividend")));

        // 4. Test reordering rows
        final AccountStatementOrderRequest orderRequest = AccountStatementOrderRequest.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .rowKeys(List.of("key1", "key2"))
                .build();

        mockMvc.perform(post("/api/v1/account-statements/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk());
    }
}
