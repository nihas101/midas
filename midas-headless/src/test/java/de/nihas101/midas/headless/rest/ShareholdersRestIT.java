package de.nihas101.midas.headless.rest;

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

import java.util.Random;

import static org.hamcrest.Matchers.hasItem;
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
public class ShareholdersRestIT {

    private static final Random RANDOM = new Random();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateShareholder() throws Exception {
        final String john = "John" + RANDOM.nextInt();
        final String doe = "Doe" + RANDOM.nextInt();
        final ShareholderDto dto = ShareholderDto.builder()
                .firstName(john)
                .lastName(doe)
                .build();

        final MvcResult result = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value(john))
                .andExpect(jsonPath("$.lastName").value(doe))
                .andReturn();

        final ShareholderDto created = objectMapper.readValue(result.getResponse().getContentAsString(), ShareholderDto.class);
        assertNotNull(created.getId());

        mockMvc.perform(get("/api/v1/shareholders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].firstName", hasItem(john)))
                .andExpect(jsonPath("$[*].lastName", hasItem(doe)));
    }

    @Test
    void testUpdateShareholder() throws Exception {
        final ShareholderDto dto = ShareholderDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .build();

        final MvcResult createResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        final ShareholderDto created = objectMapper.readValue(createResult.getResponse().getContentAsString(), ShareholderDto.class);

        final ShareholderDto updateDto = ShareholderDto.builder()
                .displayId(888)
                .firstName("JaneUpdated")
                .lastName("DoeUpdated")
                .build();

        mockMvc.perform(put("/api/v1/shareholders/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayId").value(888))
                .andExpect(jsonPath("$.firstName").value("JaneUpdated"))
                .andExpect(jsonPath("$.lastName").value("DoeUpdated"));

        mockMvc.perform(get("/api/v1/shareholders/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("JaneUpdated"));
    }

    @Test
    void testDeleteShareholder() throws Exception {
        final ShareholderDto dto = ShareholderDto.builder()
                .firstName("Delete")
                .lastName("Me")
                .build();

        final MvcResult createResult = mockMvc.perform(post("/api/v1/shareholders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        final ShareholderDto created = objectMapper.readValue(createResult.getResponse().getContentAsString(), ShareholderDto.class);

        mockMvc.perform(delete("/api/v1/shareholders/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/shareholders/" + created.getId()))
                .andExpect(status().isNotFound());
    }
}
