package entity;

import constants.UserRoles;

/**
 * Database entity for User Roles
 */
public class UserRole {
    /**
     * Variable for user's username
     */
    private String parentUser;
    /**
     * Variable for user's user profile name
     */
    private String username;
    /**
     * Variable for username's role
     */
    private UserRoles role;

    public String getParentUser() {
        return parentUser;
    }

    public String getUsername() {
        return username;
    }

    public UserRoles getRole() {
        return role;
    }

    public void setParentUser(String userParent) {
        this.parentUser = userParent;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(UserRoles role) {
        this.role = role;
    }
}
