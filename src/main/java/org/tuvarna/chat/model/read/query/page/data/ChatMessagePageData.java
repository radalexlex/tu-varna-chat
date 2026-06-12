package org.tuvarna.chat.model.read.query.page.data;

import java.time.Instant;

public record ChatMessagePageData(
        Long messageCursorId,
        boolean downScroll,
        boolean initialRequest
) {}