package com.iamkhs.notesventure.repository;

import com.iamkhs.notesventure.entities.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotesRepository extends JpaRepository<Notes, Long> {

    @Query(value = "SELECT * FROM notes WHERE (user_id = :userId) AND (title LIKE %:keyword% OR description LIKE %:keyword%)", nativeQuery = true)
    List<Notes> searchNotesByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}
