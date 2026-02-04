package org.tuvarna.chat.model.read.query.handler.impl;

public sealed interface PageQuery<Index> permits PageQuery.GetFirstPage, PageQuery.GetFollowingPage{

    record GetFirstPage<Index> (Index id)
            implements PageQuery<Index> {}

    record GetFollowingPage<Index, Parameters>(Index id, Parameters p)
            implements PageQuery<Index> {}

}
