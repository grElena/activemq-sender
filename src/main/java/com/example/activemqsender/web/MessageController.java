package com.example.activemqsender.web;

import com.example.activemqsender.activemq.service.MessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MessageController {

   @Autowired
   private MessageSenderService messageSenderService;

    @GetMapping("/start")
    public ResponseEntity<Boolean> publish(){
        messageSenderService.sendMessages(messageSenderService.generateMessages());
        return ResponseEntity.ok(true);
    }

}
