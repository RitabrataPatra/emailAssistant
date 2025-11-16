package com.emailH.emailHelper.service;


import com.emailH.emailHelper.structure.EmailStructure;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmailGeneratorService {
    private final WebClient webClient;
    private final String apiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder,
                                 @Value("${gemini.api.key}") String geminiApiKey,
                                 @Value("${gemini.api.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = geminiApiKey;
    }

    public String generateEmailReply(EmailStructure emailStructure) {
        //Build Prompt
        String prompt = buildPrompt(emailStructure);

        //Prepare raw json body
        String requestBody = String.format("""
                {
                    "contents": [
                      {
                        "parts": [
                          {
                            "text": "%s"
                          }
                        ]
                      }
                    ]
                  }""" , prompt);

        //Send Request
        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-2.5-flash:generateContent")
                        .build())
                .header("x-goog-api-key" , apiKey)
                .header("Content-Type" ,  "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        //Extract Response
        return extractResponseContent(response);


    }

    //helper functions
    private String extractResponseContent(String response) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            return  root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

//            String extraModelResponse = root.path("modelVersion").asText();


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildPrompt(EmailStructure emailStructure) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Generate a professional reply within 80 words for the following email");

        if(emailStructure.getTone() != null && !emailStructure.getTone().isEmpty()){
            prompt.append("Use a").append(emailStructure.getTone()).append(" tone.");
        }

        prompt.append("Original Email : \n").append(emailStructure.getEmailContent());

        return prompt.toString();
    }
}
