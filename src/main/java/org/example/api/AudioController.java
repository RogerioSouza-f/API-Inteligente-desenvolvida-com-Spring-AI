package org.example.api;

import org.example.application.VoiceBudgetService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/audio")
public class AudioController {
    private final VoiceBudgetService voiceBudgetService;

    public AudioController(VoiceBudgetService voiceBudgetService) {
        this.voiceBudgetService = voiceBudgetService;
    }

    @GetMapping("/transactions")
    public Map<String, Object> audioInstructions() {
        return Map.of(
                "message", "Para enviar um comando por áudio, use POST com multipart/form-data.",
                "method", "POST",
                "url", "/api/audio/transactions",
                "field", "file",
                "contentType", "multipart/form-data");
    }

    @PostMapping(value = "/transactions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> handle(@RequestPart("file") MultipartFile file) {
        VoiceBudgetService.VoiceResponse response = voiceBudgetService.handle(file);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"resposta.mp3\"")
                .header("X-Transcript", URLEncoder.encode(response.transcript(), StandardCharsets.UTF_8))
                .body(response.mp3());
    }
}
