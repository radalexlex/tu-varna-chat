package org.tuvarna.chat.model.write.repository;

import jakarta.data.repository.*;
import org.tuvarna.chat.model.entity.postgres.ChatroomUser;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;

import java.time.Instant;
import java.util.List;

@Repository
public interface ChatroomUsersWrite {

    @Save
    ChatroomUser save(ChatroomUser entity);

    @Save
    List<ChatroomUser> saveAll(List<ChatroomUser> entities);

    @Query("update ChatroomUser cu set cu.status = :newStatus where cu.userId = :userId")
    @Update
    int changeMembership(@Param("userId") long userId,
                         @Param("newStatus") MembershipStatus status); // returns number of changed rows

    @Query("update ChatroomUser cu set cu.role = :newRole where cu.userId = :userId")
    @Update
    int changeRole(@Param("userId") long userId,
                   @Param("newRole") ChatroomRole role); // returns number of changed rows

    @Query("update ChatroomUser cu set cu.lastRead = :newLastRead " +
            "where cu.userId = :userId " +
            "and coalesce(cu.lastRead, 0) < :newLastRead " +
            "and cu.chatroomId = :chatroomId")
    @Update
    int updateLastReadById(@Param("userId") long userId,
                       @Param("chatroomId") int chatroomId,
                       @Param("newLastRead") long newLastRead);

    @Query("update ChatroomUser cu " +
            "set cu.lastRead = (" +
            "    select max(cm.id) " +
            "    from ChatMessage cm " +
            "    where cm.chatroomId = cu.chatroomId " +
            "      and cm.timeSent <= :newLastRead " +
            ")" +
            "where cu.userId = :userId" +
            "  and cu.chatroomId = :chatroomId" +
            "  and coalesce(cu.lastRead, 0) < (" +
            "      select max(cm.id)" +
            "      from ChatMessage cm" +
            "      where cm.chatroomId = cu.chatroomId" +
            "        and cm.timeSent <= :newLastRead" +
            ")")
    @Update
    int updateLastReadByTimestamp(@Param("userId") long userId,
                           @Param("chatroomId") int chatroomId,
                           @Param("newLastRead") Instant newLastRead);


}
