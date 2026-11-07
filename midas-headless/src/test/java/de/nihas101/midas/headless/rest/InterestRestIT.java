package de.nihas101.midas.headless.rest;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.headless.rest.dto.BookingsResponse;
import de.nihas101.midas.headless.rest.dto.CreateInterestRate;
import de.nihas101.midas.headless.rest.dto.RequestShareholder;
import de.nihas101.midas.headless.rest.dto.RequestToggleAutomaticInterestBooking;
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
        final CreateInterestRate rateDto = CreateInterestRate.builder()
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

        // 4. Toggle the generation of interest booking
        final RequestToggleAutomaticInterestBooking toggleRequest = RequestToggleAutomaticInterestBooking.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .toggleAutomaticBooking(true)
                .build();

        mockMvc.perform(post("/api/v1/interest/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toggleRequest)))
                .andExpect(status().isOk());

        // 5. Verify that an interest booking is generated
        final MvcResult bookingsResult = mockMvc.perform(get("/api/v1/bookings?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andReturn();

        final BookingsResponse bookingsResponse = objectMapper.readValue(bookingsResult.getResponse().getContentAsString(), BookingsResponse.class);
        final boolean hasInterestBooking = bookingsResponse.getBookings().stream()
                .anyMatch(b -> BookingType.INTEREST.equals(b.getType()));
        assertTrue(hasInterestBooking, "Expected interest booking in bookings list");
    }
}
