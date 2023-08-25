package com.iamkhs.notesventure.controller.admin;

import com.iamkhs.notesventure.entities.Note;
import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.service.NoteService;
import com.iamkhs.notesventure.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller()
@AllArgsConstructor
public class AdminHomeController {

    private final UserService userService;
    private final NoteService noteService;

    @GetMapping("/admin/home")
    public String home(Model model, Principal principal){
        User user = userService.getUser(principal.getName());

        List<User> allUsers = this.userService.getAllUsers();
        List<Note> allNotes = this.noteService.totalNotes();
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("user", user);
        model.addAttribute("allNotes", allNotes);
        return "/admin/admin-home";
    }

}
