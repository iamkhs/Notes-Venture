package com.iamkhs.notesventure.controller;

import com.iamkhs.notesventure.entities.Note;
import com.iamkhs.notesventure.entities.TrashNote;
import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.service.NoteService;
import com.iamkhs.notesventure.service.TrashNoteService;
import com.iamkhs.notesventure.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class TrashNoteController {

    private final TrashNoteService trashNoteService;
    private final UserService userService;
    private final NoteService noteService;

    // Display the Trash Notes dashboard for the current user
    @GetMapping("/0/u/trash-notes")
    public String trashNotePage(Model model, Principal principal) {
        // Get the currently logged-in user
        User user = this.userService.getUser(principal.getName());

        // Retrieve all the trash notes for the user
        List<TrashNote> trashNotesList = trashNoteService.getAllTrashNotes(user.getId());

        // Add the list of trash notes to the model
        model.addAttribute("trashNotesList", trashNotesList);

        // Display the trash notes page
        return "/user/trash-note";
    }

    // Process actions on a trash note (delete or restore)
    @PostMapping("/trash-note/process")
    public String trashNoteProcess(@RequestParam Long id, String action) {
        // If the action is to delete the note permanently
        if (action != null && action.equals("delete")) {
            // Delete the note from the trash
            trashNoteService.deleteNoteById(id);
            System.err.println("Note deleted successfully");
        } else {
            // If the action is to restore the note
            // Get the trash note by its id
            TrashNote trashNote = trashNoteService.getNoteById(id);

            // Create a new Note object and copy data from the trash note
            Note note = new Note();
            note.setTitle(trashNote.getTitle());
            note.setDescription(trashNote.getDescription());
            note.setNotesCreatedDate(trashNote.getNotesCreatedDate());
            note.setNotesUpdatedDate(trashNote.getNotesUpdatedDate());
            note.setUser(trashNote.getUser());

            // Save the recovered note
            this.noteService.saveNotes(note);
            System.err.println("Note Recover Successfully");

            // Delete the note from the trash after recovery
            this.trashNoteService.deleteNoteById(id);
        }

        // Redirect the user to the dashboard after processing the action
        return "redirect:/user/0/u/dashboard";
    }
}
