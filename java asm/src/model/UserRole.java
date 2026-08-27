package model;

import java.awt.Color;

/**
 * Represents the four defined user roles in APU Medical Centre HMS:
 * 1. Admin Staff
 * 2. Medical Managers
 * 3. Doctors
 * 4. Patients
 */
public enum UserRole {
    ADMIN_STAFF("Admin Staff", "Hospital Administration & Front-Desk Operations", new Color(0x1E, 0x3A, 0x8A)),      // Deep Navy
    MEDICAL_MANAGER("Medical Manager", "Medical Supervision, Quality & Analytics", new Color(0x7C, 0x3A, 0xED)),    // Purple / Indigo
    DOCTOR("Doctor", "Physician / Healthcare Professional", new Color(0x0D, 0x94, 0x88)),                             // Medical Teal
    PATIENT("Patient", "Patient Portal & Medical Records", new Color(0x02, 0x84, 0xC7));                               // Ocean Blue

    private final String displayName;
    private final String description;
    private final Color badgeColor;

    UserRole(String displayName, String description, Color badgeColor) {
        this.displayName = displayName;
        this.description = description;
        this.badgeColor = badgeColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Color getBadgeColor() {
        return badgeColor;
    }

    public static UserRole fromString(String roleStr) {
        if (roleStr == null) return null;
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(roleStr.trim()) || 
                role.getDisplayName().equalsIgnoreCase(roleStr.trim())) {
                return role;
            }
        }
        return null;
    }
}
