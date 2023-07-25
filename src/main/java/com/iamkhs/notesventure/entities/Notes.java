package com.iamkhs.notesventure.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Notes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;

    @Column(length = 5000)
    private String description;
    private LocalDateTime notesCreatedDate;
    private LocalDateTime notesUpdatedDate;

    @ManyToOne
    private User user;

    public Notes() {
    }

    public Notes(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getNotesCreatedDate() {
        return notesCreatedDate;
    }

    public void setNotesCreatedDate(LocalDateTime notesCreatedDate) {
        this.notesCreatedDate = notesCreatedDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return "Notes{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", notesCreatedDate=" + notesCreatedDate +
                ", user=" + user +
                '}';
    }

    public LocalDateTime getNotesUpdatedDate() {
        return notesUpdatedDate;
    }

    public void setNotesUpdatedDate(LocalDateTime notesUpdatedDate) {
        this.notesUpdatedDate = notesUpdatedDate;
    }
}
