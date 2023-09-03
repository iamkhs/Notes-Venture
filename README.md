# NotesVenture - Your Personal Note-Taking App

Welcome to NotesVenture, your ultimate note-taking companion! This user-friendly web application, inspired by Google Keep Notes, simplifies your note-taking experience with essential features like creating, updating, and deleting notes.

## Features:

- **Users can create their accounts.**
- **Role-based authentication ensures that admins and regular users are directed to different landing pages upon login.**
- **Email verification is implemented, requiring users to confirm their accounts via email after registration.**
- **User can login with Google account**
- **The app supports note creation, deletion, and updating.**
- **A Trash Note feature is available.**
- **Search functionality for notes is included.**
- **Users can update their profiles.**
- **Users can reset passwords by OTP Verification.**
- **Admin Panel UI Implemented**

## Technologies Used:

### Backend:

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL

### Frontend:

- Thymeleaf
- Html, CSS

## Configuration:

Before running the application, make sure to configure the following settings in your `application.properties` file:

- **MySQL Database Configuration:**
  - Set the database URL, username, and password in `application.properties` to connect to your MySQL database.

- **Google OAuth2 Login Credentials:**
  - Obtain OAuth2 credentials from the Google Developer Console and configure them in `application.properties`.

- **Mail Sender Configuration:**
  - Configure email settings (SMTP host, port, username, and password) for sending email verifications.

Example `application.properties`:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/notes_venture
spring.datasource.username=root
spring.datasource.password=your_password

# Google OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=your_client_id
spring.security.oauth2.client.registration.google.client-secret=your_client_secret

# Mail Sender Configuration
spring.mail.host=your_smtp_host
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_email_password
