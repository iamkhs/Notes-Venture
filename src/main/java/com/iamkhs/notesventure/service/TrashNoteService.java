package com.iamkhs.notesventure.service;

import com.iamkhs.notesventure.entities.TrashNote;

import java.util.List;

public interface TrashNoteService{
    void saveTrashNote(TrashNote note);
    List<TrashNote> getAllTrashNotes(Long userId);
    void deleteOldTrashNotes();
    void deleteNoteById(Long id);
    TrashNote getNoteById(Long id);
}
