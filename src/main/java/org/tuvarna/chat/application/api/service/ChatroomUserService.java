package org.tuvarna.chat.application.api.service;

import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.write.dto.ChatroomUsersSaveData;

import java.time.Instant;

public interface ChatroomUserService {
    ContentPage<ChatroomUserDetails> getUsers(long requestingUserId,
                                              int chatroomId,
                                              Instant oldestAdditionTimestamp,
                                              Integer oldestAdditionId);

    int addUsers(long requestingUserId,
                 ChatroomUsersSaveData saveData);

    int changeUserRole(long requestingUserId,
                       int chatroomId,
                       long affectedUserId,
                       String updatedRole);

    int changeUserMembershipStatus(long requestingUserId,
                                   int chatroomId,
                                   long affectedUserId,
                                   String updatedStatus);

    int addFirstChatroomUser(long userId, int chatroomId);

    ChatroomUserDetails getUserDetailsForSelf(long requestingUserId, int chatroomId);

    ChatroomUserDetails getUserDetailsForRequester(long requestingUserId, long userId, int chatroomId);

    int updateLastReadStatus(long requestingUserId, int chatroomId, long lastReadMessage);
}
