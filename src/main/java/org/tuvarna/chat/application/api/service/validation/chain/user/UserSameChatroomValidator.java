package org.tuvarna.chat.application.api.service.validation.chain.user;

import org.tuvarna.chat.application.exceptions.base.ApplicationValidationException;
import org.tuvarna.chat.application.exceptions.validation.user.InvalidUserMembershipStatusException;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;

public class UserSameChatroomValidator extends UserValidatorBaseHandler {

    private final ChatroomUserDetails comparedTo;

    public UserSameChatroomValidator(ChatroomUserDetails comparedTo) {
        this.comparedTo = comparedTo;
    }

    @Override
    public boolean handle(ChatroomUserDetails validateOn) {

        if (this.comparedTo == null || validateOn == null) {
            throw new ApplicationValidationException(
                    "Cannot compare chatroom users: one or both user details are null"
            );
        }

        if (this.comparedTo.chatroomId() != validateOn.chatroomId()) {
            throw new InvalidUserMembershipStatusException(
                    "Users belong to different chatrooms: " +
                            "user " + validateOn.userId() + " -> " + validateOn.chatroomId() +
                            ", comparedTo user " + comparedTo.userId() + " -> " + comparedTo.chatroomId()
            );
        }

        return checkNext(validateOn);
    }
}
