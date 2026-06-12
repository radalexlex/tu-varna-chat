//package org.tuvarnachat.model.repository.mocks;
//
//import io.quarkus.test.Mock;
//import jakarta.enterprise.context.ApplicationScoped;
//import org.tuvarna.chat.model.entity.postgres.Chatroom;
//import org.tuvarna.chat.model.write.repository.ChatroomsWrite;
//
//import java.util.List;
//import java.util.Random;
//
//@Mock
//@ApplicationScoped
//public class ChatroomsWriteMock implements ChatroomsWrite {
//
//    @Override
//    public Chatroom save(Chatroom entity) {
//        entity.setId(new Random().nextInt(1000));
//        return entity;
//    }
//
//    @Override
//    public List<Chatroom> saveAll(List<Chatroom> entities) {
//        int id = 1;
//        for (Chatroom c : entities) {
//            c.setId(id++);
//        }
//        return entities;
//    }
//
//    @Override
//    public int archiveChatroomById(int chatroomId) {
//        return 1;
//    }
//
//    @Override
//    public int updateLastRead(int chatroomId, long newLastRead) {
//        return 1;
//    }
//
//    @Override
//    public int updateName(int chatroomId, String newName) {
//        return 1;
//    }
//}