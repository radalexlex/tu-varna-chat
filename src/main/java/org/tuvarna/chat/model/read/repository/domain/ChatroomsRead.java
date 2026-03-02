package org.tuvarna.chat.model.read.repository.domain;

import jakarta.data.repository.By;
import jakarta.data.repository.Find;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import org.hibernate.StatelessSession;
import org.hibernate.query.NativeQuery;
import org.tuvarna.chat.model.entity.postgres.Chatroom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatroomsRead {

    StatelessSession session();

    @Find
    Optional<Chatroom> findById(@By("id(this)") int id);

    default List<Integer> findAllIdsByUserId(Long userId) {
        StatelessSession session = session();

        String sql = """
                SELECT cm.id FROM chatroom cm
                    INNER JOIN chatroom_user cu
                        ON cm.id = cu.chatroom_id
                WHERE user_id = :userId
                """;

        NativeQuery<Integer> nativeQuery =
                session.createNativeQuery(sql, Integer.class);

        nativeQuery.setParameter("userId", userId);

        return nativeQuery.getResultList();

    }

}

