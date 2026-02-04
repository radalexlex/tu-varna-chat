package org.tuvarna.chat.model.entity.postgres;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "chat_message",
        indexes = {
                @Index(
                        name = "chatroom_id_time_sent_" +
                                "and_id_desc_index",
                        columnList = "chatroom_id, " +
                                "time_sent DESC, id DESC"
                )
        }
)
public class ChatMessage {
    @Id
    @SequenceGenerator(
            name = "ChatMessageSequence",
            sequenceName = "seq_chat_message",
            allocationSize = 50
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ChatMessageSequence"
    )
    private long id;
    @Column(
            name = "client_message_id",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID clientMessageId;
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "chatroom_id",
            insertable = false,
            nullable = false,
            updatable = false
    )
    private Chatroom chatroom;
    @Column(
            name="chatroom_id",
            insertable = true,
            updatable = false,
            nullable = false
    )
    private int chatroomId;
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "sender_user_id",
            insertable = false,
            updatable = false,
            nullable = false
    )
    private ChatroomUser senderUser;
    @Column(
            name = "sender_user_id",
            insertable = true,
            nullable = false,
            updatable = false
    )
    private int senderUserId;
    @Column(
            length = 4000,
            nullable = false
    )
    private String content;
    @Column(
            name = "time_sent",
            updatable = false,
            nullable = false
    )
    private Instant timeSent;
    @Column(
            nullable = false,
            updatable = true
    )
    private boolean deleted;

    public ChatMessage() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(UUID clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public Chatroom getChatroom() {
        return chatroom;
    }

    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }

    public int getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(int chatroomId) {
        this.chatroomId = chatroomId;
    }

    public ChatroomUser getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(ChatroomUser senderUser) {
        this.senderUser = senderUser;
    }

    public int getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(int senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Instant timeSent) {
        this.timeSent = timeSent;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessage that = (ChatMessage) o;
        return id == that.id && deleted == that.deleted && Objects.equals(clientMessageId, that.clientMessageId) && Objects.equals(chatroom, that.chatroom) && Objects.equals(senderUser, that.senderUser) && Objects.equals(content, that.content) && Objects.equals(timeSent, that.timeSent);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + Objects.hashCode(clientMessageId);
        result = 31 * result + Objects.hashCode(chatroom);
        result = 31 * result + chatroomId;
        result = 31 * result + Objects.hashCode(senderUser);
        result = 31 * result + senderUserId;
        result = 31 * result + Objects.hashCode(content);
        result = 31 * result + Objects.hashCode(timeSent);
        result = 31 * result + Boolean.hashCode(deleted);
        return result;
    }
}

/*
* @Index(
        name = "chatroom_id_sender_id_time_sent_desc_index",
        columnList = "chatroom_id, sender_user_id, time_sent DESC"
),*/