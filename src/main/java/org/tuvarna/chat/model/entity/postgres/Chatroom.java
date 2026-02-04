package org.tuvarna.chat.model.entity.postgres;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "chatroom",
        indexes = {
                @Index(
                        name = "chatroom_id_name_created_at_index",
                        columnList = "id, name, created_at")
        }
)
public class Chatroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(
            nullable = false,
            updatable = true
    )
    private String name;
    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = {},
            mappedBy = "chatroom"
    )
    private List<ChatroomUser> users = new ArrayList<>();
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;
    @Column(
            nullable = false,
            updatable = true
    )
    private boolean deleted;

    public Chatroom() {
    }

    @PrePersist
    public void prePersist() {
        if (deleted) {
            deleted = false;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChatroomUser> getUsers() {
        return users;
    }

    public void setUsers(List<ChatroomUser> users) {
        this.users = users;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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

        Chatroom chatroom = (Chatroom) o;
        return id == chatroom.id && deleted == chatroom.deleted && Objects.equals(name, chatroom.name) && Objects.equals(users, chatroom.users) && Objects.equals(createdAt, chatroom.createdAt);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(users);
        result = 31 * result + Objects.hashCode(createdAt);
        result = 31 * result + Boolean.hashCode(deleted);
        return result;
    }
}


//    @OneToMany(fetch = FetchType.LAZY, cascade = {
//            CascadeType.REMOVE},
//            mappedBy = "chatroom",
//            orphanRemoval = true
//    ) // might be up for removal, seems that there is no need for chatroom-messages link
//    private List<ChatMessage> messages = new ArrayList<>();
//
//    public List<ChatMessage> getMessages() {
//        return messages;
//    }
//
//    public void setMessages(List<ChatMessage> messages) {
//        this.messages = messages;
//    }
