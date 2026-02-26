package org.tuvarna.chat.application.api.service.validation;

public interface ValidationHandler<E> {

    void setNext(ValidationHandler<E> nextValidator);

    boolean handle(E validateOn);

}
