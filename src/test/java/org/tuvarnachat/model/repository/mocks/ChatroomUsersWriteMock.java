//package org.tuvarnachat.model.repository.mocks;
//
//import io.quarkus.test.Mock;
//import jakarta.enterprise.context.ApplicationScoped;
//import org.tuvarna.chat.model.entity.postgres.ChatroomUser;
//import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
//import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
//import org.tuvarna.chat.model.write.repository.ChatroomUsersWrite;
//
//import java.util.List;
//import java.util.Random;
//
//@Mock
//@ApplicationScoped
//public class ChatroomUsersWriteMock implements ChatroomUsersWrite {
//
//    @Override
//    public ChatroomUser save(ChatroomUser entity) {
//        entity.setId(new Random().nextLong(1000));
//        return entity;
//    }
//
//    @Override
//    public List<ChatroomUser> saveAll(List<ChatroomUser> entities) {
//        long id = 1;
//        for (ChatroomUser u : entities) {
//            u.setId(id++);
//        }
//        return entities;
//    }
//
//    @Override
//    public int changeMembership(long userId, MembershipStatus status) {
//        return 1;
//    }
//
//    @Override
//    public int changeRole(long userId, ChatroomRole role) {
//        return 1;
//    }
//
//    @Override
//    public int updateLastRead(long userId, long newLastRead) {
//        return 1;
//    }
//}