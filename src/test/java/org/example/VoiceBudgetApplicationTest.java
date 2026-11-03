package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:voice-budget-test;DB_CLOSE_DELAY=-1",
        "spring.ai.openai.api-key=not-configured"
})
@AutoConfigureMockMvc
class VoiceBudgetApplicationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void showsAvailableEndpointsAtTheRoot() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Voice Budget API"))
                .andExpect(jsonPath("$.endpoints.transactions").value("/api/transactions"))
                .andExpect(jsonPath("$.endpoints.audio").value("/api/audio/transactions"));
    }

    @Test
    void returnsNotFoundForMissingStaticResources() throws Exception {
        mockMvc.perform(get("/favicon.ico"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("O recurso solicitado não foi encontrado."));
    }

    @Test
    void explainsHowToSendAudioWhenEndpointIsOpenedInBrowser() throws Exception {
        mockMvc.perform(get("/api/audio/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("POST"))
                .andExpect(jsonPath("$.field").value("file"))
                .andExpect(jsonPath("$.contentType").value("multipart/form-data"));
    }

    @Test
    void persistsTransactionsAndReportsMissingAudioCredentials() throws Exception {
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":32.40,"description":"mercado","category":"alimentação","type":"EXPENSE"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("mercado"));

        mockMvc.perform(multipart("/api/audio/transactions")
                        .file(new MockMultipartFile("file", "comando.mp3",
                                "audio/mpeg", new byte[]{1, 2, 3})))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value(
                        "Configure OPENAI_API_KEY para usar o endpoint de áudio."));
    }
}
