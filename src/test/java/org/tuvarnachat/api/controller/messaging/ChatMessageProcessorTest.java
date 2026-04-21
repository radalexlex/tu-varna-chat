package org.tuvarnachat.api.controller.messaging;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.vertx.core.json.JsonObject;
import org.tuvarna.chat.application.api.controller.messaging.processor.ChatMessageProcessor;
import org.tuvarna.chat.application.api.service.ChatMessageService;
import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;
import org.tuvarna.chat.model.write.dto.enums.AckStatus;

@QuarkusTest
class ChatMessageProcessorTest {

    @Inject
    ChatMessageProcessor processor;

    @InjectMock
    ChatMessageService chatMessageService;

    private JsonObject createMessage(String id) {
        return new JsonObject()
                .put("clientMessageId", id)
                .put("chatroomId", 1)
                .put("senderId", 10L)
                .put("senderUserId", 20L)
                .put("content", "hello")
                .put("creationTimestamp", "2025-01-01T00:00:00Z");
    }

    @Test
    void shouldProcessMessagesSuccessfully() {
        JsonObject msg1 = createMessage("m1");
        JsonObject msg2 = createMessage("m2");

        Map<AckStatus, List<ChatMessageOperationalData>> mockResponse =
                Map.of(
                        AckStatus.SUCCESS, List.of(),
                        AckStatus.FAILURE, List.of()
                );

        when(chatMessageService.addMessages(anyList()))
                .thenReturn(mockResponse);

        Multi<JsonObject> input = Multi.createFrom().items(msg1, msg2);

        var result = processor.processMessages(input);

        AssertSubscriber<Map<AckStatus, List<ChatMessageOperationalData>>> subscriber =
                result.subscribe().withSubscriber(AssertSubscriber.create(Long.MAX_VALUE));

        subscriber.awaitItems(1, Duration.ofMillis(3000));

        Map<AckStatus, List<ChatMessageOperationalData>> output = subscriber.getItems().get(0);

        assertNotNull(output);
        assertTrue(output.containsKey(AckStatus.SUCCESS));
        assertTrue(output.containsKey(AckStatus.FAILURE));

        verify(chatMessageService, atLeastOnce()).addMessages(anyList());
    }

    @Test
    void shouldHandleServiceFailureAndReturnFallback() {
        JsonObject msg = createMessage("fail-1");

        when(chatMessageService.addMessages(anyList()))
                .thenThrow(new RuntimeException("DB failure"));

        Multi<JsonObject> input = Multi.createFrom().item(msg);

        var result = processor.processMessages(input);

        AssertSubscriber<Map<AckStatus, List<ChatMessageOperationalData>>> subscriber =
                result.subscribe().withSubscriber(AssertSubscriber.create(Long.MAX_VALUE));

        subscriber.awaitItems(1, Duration.ofMillis(3000));

        Map<AckStatus, List<ChatMessageOperationalData>> output = subscriber.getItems().get(0);

        assertNotNull(output);
        assertTrue(output.get(AckStatus.SUCCESS).isEmpty());
        assertFalse(output.get(AckStatus.FAILURE).isEmpty());
    }

    @Test
    void shouldReturnNoItemsWhenInputEmpty() {
        Multi<JsonObject> input = Multi.createFrom().empty();

        var result = processor.processMessages(input);

        AssertSubscriber<Map<AckStatus, List<ChatMessageOperationalData>>> subscriber =
                result.subscribe().withSubscriber(AssertSubscriber.create(Long.MAX_VALUE));

        subscriber.awaitCompletion(Duration.ofMillis(2000));

        assertTrue(subscriber.getItems().isEmpty());
    }
}