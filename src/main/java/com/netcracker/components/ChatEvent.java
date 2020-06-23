package com.netcracker.components;

import com.netcracker.dto.MessageDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChatEvent {
    private MessageDTO message;

    public ChatEvent(MessageDTO message) {
        this.message = message;
    }
}
