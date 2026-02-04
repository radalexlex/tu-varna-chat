package org.tuvarna.chat.model.read.query;

public sealed interface ChatroomUserQuery permits {
    record GetChatroomUser(int )
}
