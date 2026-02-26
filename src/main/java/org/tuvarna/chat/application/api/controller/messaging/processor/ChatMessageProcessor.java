package org.tuvarna.chat.application.api.controller.messaging.processor;

import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.tuvarna.chat.application.api.service.ChatMessageService;
import org.tuvarna.chat.model.write.dto.ChatMessageSaveData;

import java.time.Duration;
import java.util.List;

@ApplicationScoped
public class ChatMessageProcessor {

    @Inject
    ChatMessageService chatMessageService;

    @Incoming("chat-message-request")
    @Outgoing("chat-message-response")
    public Multi<Integer> processMessages(Multi<ChatMessageSaveData> saveData) {
        return saveData.group().intoLists().every(Duration.ofMillis(500))
                .onItem().transform(this::saveBatch);
    }

    private Integer saveBatch(List<ChatMessageSaveData> batch){
        if(batch.isEmpty()){
            return null;
        }
        return chatMessageService.addMessages(batch);
    }

}
