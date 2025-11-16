package com.emailH.emailHelper.controller;


import com.emailH.emailHelper.service.EmailGeneratorService;
import com.emailH.emailHelper.structure.EmailStructure;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmailGeneratorController {
    final EmailGeneratorService emailGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailStructure emailStructure){
        //take the email as parameter and generate response
        String response = emailGeneratorService.generateEmailReply(emailStructure);
        return ResponseEntity.ok(response);
    }
}
