package org.tuvarna.chat.model.read.query;

import org.tuvarna.chat.model.read.query.ChatroomQuery.GetChatroomOverview;

public sealed interface ChatroomQuery permits
        ChatroomQuery.GetChatroomOverview {

    record GetChatroomOverview(int chatroomId) implements ChatroomQuery {
    }

}
