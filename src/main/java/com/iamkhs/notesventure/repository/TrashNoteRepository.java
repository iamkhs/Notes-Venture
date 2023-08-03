package com.iamkhs.notesventure.repository;

import com.iamkhs.notesventure.entities.TrashNote;
import com.iamkhs.notesventure.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrashNoteRepository extends JpaRepository<TrashNote, Long> {
    void deleteByNoteDeletedDateBeforeAndUser(LocalDateTime date, User user);
    List<TrashNote> findAllByUserId(Long id);
}
