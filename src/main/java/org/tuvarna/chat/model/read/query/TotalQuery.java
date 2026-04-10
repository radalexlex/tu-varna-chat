package org.tuvarna.chat.model.read.query;

public sealed interface TotalQuery<T> permits TotalQuery.GetAllForCommon {

    record GetAllForCommon<T>(T commonId) implements TotalQuery<T> {
    }

}
