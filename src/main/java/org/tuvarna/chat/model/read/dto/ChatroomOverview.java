package org.tuvarna.chat.model.read.dto;

import java.time.Instant;

public record ChatroomOverview(int id,
                               String name,
                               Instant createdAt) {
}
