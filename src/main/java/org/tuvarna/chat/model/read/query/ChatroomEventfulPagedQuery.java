package org.tuvarna.chat.model.read.query;

import java.time.Instant;

public sealed interface ChatroomEventfulPagedQuery permits
        ChatroomEventfulPagedQuery.GetFirstPageEventOrdered,
        ChatroomEventfulPagedQuery.GetFollowingPageEventOrdered {

    record GetFirstPageEventOrdered(long userId) implements ChatroomEventfulPagedQuery {
    }

    record GetFollowingPageEventOrdered(long userId,
                                        Instant latestEventTimeOnPage,
                                        Integer latestChatroomIdOnPage,
                                        Long latestChatMessageIdOnPage) implements ChatroomEventfulPagedQuery {
    }

}
