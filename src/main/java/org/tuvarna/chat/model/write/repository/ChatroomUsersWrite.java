package org.tuvarna.chat.model.write.repository;

import jakarta.data.repository.Param;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Update;
import org.tuvarna.chat.model.entity.postgres.ChatroomUser;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;

@Repository
public interface ChatroomUsersWrite extends WriteRepository<ChatroomUser, Integer> {

    @Query("update ChatroomUser cu set cu.status = :newStatus where cu.id = :joinId")
    @Update
    int changeMembership(@Param("joinId") int joinId,
                         @Param("newStatus") MembershipStatus status); // returns number of changed rows

    @Query("update ChatroomUser cu set cu.role = :newRole where cu.id = :joinId")
    @Update
    int changeRole(@Param("joinId") int joinId,
                   @Param("newRole") ChatroomRole role); // returns number of changed rows

}
