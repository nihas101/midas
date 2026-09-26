package de.nihas101.midas.headless.rest;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.headless.rest.dto.BookingDto;
import de.nihas101.midas.headless.rest.dto.BookingsResponse;
import de.nihas101.midas.headless.rest.dto.CarryOverRequest;
import de.nihas101.midas.headless.rest.dto.CommentTemplateDto;
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
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private ShareholderDto createTestShareholder(String first, String last) throws Exception {
        final ShareholderDto dto = ShareholderDto.builder()
                .firstName(first)
                .lastName(last)
                .build();
        final MvcResult result = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), ShareholderDto.class);
    }

    @Test
    void testBookingsWorkflow() throws Exception {
        final ShareholderDto sh = createTestShareholder("Alice", "Smith");

        // 1. Set opening balance
        final OpeningBalanceDto obDto = OpeningBalanceDto.builder()
                .shareholderId(sh.getId())
                .year(Year.of(2026))
                .openingBalance(MoneyAmount.of(new BigDecimal("500.00")))
                .build();

        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(obDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openingBalance").value(500.00));

        // 2. Add booking
        final BookingDto bookingDto = BookingDto.builder()
                .shareholderId(sh.getId())
                .date(LocalDate.of(2026, 5, 10))
                .type(BookingType.WITHDRAWAL)
                .comment("Test Withdrawal Booking")
                .amount(MoneyAmount.of(new BigDecimal("150.00")))
                .build();

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.comment").value("Test Withdrawal Booking"));

        // 3. Verify bookings list
        final MvcResult listResult = mockMvc.perform(get("/api/v1/bookings?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openingBalance.openingBalance").value(500.00))
                .andExpect(jsonPath("$.bookings", hasSize(1)))
                .andExpect(jsonPath("$.bookings[0].comment").value("Test Withdrawal Booking"))
                .andReturn();

        final BookingsResponse resp = objectMapper.readValue(listResult.getResponse().getContentAsString(), BookingsResponse.class);
        assertNotNull(resp.getOpeningBalance());
        assertEquals(1, resp.getBookings().size());
    }

    @Test
    void testDoubleBookingWarningAndForceSave() throws Exception {
        final ShareholderDto sh = createTestShareholder("Bob", "Jones");

        final BookingDto bookingDto = BookingDto.builder()
                .shareholderId(sh.getId())
                .date(LocalDate.of(2026, 6, 12))
                .type(BookingType.WITHDRAWAL)
                .comment("Duplicate Booking Text")
                .amount(MoneyAmount.of(new BigDecimal("50.00")))
                .build();

        // First creation succeeds
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated());

        // Duplicate creation returns 409 Conflict without force
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.warning").value("DUPLICATE_BOOKING"));

        // Duplicate creation with ?force=true succeeds
        mockMvc.perform(post("/api/v1/bookings?force=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated());

        // Verify two bookings exist
        mockMvc.perform(get("/api/v1/bookings?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookings", hasSize(2)));
    }

    @Test
    void testCarryOverToNextYear() throws Exception {
        final ShareholderDto sh = createTestShareholder("Alice", "Carry");

        // Booking 50.00 in 2026
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingDto.builder()
                                .shareholderId(sh.getId())
                                .date(LocalDate.of(2026, 6, 1))
                                .type(BookingType.COMPENSATION)
                                .amount(MoneyAmount.of(new BigDecimal("50.00")))
                                .comment("Deposit in 2026")
                                .build())))
                .andExpect(status().isCreated());

        // Set opening balance 2026 to 100.00
        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OpeningBalanceDto.builder()
                                .shareholderId(sh.getId())
                                .year(Year.of(2026))
                                .openingBalance(MoneyAmount.of(new BigDecimal("100.00")))
                                .build())))
                .andExpect(status().isOk());

        // Verify no opening balance in 2027 yet
        mockMvc.perform(get("/api/v1/opening-balances?shareholderId=" + sh.getId() + "&year=2027"))
                .andExpect(status().isNotFound());

        // Carry over to 2027
        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CarryOverRequest.builder()
                                .shareholderId(sh.getId())
                                .fromYear(Year.of(2026))
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openingBalance").value(150.00))
                .andExpect(jsonPath("$.source").value("SYSTEM"));
    }

    @Test
    void testCarryOverWhenNextYearHasOpeningBalanceConflictAndOverwrite() throws Exception {
        final ShareholderDto sh = createTestShareholder("Alice", "Conflict");

        // Prepopulate opening balance for 2027 with 300.00 USER source
        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OpeningBalanceDto.builder()
                                .shareholderId(sh.getId())
                                .year(Year.of(2027))
                                .openingBalance(MoneyAmount.of(new BigDecimal("300.00")))
                                .source(Source.USER)
                                .build())))
                .andExpect(status().isOk());

        // Booking 50.00 in 2026 and opening balance 100.00 in 2026
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingDto.builder()
                                .shareholderId(sh.getId())
                                .date(LocalDate.of(2026, 6, 1))
                                .type(BookingType.COMPENSATION)
                                .amount(MoneyAmount.of(new BigDecimal("50.00")))
                                .comment("Deposit in 2026")
                                .build())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OpeningBalanceDto.builder()
                                .shareholderId(sh.getId())
                                .year(Year.of(2026))
                                .openingBalance(MoneyAmount.of(new BigDecimal("100.00")))
                                .build())))
                .andExpect(status().isOk());

        // Attempt carry over without forceOverwrite returns 409
        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CarryOverRequest.builder()
                                .shareholderId(sh.getId())
                                .fromYear(Year.of(2026))
                                .forceOverwrite(false)
                                .build())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.warning").value("OPENING_BALANCE_EXISTS"));

        // With forceOverwrite: true, succeeds and overwrites to 150.00 SYSTEM source
        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CarryOverRequest.builder()
                                .shareholderId(sh.getId())
                                .fromYear(Year.of(2026))
                                .forceOverwrite(true)
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openingBalance").value(150.00))
                .andExpect(jsonPath("$.source").value("SYSTEM"));
    }

    @Test
    void testCarryOverAndThenUncheck() throws Exception {
        final ShareholderDto sh = createTestShareholder("Alice", "Uncheck");

        // 2026: 100 opening + 50 deposit = 150
        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OpeningBalanceDto.builder()
                                .shareholderId(sh.getId())
                                .year(Year.of(2026))
                                .openingBalance(MoneyAmount.of(new BigDecimal("100.00")))
                                .build())))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingDto.builder()
                                .shareholderId(sh.getId())
                                .date(LocalDate.of(2026, 6, 1))
                                .type(BookingType.COMPENSATION)
                                .amount(MoneyAmount.of(new BigDecimal("50.00")))
                                .comment("Deposit")
                                .build())))
                .andExpect(status().isCreated());

        // Carry over
        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CarryOverRequest.builder()
                                .shareholderId(sh.getId())
                                .fromYear(Year.of(2026))
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("SYSTEM"));

        // Uncheck / deactivate carry over (active: false)
        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CarryOverRequest.builder()
                                .shareholderId(sh.getId())
                                .fromYear(Year.of(2026))
                                .active(false)
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openingBalance").value(150.00))
                .andExpect(jsonPath("$.source").value("USER"));
    }

    @Test
    void testCarryOverWhenNoBookingsExistWithOpeningBalance() throws Exception {
        final ShareholderDto sh = createTestShareholder("Alice", "Empty");

        // 2026: 100.00 opening balance, no bookings
        mockMvc.perform(post("/api/v1/opening-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OpeningBalanceDto.builder()
                                .shareholderId(sh.getId())
                                .year(Year.of(2026))
                                .openingBalance(MoneyAmount.of(new BigDecimal("100.00")))
                                .build())))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/opening-balances/carry-over")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CarryOverRequest.builder()
                                .shareholderId(sh.getId())
                                .fromYear(Year.of(2026))
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openingBalance").value(100.00))
                .andExpect(jsonPath("$.source").value("SYSTEM"));
    }

    @Test
    void testCommentTemplatesSuggestionsAndCustomInput() throws Exception {
        // Save templates
        mockMvc.perform(post("/api/v1/comment-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CommentTemplateDto.builder()
                                .text("General Template")
                                .bookingTypes(Set.of())
                                .build())))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/comment-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CommentTemplateDto.builder()
                                .text("Withdrawal Only Template")
                                .bookingTypes(Set.of(BookingType.WITHDRAWAL))
                                .build())))
                .andExpect(status().isOk());

        // Verify suggestions
        mockMvc.perform(get("/api/v1/comment-templates/suggestions?bookingType=WITHDRAWAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItem("General Template")))
                .andExpect(jsonPath("$", hasItem("Withdrawal Only Template")));

        // Create booking with custom comment
        final ShareholderDto sh = createTestShareholder("Charlie", "TemplateUser");
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingDto.builder()
                                .shareholderId(sh.getId())
                                .date(LocalDate.of(2026, 7, 1))
                                .type(BookingType.COMPENSATION)
                                .amount(MoneyAmount.of(new BigDecimal("75.00")))
                                .comment("My Custom Comment")
                                .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment").value("My Custom Comment"));
    }

    @Test
    void testDeleteBookingWorkflow() throws Exception {
        final ShareholderDto sh = createTestShareholder("Charlie", "Delete");

        final MvcResult createResult = mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingDto.builder()
                                .shareholderId(sh.getId())
                                .date(LocalDate.of(2026, 5, 10))
                                .type(BookingType.WITHDRAWAL)
                                .amount(MoneyAmount.of(new BigDecimal("120.00")))
                                .comment("Delete Me Booking")
                                .build())))
                .andExpect(status().isCreated())
                .andReturn();

        final BookingDto created = objectMapper.readValue(createResult.getResponse().getContentAsString(), BookingDto.class);

        mockMvc.perform(delete("/api/v1/bookings/" + created.getId() + "?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isNoContent());

        // Verify bookings is now empty
        mockMvc.perform(get("/api/v1/bookings?shareholderId=" + sh.getId() + "&year=2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookings", hasSize(0)));
    }
}
