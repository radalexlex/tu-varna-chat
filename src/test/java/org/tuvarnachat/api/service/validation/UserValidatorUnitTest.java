package org.tuvarnachat.api.service.validation;

import org.junit.jupiter.api.Test;
import org.tuvarna.chat.application.api.service.validation.ValidationHandler;
import org.tuvarna.chat.application.api.service.validation.chain.user.UserChatroomPresenceValidator;
import org.tuvarna.chat.application.api.service.validation.chain.user.UserSameChatroomValidator;
import org.tuvarna.chat.application.api.service.validation.chain.user.UserSpecialPermissionValidator;
import org.tuvarna.chat.application.api.service.validation.chain.user.UserValidatorBaseHandler;
import org.tuvarna.chat.application.exceptions.base.ApplicationValidationException;
import org.tuvarna.chat.application.exceptions.validation.user.InvalidUserMembershipStatusException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotChatroomMemberException;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserValidatorUnitTest {

    private ChatroomUserDetails validUser(int chatroomId) {
        return new ChatroomUserDetails(
                1L,
                chatroomId,
                100L,
                ChatroomRole.ADMIN.toString(),
                MembershipStatus.ACTIVE.toString(),
                "2026-01-01T00:00:00Z",
                10L
        );
    }

    private ChatroomUserDetails userWithRole(String role) {
        return new ChatroomUserDetails(
                1L,
                10,
                100L,
                role,
                MembershipStatus.ACTIVE.toString(),
                "2026-01-01T00:00:00Z",
                10L
        );
    }

    private ChatroomUserDetails userWithStatus(String status) {
        return new ChatroomUserDetails(
                1L,
                10,
                100L,
                ChatroomRole.ADMIN.toString(),
                status,
                "2026-01-01T00:00:00Z",
                10L
        );
    }

    @Test
    void presenceValidator_ok() {
        UserChatroomPresenceValidator validator =
                new UserChatroomPresenceValidator(10);

        assertTrue(validator.handle(validUser(10)));
    }

    @Test
    void presenceValidator_nullUser() {
        UserChatroomPresenceValidator validator =
                new UserChatroomPresenceValidator(10);

        assertThrows(ApplicationValidationException.class,
                () -> validator.handle(null));
    }

    @Test
    void presenceValidator_wrongChatroom() {
        UserChatroomPresenceValidator validator =
                new UserChatroomPresenceValidator(10);

        ChatroomUserDetails user = validUser(99);

        assertThrows(UserNotChatroomMemberException.class,
                () -> validator.handle(user));
    }

    @Test
    void presenceValidator_inactiveUser() {
        UserChatroomPresenceValidator validator =
                new UserChatroomPresenceValidator(10);

        ChatroomUserDetails user = userWithStatus("LEFT");

        assertThrows(InvalidUserMembershipStatusException.class,
                () -> validator.handle(user));
    }

    @Test
    void presenceValidator_chainNext_called() {
        UserChatroomPresenceValidator validator =
                new UserChatroomPresenceValidator(10);

        ValidationHandler<ChatroomUserDetails> next = mock(ValidationHandler.class);
        when(next.handle(any())).thenReturn(true);

        validator.setNext(next);

        assertTrue(validator.handle(validUser(10)));
        verify(next).handle(any());
    }


    @Test
    void sameChatroom_ok() {
        ChatroomUserDetails base = validUser(10);

        UserSameChatroomValidator validator =
                new UserSameChatroomValidator(base);

        assertTrue(validator.handle(validUser(10)));
    }

    @Test
    void sameChatroom_nullComparedTo() {
        UserSameChatroomValidator validator =
                new UserSameChatroomValidator(null);

        assertThrows(ApplicationValidationException.class,
                () -> validator.handle(validUser(10)));
    }

    @Test
    void sameChatroom_nullValidateOn() {
        UserSameChatroomValidator validator =
                new UserSameChatroomValidator(validUser(10));

        assertThrows(ApplicationValidationException.class,
                () -> validator.handle(null));
    }

    @Test
    void sameChatroom_differentChatrooms() {
        ChatroomUserDetails base = validUser(10);

        UserSameChatroomValidator validator =
                new UserSameChatroomValidator(base);

        ChatroomUserDetails other = validUser(20);

        assertThrows(InvalidUserMembershipStatusException.class,
                () -> validator.handle(other));
    }

    @Test
    void sameChatroom_chainNext_called() {
        ChatroomUserDetails base = validUser(10);

        UserSameChatroomValidator validator =
                new UserSameChatroomValidator(base);

        ValidationHandler<ChatroomUserDetails> next = mock(ValidationHandler.class);
        when(next.handle(any())).thenReturn(true);

        validator.setNext(next);

        assertTrue(validator.handle(validUser(10)));
        verify(next).handle(any());
    }

    @Test
    void specialPermission_admin_ok() {
        UserSpecialPermissionValidator validator =
                new UserSpecialPermissionValidator();

        assertTrue(validator.handle(userWithRole("ADMIN")));
    }

    @Test
    void specialPermission_superuser_ok() {
        UserSpecialPermissionValidator validator =
                new UserSpecialPermissionValidator();

        assertTrue(validator.handle(userWithRole("SUPERUSER")));
    }

    @Test
    void specialPermission_member_denied() {
        UserSpecialPermissionValidator validator =
                new UserSpecialPermissionValidator();

        assertThrows(UserNotAllowedException.class,
                () -> validator.handle(userWithRole("MEMBER")));
    }

    @Test
    void specialPermission_nullUser() {
        UserSpecialPermissionValidator validator =
                new UserSpecialPermissionValidator();

        assertThrows(ApplicationValidationException.class,
                () -> validator.handle(null));
    }

    @Test
    void specialPermission_chainNext_called() {
        UserSpecialPermissionValidator validator =
                new UserSpecialPermissionValidator();

        ValidationHandler<ChatroomUserDetails> next = mock(ValidationHandler.class);
        when(next.handle(any())).thenReturn(true);

        validator.setNext(next);

        assertTrue(validator.handle(userWithRole("ADMIN")));
        verify(next).handle(any());
    }

    @Test
    void chain_multipleValidators_allPass() {
        ChatroomUserDetails user = validUser(10);

        ValidationHandler<ChatroomUserDetails> chain =
                UserValidatorBaseHandler.linkValidators(
                        new UserChatroomPresenceValidator(10),
                        new UserSameChatroomValidator(user),
                        new UserSpecialPermissionValidator()
                );

        assertTrue(chain.handle(user));
    }

    @Test
    void chain_stopsOnFailure() {
        ChatroomUserDetails user = userWithRole("MEMBER");

        ValidationHandler<ChatroomUserDetails> next = mock(ValidationHandler.class);

        UserSpecialPermissionValidator validator =
                new UserSpecialPermissionValidator();
        validator.setNext(next);

        assertThrows(UserNotAllowedException.class,
                () -> validator.handle(user));

        verify(next, never()).handle(any());
    }

    @Test
    void checkNext_returnsTrue_whenNoNext() {
        UserSpecialPermissionValidator validator =
                new UserSpecialPermissionValidator();

        assertTrue(validator.handle(userWithRole("ADMIN")));
    }

    @Test
    void linkValidators_orderIsCorrect() {
        ValidationHandler<ChatroomUserDetails> v1 = mock(ValidationHandler.class);
        ValidationHandler<ChatroomUserDetails> v2 = mock(ValidationHandler.class);
        ValidationHandler<ChatroomUserDetails> v3 = mock(ValidationHandler.class);

        when(v1.handle(any())).thenAnswer(invocation -> {
            v2.handle(invocation.getArgument(0));
            return true;
        });

        when(v2.handle(any())).thenAnswer(invocation -> {
            v3.handle(invocation.getArgument(0));
            return true;
        });

        when(v3.handle(any())).thenReturn(true);

        ValidationHandler<ChatroomUserDetails> chain =
                UserValidatorBaseHandler.linkValidators(v1, v2, v3);

        chain.handle(validUser(10));

        verify(v1).handle(any());
        verify(v2).handle(any());
        verify(v3).handle(any());
    }

    @Test
    void presenceValidator_statusCaseSensitiveFails() {
        UserChatroomPresenceValidator validator =
                new UserChatroomPresenceValidator(10);

        ChatroomUserDetails user = userWithStatus("active");

        assertThrows(InvalidUserMembershipStatusException.class,
                () -> validator.handle(user));
    }

    @Test
    void specialPermission_roleCaseSensitiveFails() {
        UserSpecialPermissionValidator validator =
                new UserSpecialPermissionValidator();

        ChatroomUserDetails user = userWithRole("admin");

        assertThrows(UserNotAllowedException.class,
                () -> validator.handle(user));
    }

    @Test
    void sameChatroom_largeIdsStillWorks() {
        ChatroomUserDetails base = new ChatroomUserDetails(
                1L, Integer.MAX_VALUE, Long.MAX_VALUE,
                "ADMIN", "ACTIVE", "", null
        );

        UserSameChatroomValidator validator =
                new UserSameChatroomValidator(base);

        assertTrue(validator.handle(base));
    }
}