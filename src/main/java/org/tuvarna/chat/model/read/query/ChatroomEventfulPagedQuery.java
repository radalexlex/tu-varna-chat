package org.tuvarna.chat.model.read.query;

import java.time.Instant;

public sealed interface ChatroomEventfulPagedQuery permits
        ChatroomEventfulPagedQuery.GetFirstPageEventOrdered,
        ChatroomEventfulPagedQuery.GetFollowingPageEventOrdered {

    record GetFirstPageEventOrdered(int chatroomUserId) implements ChatroomEventfulPagedQuery {}

    record GetFollowingPageEventOrdered(int chatroomUserId,
                                        Instant latestEventTimeOnPage,
                                        Integer latestChatroomIdOnPage,
                                        Integer latestChatMessageIdOnPage) implements ChatroomEventfulPagedQuery {}

}
