package org.tuvarna.chat.model.read.repository.domain;

import jakarta.data.repository.Repository;
import org.tuvarna.chat.model.entity.postgres.Chatroom;

@Repository
public interface ChatroomsRead extends ReadRepository<Chatroom, Integer> {

}

//    public record ChatroomSideBarItem(
//            long chatroomId,
//            String chatroomName,
//            long lastChatroomUserId,
//            String lastEvent,
//            String timeOfLastEvent
//    ) {
//
//    }
