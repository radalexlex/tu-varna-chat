package org.tuvarna.chat.model.read.query;

import java.time.Instant;

public sealed interface ChatroomUserPagedQuery permits
        ChatroomUserPagedQuery.GetFirstPage,
        ChatroomUserPagedQuery.GetFollowingPage  {

    record GetFirstPage(int chatroomId,
                        boolean extendedPermissionGiven) implements ChatroomUserPagedQuery {
    }

    record GetFollowingPage(int chatroomId,
                            Instant oldestAdditionTimestamp,
                            int oldestAdditionId,
                            boolean extendedPermissionGiven) implements ChatroomUserPagedQuery {
    }

//    record GetChatroomUser(int userId)

}
