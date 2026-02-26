package org.tuvarna.chat.application.api.service.validation.chain.user;

import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;

public class UserSpecialPermissionValidator extends UserValidatorBaseHandler {

    @Override
    public boolean handle(ChatroomUserDetails validateOn) {

        if (validateOn.role().equals(ChatroomRole.ADMIN.toString())
                || validateOn.role().equals(ChatroomRole.SUPERUSER.toString())) {
            return checkNext(validateOn);
        }

        throw new UserNotAllowedException("");

    }
}
