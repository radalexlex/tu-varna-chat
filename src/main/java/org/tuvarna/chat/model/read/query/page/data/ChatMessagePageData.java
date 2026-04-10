package org.tuvarna.chat.model.read.query.page.data;

import java.time.Instant;

public record ChatMessagePageData(Instant lastTimestamp, Integer lastId, boolean requestForOlder) {
}
