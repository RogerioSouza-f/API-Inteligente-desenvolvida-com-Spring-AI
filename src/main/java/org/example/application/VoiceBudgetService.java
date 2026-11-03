package org.example.application;

import org.example.infrastructure.ai.OpenAiAudioClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class VoiceBudgetService {
    private final OpenAiAudioClient audioClient;
    private final ChatClient chatClient;
    private final TransactionTools transactionTools;

    public VoiceBudgetService(OpenAiAudioClient audioClient, ChatClient.Builder chatClientBuilder,
                              TransactionTools transactionTools) {
        this.audioClient = audioClient;
        this.chatClient = chatClientBuilder.build();
        this.transactionTools = transactionTools;
    }

    public VoiceResponse handle(MultipartFile audio) {
        if (audio == null || audio.isEmpty()) {
            throw new IllegalArgumentException("Envie um arquivo de áudio não vazio.");
        }
        try {
            String transcript = audioClient.transcribe(audio.getBytes(), audio.getOriginalFilename());
            String answer = chatClient.prompt()
                    .system("""
                            Você é um assistente de orçamento pessoal em português.
                            Responda com clareza e concisão. Use as ferramentas para registrar ou consultar
                            transações somente conforme solicitado pelo usuário. Nunca invente dados.
                            Se faltarem valor, descrição, categoria ou tipo para registrar uma transação,
                            não a registre e peça a informação que falta.
                            """)
                    .user(transcript)
                    .tools(transactionTools)
                    .call()
                    .content();
            if (answer == null || answer.isBlank()) {
                throw new IllegalStateException("O assistente não retornou uma resposta.");
            }
            return new VoiceResponse(transcript, answer, audioClient.synthesize(answer));
        } catch (IOException exception) {
            throw new IllegalArgumentException("Não foi possível ler o arquivo de áudio.");
        }
    }

    public record VoiceResponse(String transcript, String answer, byte[] mp3) {
    }
}
