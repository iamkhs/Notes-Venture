package com.iamkhs.notesventure.repository;

import com.iamkhs.notesventure.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query(value = "SELECT * FROM notes WHERE (user_id = :userId) AND (title LIKE %:keyword% OR description LIKE %:keyword%)", nativeQuery = true)
    List<Note> searchNotesByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}
