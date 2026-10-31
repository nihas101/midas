package de.nihas101.midas.headless.rest;

import de.nihas101.midas.headless.rest.dto.RequestSettings;
import de.nihas101.midas.headless.rest.dto.ResponseSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SettingsRestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAndUpdateSettings() throws Exception {
        // 1. Get default settings
        mockMvc.perform(get("/api/v1/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theme").exists())
                .andExpect(jsonPath("$.locale").exists());

        // 2. Update settings
        final RequestSettings updateDto = RequestSettings.builder()
                .theme("dark")
                .locale(Locale.GERMAN)
                .build();

        final MvcResult updateResult = mockMvc.perform(put("/api/v1/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theme").value("dark"))
                .andExpect(jsonPath("$.locale").value("de"))
                .andReturn();

        final ResponseSettings updated = objectMapper.readValue(updateResult.getResponse().getContentAsString(), ResponseSettings.class);
        assertEquals("dark", updated.getTheme());
        assertEquals(Locale.GERMAN, updated.getLocale());

        // 3. Verify changes persist
        mockMvc.perform(get("/api/v1/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theme").value("dark"))
                .andExpect(jsonPath("$.locale").value("de"));
    }
}
