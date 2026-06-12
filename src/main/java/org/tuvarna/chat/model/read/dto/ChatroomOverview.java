package org.tuvarna.chat.model.read.dto;

public record ChatroomOverview(int id,
                               String name,
                               String createdAt,
                               String globalLastRead) {
}
