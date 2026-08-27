package data;

import model.User;

/**
 * Service handling authentication by username or email address.
 * Automatically resolves the user's role upon successful login.
 */
public class AuthService {
    private static AuthService instance;
    private final DataManager dataManager;
    private User currentUser;

    public enum AuthStatus {
        SUCCESS("Login successful"),
        USER_NOT_FOUND("No account found with this username or email"),
        INVALID_PASSWORD("Incorrect password entered"),
        EMPTY_FIELDS("Please enter both username/email and password");

        private final String message;

        AuthStatus(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private AuthService() {
        this.dataManager = DataManager.getInstance();
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Authenticates user using either their username OR email address.
     *
     * @param identifier Username or Email address
     * @param password   User's password
     * @return AuthStatus outcome
     */
    public AuthStatus login(String identifier, String password) {
        if (identifier == null || identifier.trim().isEmpty() || password == null || password.isEmpty()) {
            return AuthStatus.EMPTY_FIELDS;
        }

        User user = dataManager.findByUsernameOrEmail(identifier.trim());
        if (user == null) {
            return AuthStatus.USER_NOT_FOUND;
        }

        if (!user.getPassword().equals(password)) {
            return AuthStatus.INVALID_PASSWORD;
        }

        this.currentUser = user;
        return AuthStatus.SUCCESS;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
