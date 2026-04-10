package org.tuvarna.chat.application.api.controller.client;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;

@RegisterRestClient
public interface ChatWsClient {

    @POST
    @Path("/notify-error")
    Response notifyError(ChatMessageOperationalData messageErrored);

}
