package org.tuvarna.chat.model.write.repository;

import jakarta.data.repository.Param;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;
import org.tuvarna.chat.model.entity.postgres.Chatroom;

@Repository
public interface ChatroomsWrite extends WriteRepository<Chatroom, Integer> {

    @Query("update Chatroom c set c.deleted = true where c.id = :chatroomId")
    @Update
    int archiveChatroomById(@Param("chatroomId") int chatroomId); // returns number of changed rows

}

