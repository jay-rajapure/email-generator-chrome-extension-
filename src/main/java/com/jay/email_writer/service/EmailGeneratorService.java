package com.jay.email_writer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jay.email_writer.model.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailGeneratorService {

    private final   WebClient webClient;
    public EmailGeneratorService(WebClient.Builder WebClientBuilder) {
        this.webClient = WebClient.builder().build();
    }

    @Value("${gemini.api.url}")
    private  String geminiUrl;
    @Value("${gemini.api.key}")
    private String geminiKey;


    public String generateEmailReply(EmailRequest emailRequest) throws JsonProcessingException {
        //build the prompt
          String prompt = this.buildPrompt(emailRequest);
        //craft a request
        Map<String,Object>  textMap = Map.of("text",prompt);
        Map<String,Object>  partsMap = Map.of("parts", List.of(textMap));
        Map<String,Object>  contentsMap = Map.of("contents",List.of(partsMap));


        //do request and get response
        String response = webClient.post()
                .uri(geminiUrl+geminiKey)
                .header("Content-type","application/json")
                .bodyValue(contentsMap)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        //return response
        return  ExtractResponseContent(response);
    }

    private String ExtractResponseContent(String response) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode jsonResponse = jsonMapper.readTree(response);
        return jsonResponse.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text").asText();

    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate  a Professional email reply for the following email content . Please Dont Generate Subject Line");
        if(emailRequest.getTone()!= null && emailRequest.getTone().isEmpty())
        {
            prompt.append("Use a ").append(emailRequest.getTone()).append("tone");
        }
        prompt.append("\n Orignal Email \n ").append(emailRequest.getEmailContent());
        return  prompt.toString();


    }
}