package org.tuvarna.chat.model.write.dto;

public record MessagePersistenceStatus(boolean errored, boolean[] errorIndexes) {
}