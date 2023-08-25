package com.iamkhs.notesventure.service.impl;

import com.iamkhs.notesventure.entities.Note;
import com.iamkhs.notesventure.repository.NoteRepository;
import com.iamkhs.notesventure.service.NoteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public Note saveNotes(Note note) {
        return this.noteRepository.save(note);
    }

    @Override
    public void deleteNote(Long id) {
        this.noteRepository.deleteById(id);
    }

    @Override
    public Note getNotes(Long id) {
        return this.noteRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Note> searchNotes(Long id, String keyword) {
        return this.noteRepository.searchNotesByUserIdAndKeyword(id, keyword);
    }

    @Override
    public List<Note> totalNotes() {
        return this.noteRepository.findAll();
    }
}
