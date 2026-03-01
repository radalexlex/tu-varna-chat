package org.tuvarna.chat.model.read.query;

public sealed interface PageQuery<T, E> permits PageQuery.GetPage {

    record GetPage<T, E>(T id, E paginationDetails) implements PageQuery<T, E> {
    }


}
