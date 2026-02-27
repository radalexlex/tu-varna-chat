package org.tuvarna.chat.model.read.repository.domain;

import jakarta.data.repository.By;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import org.tuvarna.chat.model.entity.postgres.Chatroom;

import java.util.Optional;

@Repository
public interface ChatroomsRead {

    @Find
    Optional<Chatroom> findById(@By("id(this)") int id);

}

