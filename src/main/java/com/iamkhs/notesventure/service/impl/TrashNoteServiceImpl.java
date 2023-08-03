package com.iamkhs.notesventure.service.impl;

import com.iamkhs.notesventure.entities.TrashNote;
import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.repository.TrashNoteRepository;
import com.iamkhs.notesventure.repository.UserRepository;
import com.iamkhs.notesventure.service.TrashNoteService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TrashNoteServiceImpl implements TrashNoteService {

    private final TrashNoteRepository noteRepository;
    private final UserRepository userRepository;


    @Override
    public void saveTrashNote(TrashNote note) {
        this.noteRepository.save(note);
    }

    @Override
    public List<TrashNote> getAllTrashNotes(Long userId) {
        return this.noteRepository.findAllByUserId(userId);
    }

    @Scheduled(cron = "0 0 12 * * *") // schedules the task to run every day at 12 PM
    @Transactional
    @Override
    public void deleteOldTrashNotes() {
        // Calculate the day for deleting old trash notes
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7); // Set it to 7 days ago

        // Fetch all users from the database
        List<User> allUser = this.userRepository.findAll();

        // Iterate through each user and delete their old trash notes
        for (User user : allUser){
            // Delete trash notes that are older than 7 days and belong to the current user
            noteRepository.deleteByNoteDeletedDateBeforeAndUser(sevenDaysAgo, user);
        }
    }

    @Override
    public void deleteNoteById(Long id) {
        this.noteRepository.deleteById(id);
    }

    @Override
    public TrashNote getNoteById(Long id) {
        return this.noteRepository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
