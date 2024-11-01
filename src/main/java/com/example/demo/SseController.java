package com.example.sse;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Iterator;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@EnableScheduling
public class SseController {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/sse")
    public SseEmitter streamSseMvc() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
            
        emitter.onCompletion(() -> {
            System.out.println("Emitter Complete");
            emitters.remove(emitter);
        });
        emitter.onTimeout(() -> System.out.println("Timed out"));
        emitter.onError((ex) -> System.out.println("Error BEEEE"));
        emitters.add(emitter);

        return emitter;
    }

    @Scheduled(fixedRate = 100)
    public void sendUpdates() {
        String message = "Current Time: " + System.currentTimeMillis();

        Iterator<SseEmitter> iterator = emitters.iterator();
        iterator.forEachRemaining(emitter -> {
            try {
                emitter.send(message);
            } catch (Exception e) {
                // Doesn't catch the IOException
            }
        });
    }
}
