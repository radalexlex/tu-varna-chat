package org.tuvarna.chat.model.read.query;

public sealed interface DetailQuery<T> permits DetailQuery.GetData {

    record GetData<T>(T id) implements DetailQuery<T> {
    }

}
