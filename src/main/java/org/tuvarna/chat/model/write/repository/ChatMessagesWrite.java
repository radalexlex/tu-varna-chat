package org.tuvarna.chat.model.write.repository;

import jakarta.data.repository.*;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;
import org.tuvarna.chat.model.write.dto.ChatMessageSaveData;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessagesWrite {

    Connection connection();

    default int insertMessages(List<ChatMessageSaveData> dataList) {

        if (dataList == null || dataList.isEmpty()) {
            return 0;
        }

        final String sql =
                "INSERT INTO chat_message (" +
                        "id, chatroom_id, client_message_id, sender_user_id, " +
                        "content, time_sent, deleted) " +
                        "VALUES (nextval(seq_chat_message), ?, ?, ?, ?, ?, false)";

        Timestamp now = Timestamp.from(Instant.now());

        try (Connection conn = connection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (ChatMessageSaveData d : dataList) {
                ps.setInt(1, d.chatroomId());
                // here check the uuid, if broken then check
                // all for broken uuids and remove them from the list
                // and run again
                ps.setObject(2, UUID.fromString(d.clientMessageId()), Types.OTHER);
                ps.setLong(3, d.senderId());
                ps.setString(4, d.content());
                ps.setTimestamp(5, now);

                ps.addBatch();
            }

            int[] counts = ps.executeBatch();
            conn.commit();

            return counts.length;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to batch insert chat messages", e);
        }
    }

    @Save
    ChatMessage save(ChatMessage entity);

    @Save
    List<ChatMessage> saveAll(List<ChatMessage> entities);

    @Query("update ChatMessage cm set cm.content = :newContent where cm.id = :messageId")
    @Update
    int updateMessageById(@Param("messageId") long messageId,
                          @Param("newContent") String content); // returns number of changed rows

    @Query("update ChatMessage cm set cm.deleted = true where cm.id = :messageId")
    @Update
    int archiveMessageById(@Param("messageId") long messageId); // returns number of changed rows

}


//    DataSource getDataSource();
//
//    @Query("update ChatMessage cm set cm.deleted = true")
//    @Update
//    ChatMessage insert();
//
//    @Insert
