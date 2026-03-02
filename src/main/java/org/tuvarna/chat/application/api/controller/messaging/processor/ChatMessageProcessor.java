package org.tuvarna.chat.application.api.controller.messaging.processor;

import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.tuvarna.chat.application.api.service.ChatMessageService;
import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ChatMessageProcessor {

    @Inject
    ChatMessageService chatMessageService;

    @Incoming("chat-message-request")
    @Outgoing("chat-message-response")
    public Multi<Integer> processMessages(Multi<JsonObject> saveData) {
        return saveData.group().intoLists().every(Duration.ofMillis(500))
                .onItem().transform(this::saveBatch);
    }

    private Integer saveBatch(List<JsonObject> batch){
        if(batch.isEmpty()){
            return null;
        }
        List<ChatMessageOperationalData> saveData = new ArrayList<>(batch.size());
        for(JsonObject b : batch){
            saveData.add(b.mapTo(ChatMessageOperationalData.class));
        }

        return chatMessageService.addMessages(saveData);
    }

}
