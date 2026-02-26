package org.tuvarna.chat.model.write.repository;

import jakarta.data.repository.*;
import org.tuvarna.chat.model.entity.postgres.ChatroomUser;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;

import javax.sql.DataSource;
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

}
