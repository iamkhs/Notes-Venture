package com.iamkhs.notesventure.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

}
