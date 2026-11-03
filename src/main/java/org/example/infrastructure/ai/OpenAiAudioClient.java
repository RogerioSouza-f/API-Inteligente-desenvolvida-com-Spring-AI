package org.example.infrastructure.ai;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.audio.speech.SpeechModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class OpenAiAudioClient {
    private static final String API_KEY_NOT_CONFIGURED = "not-configured";

    private final String apiKey;
    private final OpenAiAudioTranscriptionModel transcriptionModel;
    private final SpeechModel speechModel;

    public OpenAiAudioClient(
            @Value("${spring.ai.openai.api-key:not-configured}") String apiKey,
            OpenAiAudioTranscriptionModel transcriptionModel,
            SpeechModel speechModel) {
        this.apiKey = apiKey;
        this.transcriptionModel = transcriptionModel;
        this.speechModel = speechModel;
    }

    public String transcribe(byte[] audio, String filename) {
        requireApiKey();
        ByteArrayResource resource = new ByteArrayResource(audio) {
            @Override
            public String getFilename() {
                return StringUtils.hasText(filename) ? filename : "audio-upload";
            }
        };
        String transcript = transcriptionModel.call(new AudioTranscriptionPrompt(resource))
                .getResult()
                .getOutput();
        if (!StringUtils.hasText(transcript)) {
            throw new AudioProviderException("O serviço de transcrição não retornou texto.");
        }
        return transcript;
    }

    public byte[] synthesize(String text) {
        requireApiKey();
        byte[] audio = speechModel.call(text);
        if (audio == null || audio.length == 0) {
            throw new AudioProviderException("O serviço de voz não retornou áudio.");
        }
        return audio;
    }

    private void requireApiKey() {
        if (!StringUtils.hasText(apiKey) || API_KEY_NOT_CONFIGURED.equals(apiKey)) {
            throw new AudioConfigurationException("Configure OPENAI_API_KEY para usar o endpoint de áudio.");
        }
    }
}
