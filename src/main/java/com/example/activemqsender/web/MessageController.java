package com.example.activemqsender.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Queue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private Queue queue;
    @Autowired
    private JmsTemplate jmsTemplate;

    @GetMapping("/start")
    public ResponseEntity<Boolean> publish(){
        ExecutorService service = null;
        List<String> strs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strs.add("Message " + i);
        }
        service = Executors.newFixedThreadPool(strs.size());
        CompletionService<String> ecs = new ExecutorCompletionService<>(service);
            try {
                for (String s : strs) {
                    ecs.submit(() -> {
                        jmsTemplate.convertAndSend(queue, s);
                        return s;
                    });
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
            try {
                if (service != null) {
                    if (!service.isShutdown()) {
                        service.shutdown();
                    }
                    service.awaitTermination(60, TimeUnit.SECONDS);
                    service.shutdownNow();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return ResponseEntity.ok(true);
    }

}
