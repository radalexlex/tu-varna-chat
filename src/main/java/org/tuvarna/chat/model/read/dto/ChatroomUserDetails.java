package org.tuvarna.chat.model.read.dto;

import java.time.Instant;

public record ChatroomUserDetails(int id,
                                  int chatroomId,
                                  long userId,
                                  String role,
                                  String status,
                                  Instant timeAdded) {
}
