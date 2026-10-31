package de.nihas101.midas.headless.rest;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.headless.rest.dto.BookingsResponse;
import de.nihas101.midas.headless.rest.dto.CarryOverRequest;
import de.nihas101.midas.headless.rest.dto.MoneyAmountDto;
import de.nihas101.midas.headless.rest.dto.RequestBooking;
import de.nihas101.midas.headless.rest.dto.RequestOpeningBalance;
import de.nihas101.midas.headless.rest.dto.RequestShareholder;
import de.nihas101.midas.headless.rest.dto.ResponseBooking;
import de.nihas101.midas.headless.rest.dto.ResponseOpeningBalance;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookingsRestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBookingsWorkflow() throws Exception {
        // 1. Create a shareholder
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Alice")
                .lastName("Smith")
                .build();
        final MvcResult shResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(shResult.getResponse().getContentAsString(), ResponseShareholder.class);

        // 2. Set Opening Balance for 2026
        final RequestOpeningBalance obDto = RequestOpeningBalance.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .openingBalance(MoneyAmountDto.of("1000.00"))
                .build();
        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(obDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.openingBalance").value(1000.00));

        // 3. Create a Booking
        final RequestBooking bookingDto = RequestBooking.builder()
                .shareholderId(sh.getId())
                .date(LocalDate.of(2026, 3, 15))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmountDto.of("500.00"))
                .comment("Initial deposit")
                .build();

        final MvcResult bookingResult = mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.amount").value(500.00))
                .andReturn();
        final ResponseBooking createdBooking = objectMapper.readValue(bookingResult.getResponse().getContentAsString(), ResponseBooking.class);
        assertNotNull(createdBooking.getId());

        // 4. Update the Booking
        final RequestBooking updatedBookingDto = RequestBooking.builder()
                .shareholderId(sh.getId())
                .date(LocalDate.of(2026, 3, 15))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmountDto.of("600.00"))
                .comment("Initial deposit updated")
                .build();

        mockMvc.perform(put("/api/v1/bookings/" + createdBooking.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(600.00))
                .andExpect(jsonPath("$.comment").value("Initial deposit updated"));

        // 5. Query Bookings for Shareholder & Year
        mockMvc.perform(get("/api/v1/bookings?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookings", hasSize(1)))
                .andExpect(jsonPath("$.bookings[0].comment").value("Initial deposit updated"));

        // 6. Test Carry Over to next year (2027)
        final CarryOverRequest carryOverRequest = CarryOverRequest.builder()
                .shareholderId(sh.getId())
                .fromYear(Year.of(2026))
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carryOverRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2027))
                .andExpect(jsonPath("$.source").value("SYSTEM"));

        // Verify next year opening balance
        mockMvc.perform(get("/api/v1/opening-balances?shareholderId=" + sh.getId() + "&year=2027"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openingBalance").value(1600.00));
    }

    @Test
    void testDoubleBookingWarningAndForceSave() throws Exception {
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Bob")
                .lastName("Jones")
                .build();
        final MvcResult shResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(shResult.getResponse().getContentAsString(), ResponseShareholder.class);

        final RequestBooking bookingDto = RequestBooking.builder()
                .shareholderId(sh.getId())
                .date(LocalDate.of(2026, 4, 1))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmountDto.of("200.00"))
                .comment("April withdrawal")
                .build();

        // First creation succeeds
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated());

        // Duplicate creation without force fails with 409 Conflict
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.warning").value("DUPLICATE_BOOKING"));

        // Duplicate creation with force=true succeeds
        mockMvc.perform(post("/api/v1/bookings?force=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated());

        // Now should have 2 bookings
        mockMvc.perform(get("/api/v1/bookings?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookings", hasSize(2)));
    }

    @Test
    void testCarryOverWhenNextYearHasOpeningBalanceConflict() throws Exception {
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Charlie")
                .lastName("Brown")
                .build();
        final MvcResult shResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(shResult.getResponse().getContentAsString(), ResponseShareholder.class);

        // Prepopulate opening balance for 2027 with USER source
        final RequestOpeningBalance ob2027 = RequestOpeningBalance.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2027))
                .openingBalance(MoneyAmountDto.of("300.00"))
                .build();
        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ob2027)))
                .andExpect(status().isCreated());

        // Attempt carry over without force overwrite -> 409 Conflict
        final CarryOverRequest request = CarryOverRequest.builder()
                .shareholderId(sh.getId())
                .fromYear(Year.of(2026))
                .forceOverwrite(false)
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.warning").value("OPENING_BALANCE_EXISTS"));

        // Carry over with forceOverwrite=true -> 200 OK
        final CarryOverRequest forceRequest = CarryOverRequest.builder()
                .shareholderId(sh.getId())
                .fromYear(Year.of(2026))
                .forceOverwrite(true)
                .active(true)
                .build();

        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("SYSTEM"));
    }

    @Test
    void testCarryOverAndThenUncheck() throws Exception {
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Diana")
                .lastName("Prince")
                .build();
        final MvcResult shResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(shResult.getResponse().getContentAsString(), ResponseShareholder.class);

        // Initial carry over (active=true)
        final CarryOverRequest enableReq = CarryOverRequest.builder()
                .shareholderId(sh.getId())
                .fromYear(Year.of(2026))
                .active(true)
                .build();
        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enableReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("SYSTEM"));

        // Uncheck carry over (active=false)
        final CarryOverRequest disableReq = CarryOverRequest.builder()
                .shareholderId(sh.getId())
                .fromYear(Year.of(2026))
                .active(false)
                .build();
        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(disableReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("USER"));
    }

    @Test
    void testOpeningBalanceYearValidation() throws Exception {
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Eve")
                .lastName("Polastri")
                .build();
        final MvcResult shResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(shResult.getResponse().getContentAsString(), ResponseShareholder.class);

        final RequestOpeningBalance ob = RequestOpeningBalance.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .openingBalance(MoneyAmountDto.of("250.00"))
                .build();

        final MvcResult obResult = mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ob)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseOpeningBalance createdOb = objectMapper.readValue(obResult.getResponse().getContentAsString(), ResponseOpeningBalance.class);

        // Update opening balance
        final RequestOpeningBalance updatedOb = RequestOpeningBalance.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .openingBalance(MoneyAmountDto.of("350.00"))
                .build();

        mockMvc.perform(put("/api/v1/opening-balances/" + createdOb.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOb)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openingBalance").value(350.00));
    }

    @Test
    void testOpeningBalanceUpdatesOnBookingChanges() throws Exception {
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Frank")
                .lastName("Castle")
                .build();
        final MvcResult shResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(shResult.getResponse().getContentAsString(), ResponseShareholder.class);

        // Set initial opening balance
        final RequestOpeningBalance ob = RequestOpeningBalance.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .openingBalance(MoneyAmountDto.of("500.00"))
                .build();
        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ob)))
                .andExpect(status().isCreated());

        // Add a booking
        final RequestBooking booking = RequestBooking.builder()
                .shareholderId(sh.getId())
                .date(LocalDate.of(2026, 5, 1))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmountDto.of("100.00"))
                .comment("May deposit")
                .build();
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated());

        // Enable carry over
        final CarryOverRequest carryOver = CarryOverRequest.builder()
                .shareholderId(sh.getId())
                .fromYear(Year.of(2026))
                .active(true)
                .build();
        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carryOver)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openingBalance").value(600.00));
    }

    @Test
    void testDeleteBookingWorkflow() throws Exception {
        final RequestShareholder shDto = RequestShareholder.builder()
                .firstName("Grace")
                .lastName("Hopper")
                .build();
        final MvcResult shResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shDto)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseShareholder sh = objectMapper.readValue(shResult.getResponse().getContentAsString(), ResponseShareholder.class);

        final RequestBooking booking = RequestBooking.builder()
                .shareholderId(sh.getId())
                .date(LocalDate.of(2026, 6, 1))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmountDto.of("200.00"))
                .comment("To delete")
                .build();
        final MvcResult bResult = mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andReturn();
        final ResponseBooking created = objectMapper.readValue(bResult.getResponse().getContentAsString(), ResponseBooking.class);

        // Delete booking
        mockMvc.perform(delete("/api/v1/bookings/" + created.getId() + "?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isNoContent());

        // Verify it is gone
        final MvcResult verifyResult = mockMvc.perform(get("/api/v1/bookings?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andReturn();
        final BookingsResponse resp = objectMapper.readValue(verifyResult.getResponse().getContentAsString(), BookingsResponse.class);
        assertEquals(0, resp.getBookings().size());
    }
}
