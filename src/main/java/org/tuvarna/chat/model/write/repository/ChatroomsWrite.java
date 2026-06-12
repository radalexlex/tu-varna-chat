package org.tuvarna.chat.model.write.repository;

import jakarta.data.repository.*;
import org.tuvarna.chat.model.entity.postgres.Chatroom;

import java.util.List;

@Repository
public interface ChatroomsWrite {

    @Save
    Chatroom save(Chatroom entity);

    @Save
    List<Chatroom> saveAll(List<Chatroom> entities);

    @Query("update Chatroom c set c.deleted = true where c.id = :chatroomId")
    @Update
    int archiveChatroomById(@Param("chatroomId") int chatroomId); // returns number of changed rows

    @Query("update Chatroom c set c.name = :newName where c.id = :chatroomId and c.deleted = false")
    @Update
    int updateName(@Param("chatroomId") int chatroomId,
                   @Param("newName") String newName);

}

