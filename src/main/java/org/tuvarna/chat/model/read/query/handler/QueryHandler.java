package org.tuvarna.chat.model.read.query.handler;

public interface QueryHandler<T, Q> {
    T handleQuery(Q query);
}
