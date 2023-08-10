package com.iamkhs.notesventure.controller;

import com.iamkhs.notesventure.entities.Note;
import com.iamkhs.notesventure.entities.TrashNote;
import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.exceptions.UserAlreadyRegisteredException;
import com.iamkhs.notesventure.helper.Messages;
import com.iamkhs.notesventure.service.NoteService;
import com.iamkhs.notesventure.service.TrashNoteService;
import com.iamkhs.notesventure.service.UserService;
import com.iamkhs.notesventure.service.impl.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.util.UUID;

@AllArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final NoteService noteService;
    private final TrashNoteService trashNoteService;
    private final EmailSenderService emailSender;


    // User Dashboard.
    @GetMapping("user/0/u/dashboard")
    public String dashboard(Principal principal, Model model) {
        String email = principal.getName();
        User user = this.userService.getUser(email);

        String keyword = (String) model.asMap().get("keyword");

        List<Note> notesList;

        if (keyword != null && !keyword.trim().isEmpty()) {
            notesList = this.noteService.searchNotes(user.getId(), keyword);
        } else {
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
    public String registration(@ModelAttribute @Valid User user,
                               BindingResult bindingResult,
                               Model model, HttpSession session,
                               HttpServletRequest request) {

        // Process the user saving to the database...
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("user", user);
                return "signup";
            }

            // Check if the password matches the confirm password field.
            if (!user.getPassword().equals(user.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Password and confirm password do not match!");
                throw new Exception("Password not match!");
            }

            // Setting the user role.
            user.setRole("ROLE_USER");

            user.setUserRegisterDate(LocalDateTime.now());

            // PASSWORD ENCODING.
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Checking if the email already exists in the database.
            User existingUser = userService.getUser(user.getEmail());
            if (existingUser != null) {
                bindingResult.rejectValue("email", "error.email", "This email is already registered!");
                throw new UserAlreadyRegisteredException();
            }

            // Generating the verification code and setting to the user
            String uuid = UUID.randomUUID().toString();
            String verificationCode = hashedUUID(uuid);
            user.setVerificationCode(verificationCode);

            // Saving the user to database
            User savedUser = this.userService.saveUser(user);

            // Sending the verification email to the user
            sendVerificationEmail(savedUser, request);

            model.addAttribute("user", savedUser);

        } catch (Exception e) {
            model.addAttribute("user", user);
            session.setAttribute("message", new Messages("Something went wrong!! " + e.getMessage(), "alert-danger"));
            return "signup";
        }

        return "registration-success";
    }

    // Sending the verification code to user
    private void sendVerificationEmail(User savedUser, HttpServletRequest request) throws MessagingException {
        String toEmail = savedUser.getEmail();
        String subject = "Please Verify your registration";
        String mailContent = messageContent(savedUser, request);

        // Finally Sending the Verification email
        emailSender.sendEmail(toEmail, subject, mailContent);
    }

    // Verifying Process
    @GetMapping("form-process/verify")
    public String verifyUser(@RequestParam String code, HttpSession session) {
        if (userService.isUserVerified(code)) {
            // User Verify successfully
            session.setAttribute("message", new Messages("Account Verified Successfully.", "alert-success"));
        } else {
            session.setAttribute("message", new Messages("Something went wrong or Invalid verification code!", "alert-danger"));
        }
        return "redirect:/login";
    }

    // Body of the Verification email
    private String messageContent(User user, HttpServletRequest request) {
        String mailContent = "<p style=\"font-size: 16px;\">Dear " + user.getName() + ",</p>";
        mailContent += "<p style=\"font-size: 16px;\">Please click the link below to verify your registration:</p>";

        String verifyUrl = request.getRequestURL().toString();
        verifyUrl += "/verify?code=" + user.getVerificationCode();

        mailContent += "<h3 style=\"font-size: 18px;\"><a href=\"" + verifyUrl + "\">VERIFY</a></h3>";
        mailContent += "<p style=\"font-size: 14px;\"><strong>This verification link will expire in one minute. " +
                "If you don't verify your registration within one minute, " +
                "you will need to register again.</strong></p>";
        mailContent += "<p style=\"font-size: 16px;\"><strong>Thank You.</strong><br>The <strong>NotesVenture Team</strong></p>";
        return mailContent;
    }

    // Hashing the UUID for security
    private String hashedUUID(String uuid) {
        return DigestUtils.sha256Hex(uuid);
    }

    // Notes creating process.
    @PostMapping("/user/creating-notes")
    public String creatingNotes(@ModelAttribute Note note, Principal principal, HttpSession session) {
        String email = principal.getName();

        User user = this.userService.getUser(email);

        // if user filled the title and description empty!
        if (note.getTitle().trim().isEmpty() && note.getDescription().trim().isEmpty()) {
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
    public String updateNotes(@RequestParam Long noteId, String action, @ModelAttribute Note note) {

        Note oldNote = noteService.getNotes(noteId);

        if (action != null && action.equals("delete") || note.getTitle().trim().isEmpty() && note.getDescription().trim().isEmpty()) {
            // Deleting Note Process

            // Setting the value to new trash note
            TrashNote trashNote = new TrashNote();
            trashNote.setTitle(oldNote.getTitle());
            trashNote.setDescription(oldNote.getDescription());
            trashNote.setNotesCreatedDate(oldNote.getNotesCreatedDate());
            trashNote.setNotesUpdatedDate(oldNote.getNotesUpdatedDate());
            trashNote.setNoteDeletedDate(LocalDateTime.now());
            trashNote.setUser(oldNote.getUser());

            // Saving the old note on the trash note
            this.trashNoteService.saveTrashNote(trashNote);

            // Now Deleting the old note
            this.noteService.deleteNote(noteId);
        } else {
            // Updating Note Process
            oldNote.setTitle(note.getTitle());
            oldNote.setDescription(note.getDescription());
            oldNote.setNotesUpdatedDate(LocalDateTime.now());

            // Saving the note to database
            this.noteService.saveNotes(oldNote);
        }
        return "redirect:/user/0/u/dashboard";
    }


    // Processing the searching method
    @GetMapping("/user/search-notes")
    public String searchNotes(@RequestParam(required = false) String keyword, RedirectAttributes redirectAttributes) {
        if (keyword != null) {
            redirectAttributes.addFlashAttribute("keyword", keyword);
        }
        // Redirect to the dashboard
        return "redirect:/user/0/u/dashboard";

    }
}