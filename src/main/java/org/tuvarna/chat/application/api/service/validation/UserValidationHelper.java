package org.tuvarna.chat.application.api.service.validation;

import jakarta.enterprise.context.ApplicationScoped;
import org.tuvarna.chat.application.api.service.validation.chain.user.UserValidatorBaseHandler;
import org.tuvarna.chat.application.api.service.validation.chain.user.UserChatroomPresenceValidator;
import org.tuvarna.chat.application.api.service.validation.chain.user.UserSameChatroomValidator;
import org.tuvarna.chat.application.api.service.validation.chain.user.UserSpecialPermissionValidator;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;

@ApplicationScoped
public class UserValidationHelper {

    public static ValidationHandler<ChatroomUserDetails> getSpecialUserValidator(int chatroomId) {
        return UserValidatorBaseHandler.linkValidators(
                new UserChatroomPresenceValidator(chatroomId),
                new UserSpecialPermissionValidator());
    }

    public static ValidationHandler<ChatroomUserDetails> getUserChatroomPresenceValidator(int chatroomId) {
        return new UserChatroomPresenceValidator(chatroomId);
    }

    public static ValidationHandler<ChatroomUserDetails> getSpecialPermissionValidator() {
        return new UserSpecialPermissionValidator();
    }

    public static ValidationHandler<ChatroomUserDetails> getSameChatroomUsersValidator(ChatroomUserDetails comparedTo) {
        return new UserSameChatroomValidator(comparedTo);
    }


}
