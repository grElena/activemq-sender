package com.example.activemqsender.activemq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.jms.Queue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Component
@Slf4j
public class MessageSenderService {
    @Autowired
    private Queue queue;

    @Autowired
    private JmsTemplate jmsTemplate;
    ExecutorService service = Executors.newFixedThreadPool(10);
    CompletionService<String> ecs = new ExecutorCompletionService<>(service);

    public List<String> generateMessages() {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            res.add("Message " + i);
        }
        return res;
    }

    public void sendMessages(List<String> messages) {
        try {
            for (String s : messages) {
                ecs.submit(() -> {
                    jmsTemplate.convertAndSend(queue, s);
                    return s;
                });
            }
        } catch (Exception e){
            log.error("Error sending a message {}", e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (service != null) {
                if (!service.isShutdown()) {
                    service.shutdown();
                }
                service.awaitTermination(60, TimeUnit.SECONDS);
                service.shutdownNow();
            }
        } catch (Exception ex) {
            log.error("Error releasing a resource {}", ex.getMessage());
        }
    }
}
