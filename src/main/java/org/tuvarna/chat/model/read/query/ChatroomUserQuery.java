package org.tuvarna.chat.model.read.query;

public sealed interface ChatroomUserQuery permits ChatroomUserQuery.GetChatroomUserDetails{

    record GetChatroomUserDetails(long userId) implements ChatroomUserQuery {
    }

}
