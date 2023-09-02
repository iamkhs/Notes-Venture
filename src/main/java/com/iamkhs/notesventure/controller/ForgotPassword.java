package com.iamkhs.notesventure.controller;

import com.iamkhs.notesventure.entities.User;
import com.iamkhs.notesventure.helper.Messages;
import com.iamkhs.notesventure.service.UserService;
import com.iamkhs.notesventure.service.impl.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;

@Controller
public class ForgotPassword {

    private final UserService userService;
    private final EmailSenderService emailService;
    private final PasswordEncoder passwordEncoder;

    private User user;

    public ForgotPassword(UserService userService, EmailSenderService emailService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    // Forgot Password Handler
    @PostMapping("/processing")
    public String forgotPassword(@RequestParam String email, HttpSession session) throws MessagingException {

        user = this.userService.getUser(email);

        if (user != null) {
            String generatedOTP = generateOTP();
            System.err.println("User found");
            String subject = "Password Reset OTP for Your NotesVenture Account";

            // Sending the OTP to the User
            emailService.sendEmail(email, subject,
                    emailBody(user.getName(), generatedOTP));

            // Saving the user in DB with OTP
            saveUser(user, generatedOTP);

            // If one minute has passed then setting the verification code null
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    saveUser(user, null);
                }
            }, 60000); // 60000 milliseconds = 1 minute

            session.setAttribute("message", new Messages("Please Check Your email for the OTP", "alert-success"));

        } else {
            // user is null
            session.setAttribute("message", new Messages("Invalid Email! User not FOUND!", "alert-danger"));
            return "redirect:/forgot-password";
        }

        return "otp-page";
    }

    // Verifying the OTP
    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam String otp, HttpSession session) {
        System.err.println(otp);
        boolean isUserVerified = userService.verifyUserByOTP(otp);
        if (isUserVerified) {
            // User is Verified by OTP
            session.setAttribute("message", new Messages("OTP verified Successfully", "alert-success"));
            return "new-password";
        } else {
            // Invalid OTP provide
            session.setAttribute("message", new Messages("invalid OTP! resend the otp again", "alert-danger"));
            return "otp-page";
        }
    }

    // New password handler
    @PostMapping("/set-new-password")
    public String setNewPassword(@RequestParam String newPassword, HttpSession session) {
        this.user.setPassword(passwordEncoder.encode(newPassword));
        this.userService.saveUser(user);
        session.setAttribute("message", new Messages("Password Changed", "alert-success"));
        return "/login";
    }

    // Generating the OTP
    private String generateOTP() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    // Saving the User to DB
    private void saveUser(User user, String verificationCode) {
        user.setVerificationCode(verificationCode);
        user.setPassword(user.getPassword());
        this.userService.saveUser(user);
    }

    // Email Body
    private String emailBody(String username, String otp) {
        return "<html><body style=\"font-size: 18px;\">" +
                "<p>Dear " + username + ",</p>" +
                "<p><strong>Here is your Forgot Password OTP. Please don't share this OTP with anyone:</strong></p>" +
                "<p>Your OTP: <strong>" + otp + "</strong></p>" +
                "<p>This OTP will expire after 1 minute.</p>" +
                "<p>Thank you,</p>" +
                "<p>NotesVenture</p>" +
                "</body></html>";
    }
}
