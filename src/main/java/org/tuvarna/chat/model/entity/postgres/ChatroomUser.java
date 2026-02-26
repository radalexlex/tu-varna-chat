package org.tuvarna.chat.model.entity.postgres;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;

import java.time.Instant;

@Entity
@Table(
        name = "chatroom_user",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "chatroom_id"}),
        indexes = {
                @Index(
                        name = "user_id_chatroom_id_index",
                        columnList = "user_id, chatroom_id"
                ),
                @Index(
                        name = "chatroom_id_user_id_index",
                        columnList = "chatroom_id, user_id"
                ),
                @Index(
                        name = "chatroom_id_status_join_time_index",
                        columnList = "chatroom_id, status, join_time ASC"
                )
        }
)
public class ChatroomUser {
    @Id
    @SequenceGenerator(
            name = "ChatroomUserSequence",
            sequenceName = "seq_chatroom_user",
            allocationSize = 50
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ChatroomUserSequence"
    )
    private long id;
    @Column(
            name = "user_id",
            nullable = false,
            updatable = false
    )
    private long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            insertable = false,
            nullable = false,
            updatable = false
    )
    private Chatroom chatroom;
    @Column(
            name = "chatroom_id",
            insertable = true,
            updatable = false,
            nullable = false
    )
    private int chatroomId;
    @CreationTimestamp
    @Column(
            name = "join_time",
            nullable = false,
            updatable = false
    )
    private Instant joinTime;
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            updatable = true
    )
    private ChatroomRole role;
    @Column(
            nullable = false,
            updatable = true
    )
    @Enumerated(EnumType.STRING)
    private MembershipStatus status;

    public ChatroomUser() {
    }

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = MembershipStatus.ACTIVE;
        }
        if (role == null) {
            role = ChatroomRole.MEMBER;
        }
    }

    public int getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(int chatroomId) {
        this.chatroomId = chatroomId;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public ChatroomRole getRole() {
        return role;
    }

    public void setRole(ChatroomRole role) {
        this.role = role;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Chatroom getChatroom() {
        return chatroom;
    }

    protected void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }

    public Instant getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Instant joinTime) {
        this.joinTime = joinTime;
    }
}
