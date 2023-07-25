package com.iamkhs.notesventure.controller;

import com.iamkhs.notesventure.entities.Notes;
import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.helper.Messages;
import com.iamkhs.notesventure.service.NotesService;
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
    private final NotesService notesService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, NotesService notesService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.notesService = notesService;
    }

    @GetMapping("user/0/u/dashboard")
    public String dashboard(Principal principal, Model model) {
        String email = principal.getName();
        User user = this.userService.getUser(email);

        String keyword = (String) model.asMap().get("keyword");

        List<Notes> notesList;

        if (keyword != null  && !keyword.trim().isEmpty()) {
            notesList = this.notesService.searchNotes(user.getId(), keyword);
        }
        else {
            notesList = user.getNotesList();
            for (Notes notes : notesList) {
                LocalDateTime notesCreatedDate = notes.getNotesCreatedDate();
                LocalDateTime notesUpdateDate = notes.getNotesUpdatedDate();

                LocalDate createdDate = notesCreatedDate.toLocalDate();
                notes.setNotesCreatedDate(createdDate.atStartOfDay());

                if (notesUpdateDate != null) {
                    LocalDate updateDate = notesUpdateDate.toLocalDate();
                    notes.setNotesUpdatedDate(updateDate.atStartOfDay());
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
                throw new Exception("Something went Wrong, Try again!");
            }

            if (!user.getPassword().equals(user.getConfirmPassword())){
                throw new Exception("Password not match!");
            }

            user.setRole("ROLE_USER");
            user.setUserRegisterDate(LocalDateTime.now());

            // PASSWORD ENCODING
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            User savedUser = userService.saveUser(user);
            model.addAttribute("user", savedUser);
            session.setAttribute("message", new Messages("Successfully Registered", "alert-success"));

        }catch (Exception e){
            model.addAttribute("message", "error");
            session.setAttribute("message", new Messages("Something went wrong!!"+ e.getMessage(), "alert-danger"));
            return "signup";
        }

        return "redirect:/login";
    }


    // Notes creating process
    @PostMapping("/user/creating-notes")
    public String creatingNotes(@ModelAttribute Notes notes, Principal principal){
        String email = principal.getName();

        User user = this.userService.getUser(email);
        notes.setUser(user);
        notes.setNotesCreatedDate(LocalDateTime.now());

        this.notesService.saveNotes(notes);

        return "redirect:/user/0/u/dashboard";
    }

    // Updating Notes
    @PostMapping("/user/update-notes")
    public String updateNotes(@RequestParam Long noteId, String action, @ModelAttribute Notes notes){
        if (action.equals("delete")){
            this.notesService.deleteNote(noteId);
        }else if (action.equals("update")){
            Notes preNotes = this.notesService.getNotes(noteId);
            preNotes.setTitle(notes.getTitle());
            preNotes.setDescription(notes.getDescription());
            preNotes.setNotesUpdatedDate(LocalDateTime.now());
            this.notesService.saveNotes(preNotes);
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
