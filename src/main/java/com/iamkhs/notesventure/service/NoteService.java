package com.iamkhs.notesventure.service;

import com.iamkhs.notesventure.entities.Note;

import java.util.List;

public interface NoteService {
    Note saveNotes(Note note);
    void deleteNote(Long id);
    Note getNotes(Long id);

    List<Note> searchNotes(Long id, String keyword);

    List<Note> totalNotes();
}
