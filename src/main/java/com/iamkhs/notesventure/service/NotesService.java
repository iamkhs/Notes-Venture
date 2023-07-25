package com.iamkhs.notesventure.service;

import com.iamkhs.notesventure.entities.Notes;

import java.util.List;

public interface NotesService {
    Notes saveNotes(Notes notes);
    void deleteNote(Long id);
    Notes getNotes(Long id);

    List<Notes> searchNotes(Long id, String keyword);
}
