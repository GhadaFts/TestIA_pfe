package com.testai.projectservice.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("Utilisateur non trouvé avec l'ID : " + userId);
    }

    public UserNotFoundException(String userId, Throwable cause) {
        super("Utilisateur non trouvé avec l'ID : " + userId, cause);
    }
}
