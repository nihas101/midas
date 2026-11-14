package de.nihas101.midas.headless.rest;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.headless.rest.dto.RequestCommentTemplate;
import de.nihas101.midas.headless.rest.dto.ResponseCommentTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentTemplatesRestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCommentTemplatesCrudAndSuggestions() throws Exception {
        // 1. Create first comment template
        final RequestCommentTemplate request1 = RequestCommentTemplate.builder()
                .text("Compensation note 1")
                .bookingTypes(Set.of(BookingType.COMPENSATION))
                .build();

        final MvcResult createResult1 = mockMvc.perform(post("/api/v1/comment-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value("Compensation note 1"))
                .andReturn();

        final ResponseCommentTemplate createdTemplate1 = objectMapper.readValue(
                createResult1.getResponse().getContentAsString(),
                ResponseCommentTemplate.class
        );
        assertNotNull(createdTemplate1.getId());

        // 2. Create second comment template
        final RequestCommentTemplate request2 = RequestCommentTemplate.builder()
                .text("Withdrawal note 2")
                .bookingTypes(Set.of(BookingType.WITHDRAWAL))
                .build();

        final MvcResult createResult2 = mockMvc.perform(post("/api/v1/comment-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value("Withdrawal note 2"))
                .andReturn();

        final ResponseCommentTemplate createdTemplate2 = objectMapper.readValue(
                createResult2.getResponse().getContentAsString(),
                ResponseCommentTemplate.class
        );

        // 3. List all templates
        final MvcResult listResult = mockMvc.perform(get("/api/v1/comment-templates"))
                .andExpect(status().isOk())
                .andReturn();

        final List<ResponseCommentTemplate> templates = objectMapper.readValue(
                listResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );
        assertTrue(templates.stream().anyMatch(t -> t.getId().equals(createdTemplate1.getId()) && "Compensation note 1".equals(t.getText())));
        assertTrue(templates.stream().anyMatch(t -> t.getId().equals(createdTemplate2.getId()) && "Withdrawal note 2".equals(t.getText())));

        // 4. Get suggestions by booking type
        mockMvc.perform(get("/api/v1/comment-templates/suggestions?bookingType=COMPENSATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItem("Compensation note 1")));

        // 5. Delete template
        mockMvc.perform(delete("/api/v1/comment-templates/" + createdTemplate1.getId()))
                .andExpect(status().isOk());

        // Verify template 1 is deleted
        final MvcResult listAfterDeleteResult = mockMvc.perform(get("/api/v1/comment-templates"))
                .andExpect(status().isOk())
                .andReturn();

        final List<ResponseCommentTemplate> templatesAfterDelete = objectMapper.readValue(
                listAfterDeleteResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                }
        );
        assertFalse(templatesAfterDelete.stream().anyMatch(t -> t.getId().equals(createdTemplate1.getId())));
        assertTrue(templatesAfterDelete.stream().anyMatch(t -> t.getId().equals(createdTemplate2.getId())));

        // Deleting non-existing template should return 404
        mockMvc.perform(delete("/api/v1/comment-templates/" + createdTemplate1.getId()))
                .andExpect(status().isNotFound());
    }
}
