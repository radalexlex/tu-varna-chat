package org.tuvarna.chat.model.read.query.page.data;

import java.time.Instant;

public record ChatroomEventfulPageData(Instant latestEventTimeOnPage,
                                       Integer latestChatroomIdOnPage,
                                       Long latestChatMessageIdOnPage) {
}
