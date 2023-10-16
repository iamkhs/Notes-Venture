package com.iamkhs.notesventure.controller;

import com.iamkhs.notesventure.entities.Note;
import com.iamkhs.notesventure.entities.TrashNote;
import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.helper.UserServiceUtil;
import com.iamkhs.notesventure.service.NoteService;
import com.iamkhs.notesventure.service.TrashNoteService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
/**
 * This controller class handles actions related to trash notes, including displaying the trash notes dashboard,
 * and processing actions such as deleting or restoring trash notes.
 *
 */

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class TrashNoteController {

    private final TrashNoteService trashNoteService;
    private final NoteService noteService;
    private final UserServiceUtil userServiceUtil;

    // Display the Trash Notes dashboard for the current user
    @GetMapping("/0/u/trash-notes")
    public String trashNotePage(Model model, Principal principal, @AuthenticationPrincipal OAuth2User oAuth2User) {
        // Get the currently logged-in user
        User user = this.userServiceUtil.getLoggedUser(principal, oAuth2User);

        // Retrieve all the trash notes for the user
        List<TrashNote> trashNotesList = trashNoteService.getAllTrashNotes(user.getId());

        // Add the list of trash notes to the model
        model.addAttribute("trashNotesList", trashNotesList);

        // Display the trash notes page
        return "user/trash-note";
    }

    // Process actions on a trash note (delete or restore)
    @PostMapping("/trash-note/process")
    public String trashNoteProcess(@RequestParam Long id, String action) {
        // If the action is to delete the note permanently
        if (action != null && action.equals("delete")) {
            // Delete the note from the trash
            trashNoteService.deleteNoteById(id);
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

            // Delete the note from the trash after recovery
            this.trashNoteService.deleteNoteById(id);
        }

        // Redirect the user to the dashboard after processing the action
        return "redirect:/user/0/u/dashboard";
    }
}
