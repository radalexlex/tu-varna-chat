package org.tuvarna.chat.utils;

public sealed interface PageQuery<Index> permits PageQuery.GetFirstPage, PageQuery.GetFollowingPage {

    record GetFirstPage<Index>(Index id)
            implements PageQuery<Index> {
    }

    record GetFollowingPage<Index, Parameters>(Index id, Parameters p)
            implements PageQuery<Index> {
    }

}
