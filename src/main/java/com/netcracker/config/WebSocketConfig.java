package com.netcracker.config;

import com.netcracker.components.ChatEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

@Configuration
public class WebSocketConfig {
    @Bean
    UnicastProcessor<ChatEvent> publisher() {
        return UnicastProcessor.create();
    }

    @Bean
    Flux<ChatEvent> messages(UnicastProcessor<ChatEvent> publisher) {
        return publisher.replay(0).autoConnect();
    }
}
