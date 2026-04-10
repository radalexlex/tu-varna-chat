package org.tuvarna.chat.model.read.query.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.model.read.query.TotalQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.repository.domain.ChatroomsRead;

import java.util.List;

@Named("ChatroomTotalQueryHandler")
@ApplicationScoped
public class ChatroomTotalQueryHandler implements QueryHandler<List<Integer>, TotalQuery<Long>> {

    ChatroomsRead repository;

    @Inject
    public ChatroomTotalQueryHandler(ChatroomsRead repository) {
        this.repository = repository;
    }

    @Override
    public List<Integer> handleQuery(TotalQuery<Long> query) {
        switch (query) {
            case TotalQuery.GetAllForCommon(Long userId) -> {
                return repository.findAllIdsByUserId(userId);
            }
        }
    }
}
