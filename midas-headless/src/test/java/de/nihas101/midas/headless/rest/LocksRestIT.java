package de.nihas101.midas.headless.rest;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.headless.rest.dto.MoneyAmountDto;
import de.nihas101.midas.headless.rest.dto.RequestBooking;
import de.nihas101.midas.headless.rest.dto.RequestLock;
import de.nihas101.midas.headless.rest.dto.RequestShareholder;
import de.nihas101.midas.headless.rest.dto.ResponseBooking;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LocksRestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLockAndUnlockYear() throws Exception {
        // 1. Create a shareholder
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Lock")
                .lastName("Tester")
                .build();
        final MvcResult createShResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(createShResult.getResponse().getContentAsString(), ResponseShareholder.class);

        // 2. Check initial lock status -> unlocked
        mockMvc.perform(get("/api/v1/locks?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(false));

        // 3. Lock year 2026
        final RequestLock lockReq = RequestLock.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .locked(true)
                .build();
        mockMvc.perform(post("/api/v1/locks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lockReq)))
                .andExpect(status().isOk());

        // 4. Verify locked
        mockMvc.perform(get("/api/v1/locks?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(true));

        // 5. Unlock year 2026
        final RequestLock unlockReq = RequestLock.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .locked(false)
                .build();
        mockMvc.perform(post("/api/v1/locks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unlockReq)))
                .andExpect(status().isOk());

        // 6. Verify unlocked
        mockMvc.perform(get("/api/v1/locks?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(false));
    }

    @Test
    void testLockedYearPreventsBookingMutations() throws Exception {
        // Create shareholder
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("LockMutation")
                .lastName("Tester")
                .build();
        final MvcResult createShResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(createShResult.getResponse().getContentAsString(), ResponseShareholder.class);

        // Lock year 2026
        final RequestLock lockReq = RequestLock.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .locked(true)
                .build();
        mockMvc.perform(post("/api/v1/locks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lockReq)))
                .andExpect(status().isOk());

        // Try to create a booking in locked year -> expect 423 Locked
        final RequestBooking shouldBeRejected = RequestBooking.builder()
                .shareholderId(sh.getId())
                .date(LocalDate.of(2026, 5, 10))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmountDto.of("100.00"))
                .comment("Should be rejected")
                .build();
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shouldBeRejected)))
                .andExpect(status().isLocked());
    }

    @Test
    void testLockedYearPreventsBookingDeletion() throws Exception {
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("LockDelete")
                .lastName("Tester")
                .build();
        final MvcResult createShResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(createShResult.getResponse().getContentAsString(), ResponseShareholder.class);

        // Create a booking first (year not locked yet)
        final RequestBooking booking = RequestBooking.builder()
                .shareholderId(sh.getId())
                .date(LocalDate.of(2026, 5, 10))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmountDto.of("100.00"))
                .comment("Before lock")
                .build();
        final MvcResult bookingResult = mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andReturn();

        final ResponseBooking created = objectMapper.readValue(bookingResult.getResponse().getContentAsString(), ResponseBooking.class);

        // Now lock the year
        final RequestLock lockRequest = RequestLock.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .locked(true)
                .build();
        mockMvc.perform(post("/api/v1/locks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lockRequest)))
                .andExpect(status().isOk());

        // Delete should be rejected
        mockMvc.perform(delete("/api/v1/bookings/" + created.getId() + "?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isLocked());
    }
}
