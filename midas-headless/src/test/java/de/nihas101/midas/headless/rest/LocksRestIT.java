package de.nihas101.midas.headless.rest;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.headless.rest.dto.BookingDto;
import de.nihas101.midas.headless.rest.dto.LockDto;
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

    private ShareholderDto createTestShareholder(String first, String last) throws Exception {
        final MvcResult result = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ShareholderDto.builder()
                                .firstName(first)
                                .lastName(last)
                                .build())))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), ShareholderDto.class);
    }

    @Test
    void testLockAndUnlockYear() throws Exception {
        final ShareholderDto sh = createTestShareholder("Max", "Mustermann");

        // Initially unlocked
        mockMvc.perform(get("/api/v1/locks?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(false));

        // Lock the year
        mockMvc.perform(post("/api/v1/locks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LockDto.builder()
                                .shareholderId(sh.getId())
                                .year(Year.of(2026))
                                .build())))
                .andExpect(status().isOk());

        // Verify it is now locked
        mockMvc.perform(get("/api/v1/locks?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(true));

        // Unlock
        mockMvc.perform(delete("/api/v1/locks?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isNoContent());

        // Verify unlocked again
        mockMvc.perform(get("/api/v1/locks?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locked").value(false));
    }

    @Test
    void testCreateBookingOnLockedYearIsRejected() throws Exception {
        final ShareholderDto sh = createTestShareholder("Erika", "Musterfrau");

        // Lock year 2026
        mockMvc.perform(post("/api/v1/locks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LockDto.builder()
                                .shareholderId(sh.getId())
                                .year(Year.of(2026))
                                .build())))
                .andExpect(status().isOk());

        // Attempt to create booking in locked year → expect 423 Locked
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingDto.builder()
                                .shareholderId(sh.getId())
                                .date(LocalDate.of(2026, 5, 10))
                                .type(BookingType.WITHDRAWAL)
                                .amount(MoneyAmount.of(new BigDecimal("100.00")))
                                .comment("Should be rejected")
                                .build())))
                .andExpect(status().isLocked());
    }

    @Test
    void testDeleteBookingOnLockedYearIsRejected() throws Exception {
        final ShareholderDto sh = createTestShareholder("Karl", "Lockedmann");

        // Create a booking first (year not locked yet)
        final MvcResult bookingResult = mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingDto.builder()
                                .shareholderId(sh.getId())
                                .date(LocalDate.of(2026, 5, 10))
                                .type(BookingType.WITHDRAWAL)
                                .amount(MoneyAmount.of(new BigDecimal("100.00")))
                                .comment("Booking before lock")
                                .build())))
                .andExpect(status().isCreated())
                .andReturn();

        final BookingDto created = objectMapper.readValue(bookingResult.getResponse().getContentAsString(), BookingDto.class);

        // Now lock the year
        mockMvc.perform(post("/api/v1/locks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LockDto.builder()
                                .shareholderId(sh.getId())
                                .year(Year.of(2026))
                                .build())))
                .andExpect(status().isOk());

        // Delete should be rejected
        mockMvc.perform(delete("/api/v1/bookings/" + created.getId() + "?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isLocked());
    }
}
