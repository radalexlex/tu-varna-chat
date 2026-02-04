package org.tuvarna.chat.model.read.query;

import java.time.Instant;

public sealed interface ChatMessagePagedQuery permits
        ChatMessagePagedQuery.GetFollowingPage,
        ChatMessagePagedQuery.GetFirstPage {

    record GetFirstPage(int chatroomId) implements ChatMessagePagedQuery {
    }

    record GetFollowingPage(int chatroomId,
                            Instant oldestTimestamp,
                            int oldestId) implements ChatMessagePagedQuery {
    }

}
