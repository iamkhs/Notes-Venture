package com.iamkhs.notesventure.controller;

import com.iamkhs.notesventure.entities.Note;
import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.helper.Messages;
import com.iamkhs.notesventure.service.NoteService;
import com.iamkhs.notesventure.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final NoteService noteService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, NoteService noteService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.noteService = noteService;
    }

    @GetMapping("user/0/u/dashboard")
    public String dashboard(Principal principal, Model model) {
        String email = principal.getName();
        User user = this.userService.getUser(email);

        String keyword = (String) model.asMap().get("keyword");

        List<Note> notesList;

        if (keyword != null  && !keyword.trim().isEmpty()) {
            notesList = this.noteService.searchNotes(user.getId(), keyword);
        }
        else {
            notesList = user.getNotesList();
            for (Note note : notesList) {
                LocalDateTime notesCreatedDate = note.getNotesCreatedDate();
                LocalDateTime notesUpdateDate = note.getNotesUpdatedDate();

                LocalDate createdDate = notesCreatedDate.toLocalDate();
                note.setNotesCreatedDate(createdDate.atStartOfDay());

                if (notesUpdateDate != null) {
                    LocalDate updateDate = notesUpdateDate.toLocalDate();
                    note.setNotesUpdatedDate(updateDate.atStartOfDay());
                }
            }
        }
        model.addAttribute("noteList", notesList);

        model.addAttribute("user", user);
        return "user/dashboard";
    }

    // Registration process
    @PostMapping("/form-process")
    public String registration(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model, HttpSession session){

        // process the user saving to database.....
        try{
            if (bindingResult.hasErrors()){
                model.addAttribute("user", user);
                return "signup";
            }

            if (!user.getPassword().equals(user.getConfirmPassword())){
                bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Password and confirm password do not match!");
                throw new Exception("Password not match!");
            }

            user.setRole("ROLE_USER");
            user.setUserRegisterDate(LocalDateTime.now());

            // PASSWORD ENCODING
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Checking if the email already exists in the database
            User existingUser = userService.getUser(user.getEmail());
            if (existingUser != null){
                bindingResult.rejectValue("email", "error.email", "This email is already registered!");
                throw new Exception("This email is already Registered!");
            }

            User savedUser = userService.saveUser(user);
            model.addAttribute("user", savedUser);
            session.setAttribute("message", new Messages("Successfully Registered", "alert-success"));

        }catch (Exception e){
            model.addAttribute("user", user);
            session.setAttribute("message", new Messages("Something went wrong!! "+ e.getMessage(), "alert-danger"));
            return "signup";
        }

        return "redirect:/login";
    }


    // Notes creating process
    @PostMapping("/user/creating-notes")
    public String creatingNotes(@ModelAttribute Note note, Principal principal, HttpSession session){
        String email = principal.getName();

        User user = this.userService.getUser(email);

        // if user filled the title and description empty!
        if (note.getTitle().trim().isEmpty() && note.getDescription().trim().isEmpty()){
            session.setAttribute("message", new Messages("Something went wrong!", "danger"));
            return "redirect:/user/0/u/dashboard";
        }

        note.setUser(user);
        note.setNotesCreatedDate(LocalDateTime.now());

        this.noteService.saveNotes(note);

        return "redirect:/user/0/u/dashboard";
    }

    // Updating Notes
    @PostMapping("/user/update-notes")
    public String updateNotes(@RequestParam Long noteId, String action, @ModelAttribute Note note){
        // Deleting Note Process
        if (action != null && action.equals("delete") || note.getTitle().trim().isEmpty() && note.getDescription().trim().isEmpty()){
            this.noteService.deleteNote(noteId);
        }else{
            // Updating Note Process
            Note preNote = this.noteService.getNotes(noteId);
            preNote.setTitle(note.getTitle());
            preNote.setDescription(note.getDescription());
            preNote.setNotesUpdatedDate(LocalDateTime.now());
            this.noteService.saveNotes(preNote);
        }
        return "redirect:/user/0/u/dashboard";
    }


    // Processing the searching method
    @GetMapping("/user/search-notes")
    public String searchNotes(@RequestParam(required = false) String keyword, RedirectAttributes redirectAttributes) {
        if (keyword != null){
            redirectAttributes.addFlashAttribute("keyword", keyword);
        }
        // Redirect to the dashboard
        return "redirect:/user/0/u/dashboard";

    }
}
