package org.tuvarna.chat.model.read.query.page.data;

import java.time.Instant;

public record ChatroomUserPageData(Instant oldestAdditionTimestamp,
                                   Integer oldestAdditionId,
                                   boolean extendedPermissionGiven) {
}
