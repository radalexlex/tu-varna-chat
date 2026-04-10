package org.tuvarna.chat.application.api.service.validation.chain.user;

import org.tuvarna.chat.application.exceptions.base.ApplicationValidationException;
import org.tuvarna.chat.application.exceptions.validation.user.InvalidUserMembershipStatusException;
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

        if (validateOn == null) {
            throw new ApplicationValidationException(
                    "User details cannot be null for permission validation"
            );
        }

        if (validateOn.chatroomId() != this.chatroomId) {
            throw new UserNotChatroomMemberException(
                    "User " + validateOn.userId() +
                            " is not a member of chatroom " + chatroomId +
                            " (actual chatroomId: " + validateOn.chatroomId() + ")"
            );
        }

        if (!validateOn.status().equals(MembershipStatus.ACTIVE.toString())) {
            throw new InvalidUserMembershipStatusException(
                    "User " + validateOn.userId() +
                            " is not active in chatroom " + chatroomId +
                            " (status: " + validateOn.status() + ")"
            );
        }

        return checkNext(validateOn);
    }
}
