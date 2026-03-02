package org.tuvarna.chat.application.api.service.validation.chain.user;

import org.tuvarna.chat.application.exceptions.base.ApplicationRuleViolationException;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;

public class UserSameChatroomValidator extends UserValidatorBaseHandler {

    private final ChatroomUserDetails comparedTo;

    public UserSameChatroomValidator(ChatroomUserDetails comparedTo) {
        this.comparedTo = comparedTo;
    }

    @Override
    public boolean handle(ChatroomUserDetails validateOn) {
        if(this.comparedTo == null || validateOn == null) {
            return false;
        }
        if(this.comparedTo.chatroomId() != validateOn.chatroomId()) {
            throw new ApplicationRuleViolationException("");
        } else return true;
    }
}
