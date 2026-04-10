package org.tuvarna.chat.application.api.service.validation.chain.user;

import org.tuvarna.chat.application.exceptions.base.ApplicationValidationException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;

public class UserSpecialPermissionValidator extends UserValidatorBaseHandler {

    @Override
    public boolean handle(ChatroomUserDetails validateOn) {

        if (validateOn == null) {
            throw new ApplicationValidationException(
                    "User details cannot be null for permission validation"
            );
        }

        if (validateOn.role().equals(ChatroomRole.ADMIN.toString())
                || validateOn.role().equals(ChatroomRole.SUPERUSER.toString())) {
            return checkNext(validateOn);
        }

        throw new UserNotAllowedException(
                "User " + validateOn.userId() +
                        " does not have sufficient permissions " +
                        "(role: " + validateOn.role() + ")"
        );

    }
}
