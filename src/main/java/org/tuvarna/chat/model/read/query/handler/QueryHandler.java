package org.tuvarna.chat.model.read.query.handler;

import java.util.Optional;

public interface QueryHandler<K, T> {
    K handleQuery(T query);
}
