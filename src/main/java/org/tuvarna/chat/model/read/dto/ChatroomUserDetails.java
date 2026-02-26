package org.tuvarna.chat.model.read.dto;

import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;

import java.time.Instant;

public record ChatroomUserDetails(long id,
                                  int chatroomId,
                                  long userId,
                                  String role,
                                  String status,
                                  String timeAdded) {
}
