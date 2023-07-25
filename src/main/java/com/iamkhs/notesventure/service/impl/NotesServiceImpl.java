package com.iamkhs.notesventure.service.impl;

import com.iamkhs.notesventure.entities.Notes;
import com.iamkhs.notesventure.repository.NotesRepository;
import com.iamkhs.notesventure.service.NotesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotesServiceImpl implements NotesService {

    private final NotesRepository notesRepository;

    public NotesServiceImpl(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    @Override
    public Notes saveNotes(Notes notes) {
        return this.notesRepository.save(notes);
    }

    @Override
    public void deleteNote(Long id) {
        this.notesRepository.deleteById(id);
    }

    @Override
    public Notes getNotes(Long id) {
        return this.notesRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Notes> searchNotes(Long id, String keyword) {
        return this.notesRepository.searchNotesByUserIdAndKeyword(id, keyword);
    }

}
