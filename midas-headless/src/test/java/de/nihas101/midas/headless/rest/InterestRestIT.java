package de.nihas101.midas.headless.rest;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.headless.rest.dto.RequestCreateInterestRate;
import de.nihas101.midas.headless.rest.dto.RequestShareholder;
import de.nihas101.midas.headless.rest.dto.RequestToggleAutomaticInterestBooking;
import de.nihas101.midas.headless.rest.dto.ResponseBookings;
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

import java.math.BigDecimal;
import java.time.Year;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InterestRestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateDuplicateInterestRate_returns409() throws Exception {
        // Create a shareholder
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Duplicate")
                .lastName("RateTester")
                .build();
        final MvcResult createShResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(createShResult.getResponse().getContentAsString(), ResponseShareholder.class);

        // Create interest rate for the first time — should succeed
        final RequestCreateInterestRate rateDto = RequestCreateInterestRate.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2025))
                .interestRate(new BigDecimal("3.00"))
                .build();

        mockMvc.perform(post("/api/v1/interest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateDto)))
                .andExpect(status().isOk());

        // Attempt to create a duplicate interest rate — should return 409 Conflict
        mockMvc.perform(post("/api/v1/interest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testInterestCalculationWorkflow() throws Exception {
        // 1. Create a shareholder
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Interest")
                .lastName("Tester")
                .build();
        final MvcResult createShResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(createShResult.getResponse().getContentAsString(), ResponseShareholder.class);

        // 2. Set interest rate
        final RequestCreateInterestRate rateDto = RequestCreateInterestRate.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .interestRate(new BigDecimal("2.50"))
                .build();

        mockMvc.perform(post("/api/v1/interest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interestRate").value(2.50));

        // 3. Verify interest rate and rows
        mockMvc.perform(get("/api/v1/interest?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interestRate").value(2.50))
                .andExpect(jsonPath("$.rows", hasSize(greaterThan(0))));

        // 4. Check initial toggle status (should be false)
        mockMvc.perform(get("/api/v1/interest/booking?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        // 5. Toggle the generation of interest booking
        final RequestToggleAutomaticInterestBooking toggleRequest = RequestToggleAutomaticInterestBooking.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .toggleAutomaticBooking(true)
                .build();

        mockMvc.perform(post("/api/v1/interest/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toggleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        // 6. Check toggle status after toggling (should be true)
        mockMvc.perform(get("/api/v1/interest/booking?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        // 7. Verify that an interest booking is generated
        final MvcResult bookingsResult = mockMvc.perform(get("/api/v1/bookings?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andReturn();

        final ResponseBookings bookingsResponse = objectMapper.readValue(bookingsResult.getResponse().getContentAsString(), ResponseBookings.class);
        final boolean hasInterestBooking = bookingsResponse.getBookings().stream()
                .anyMatch(b -> BookingType.INTEREST.equals(b.getType()));
        assertTrue(hasInterestBooking, "Expected interest booking in bookings list");
    }
}
