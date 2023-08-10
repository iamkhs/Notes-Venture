package com.iamkhs.notesventure.exceptions;

public class UserAlreadyRegisteredException extends Exception{

    public UserAlreadyRegisteredException() {
        super("User is already registered with this Email!");
    }

    public UserAlreadyRegisteredException(String message) {
        super(message);
    }
}
