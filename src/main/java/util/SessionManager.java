package util;

import Models.User;

/**
 * Session manager to maintain current logged-in user across scenes
 */
public class SessionManager {

    private static User currentUser;

    /**
     * Set the current logged-in user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Get the current logged-in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Clear the current session
     */
    public static void clearSession() {
        currentUser = null;
    }

    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
