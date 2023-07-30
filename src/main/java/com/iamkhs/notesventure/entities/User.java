package com.iamkhs.notesventure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String name;

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank()
    @Size(min = 5, message = "minimum password character is 5")
    private String password;
    private String role;

    @Transient
    @NotBlank
    @Size(min = 5, message = "minimum password character is 5")
    private String confirmPassword;

    private LocalDateTime userRegisterDate;

    private boolean enable = true;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Note> notesList = new ArrayList<>();

}
