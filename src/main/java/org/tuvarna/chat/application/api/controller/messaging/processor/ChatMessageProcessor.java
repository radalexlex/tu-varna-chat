package org.tuvarna.chat.application.api.controller.messaging.processor;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.application.api.service.ChatMessageService;
import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;
import org.tuvarna.chat.model.write.dto.enums.AckStatus;
import org.tuvarna.chat.utils.UniUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ChatMessageProcessor {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageProcessor.class);

    @Inject
    ChatMessageService chatMessageService;

    @Incoming("chat-message-request")
    @Outgoing("chat-message-response")
    public Multi<Map<AckStatus, List<ChatMessageOperationalData>>> processMessages(Multi<JsonObject> saveData) {

        Multi<List<JsonObject>> m = saveData
                .group().intoLists().every(Duration.ofMillis(500))
                .select().where(
                        list -> list != null
                                && !list.isEmpty());

        return m.onItem().transformToUniAndMerge(batch ->
                UniUtils.processFailures(saveBatch(batch), log, 2)
                        .onFailure().recoverWithItem(error -> {
                            log.error("Batch failed. Size: {}, FirstMessageId: {}",
                                    batch.size(),
                                    batch.getFirst().getString("clientMessageId"),
                                    error);
                            return Map.of(
                                    AckStatus.SUCCESS, List.of(),
                                    AckStatus.FAILURE, toObjectListFromJsonObject(batch)
                            );
                        })
        );
    }

    private Uni<Map<AckStatus, List<ChatMessageOperationalData>>> saveBatch(List<JsonObject> batch) {

        if (batch.isEmpty()) { // if for some reason filter in the processMessages breaks (0% chance? better be safe than sorry)
            log.warn("Batch is empty, saveBatch method should never be called in this case");
            return Uni.createFrom().item(Map.of(
                    AckStatus.SUCCESS, List.of(),
                    AckStatus.FAILURE, List.of())
            );
        }

        List<ChatMessageOperationalData> saveData = toObjectListFromJsonObject(batch);

        return UniUtils.processFailures(
                Uni.createFrom().item(() ->
                                chatMessageService.addMessages(saveData)
                        )
                        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool()),
                log,
                2
        );
    }

    private List<ChatMessageOperationalData> toObjectListFromJsonObject(List<JsonObject> jsonList) {

        List<ChatMessageOperationalData> saveData = new ArrayList<>(jsonList.size());
        for (JsonObject b : jsonList) {
            saveData.add(b.mapTo(ChatMessageOperationalData.class));
        }

        return saveData;

    }

}
