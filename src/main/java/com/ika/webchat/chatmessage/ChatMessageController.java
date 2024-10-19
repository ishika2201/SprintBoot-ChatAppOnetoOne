package com.ika.webchat.chatmessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    public void processMessage(
            @Payload ChatMessage chatMessage
    ){
        ChatMessage savedMsg = chatMessageService.saveChatMessage(chatMessage);
        // 'recipient/queue/messages
        // recipients queue will be generated and put into everytime and recipient will have to subscribe to get it
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(),
                "/queue/messages",
                ChatNotification.builder()
                        .chatId(savedMsg.getChatId())
                        .senderId(savedMsg.getSenderId())
                        .recipientId(savedMsg.getRecipientId())
                        .content(savedMsg.getContent())
                        .build()
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(
            @PathVariable("senderId") String senderId,
            @PathVariable("recipientId") String recipientId
    )
    {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }
}
