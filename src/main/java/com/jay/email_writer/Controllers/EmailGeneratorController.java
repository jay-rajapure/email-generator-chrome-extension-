package com.jay.email_writer.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jay.email_writer.model.EmailRequest;
import com.jay.email_writer.service.EmailGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
public class EmailGeneratorController {
    @Autowired
    EmailGeneratorService emailGeneratorService;
    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest) throws JsonProcessingException {
       String response= emailGeneratorService.generateEmailReply(emailRequest);

        return  ResponseEntity.ok(response);
    }

}
