package org.tuvarna.chat.model.read.query;

public sealed interface ChatroomQuery permits
        ChatroomQuery.GetChatroomOverview {

    record GetChatroomOverview(int chatroomId) implements ChatroomQuery {
    }

}
