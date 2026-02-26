package org.tuvarna.chat.application.api.service.validation.chain.user;

import org.tuvarna.chat.application.api.service.validation.ValidationHandler;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;

public abstract class UserValidatorBaseHandler implements ValidationHandler<ChatroomUserDetails> {

    private ValidationHandler<ChatroomUserDetails> nextValidator;

    @SafeVarargs
    public static ValidationHandler<ChatroomUserDetails> linkValidators(ValidationHandler<ChatroomUserDetails> first,
                                                                        ValidationHandler<ChatroomUserDetails>... subsequentValidators) {

        ValidationHandler<ChatroomUserDetails> head = first;

        for (ValidationHandler<ChatroomUserDetails> validator : subsequentValidators) {
            head.setNext(validator);
            head = validator;
        }

        return first;
    }

    @Override
    public void setNext(ValidationHandler<ChatroomUserDetails> nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public abstract boolean handle(ChatroomUserDetails validateOn);

    protected boolean checkNext(ChatroomUserDetails validateOn) {

        return nextValidator == null || nextValidator.handle(validateOn);

    }
}
