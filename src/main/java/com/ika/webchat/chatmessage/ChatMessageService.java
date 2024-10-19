package com.ika.webchat.chatmessage;

import com.ika.webchat.chatroom.ChatRoomService;
import com.ika.webchat.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
        var ChatId = chatRoomService
                .getChatRoomId(
                        chatMessage.getSenderId(),
                        chatMessage.getRecipientId(),
                        true
                        )
                .orElseThrow(); //create own dedicated exception
        chatMessage.setChatId(ChatId);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(
            String senderId, String recipientId
    ) {
        var chatId = chatRoomService
                .getChatRoomId(
                        senderId,
                        recipientId,
                        false
                );
        return chatId.map(chatMessageRepository::findByChatId)
                .orElse(new ArrayList<>());
    }
}
