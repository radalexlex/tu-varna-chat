package org.tuvarna.chat.application.api.service.validation.chain.user;

import org.tuvarna.chat.application.exceptions.conflict.user.UserAlreadyDeletedException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotChatroomMemberException;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;

public class UserChatroomPresenceValidator extends UserValidatorBaseHandler {

    private final int chatroomId;

    public UserChatroomPresenceValidator(int chatroomId) {
        this.chatroomId = chatroomId;
    }

    @Override
    public boolean handle(ChatroomUserDetails validateOn) {

        if (validateOn.chatroomId() != this.chatroomId) {
            throw new UserNotChatroomMemberException("");
        }

        if (!validateOn.status().equals(MembershipStatus.ACTIVE.toString())) {
            throw new UserAlreadyDeletedException("");
        }

        return checkNext(validateOn);

    }
}
