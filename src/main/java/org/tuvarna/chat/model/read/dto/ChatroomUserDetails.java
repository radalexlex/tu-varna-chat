package org.tuvarna.chat.model.read.dto;

public record ChatroomUserDetails(long id,
                                  int chatroomId,
                                  long userId,
                                  String role,
                                  String status,
                                  String timeAdded,
                                  Long lastRead) {
}
