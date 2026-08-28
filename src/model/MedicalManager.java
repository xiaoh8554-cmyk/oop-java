package model;

/**
 * Represents a Medical Manager in APU Medical Centre.
 * Supervises clinical departments, oversees doctor assignments, health metric policies, and analytics.
 */
public class MedicalManager extends User {
    private static final long serialVersionUID = 1L;

    private String division;
    private String managementTitle;
    private String assignedDepartment;

    public MedicalManager(String id, String username, String password, String fullName, String email, String phoneNumber,
                          String division, String managementTitle, String assignedDepartment) {
        super(id, username, password, fullName, email, phoneNumber, UserRole.MEDICAL_MANAGER);
        this.division = division != null ? division : "Clinical Operations";
        this.managementTitle = managementTitle != null ? managementTitle : "Medical Services Director";
        this.assignedDepartment = assignedDepartment != null ? assignedDepartment : "All Clinical Divisions";
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getManagementTitle() {
        return managementTitle;
    }

    public void setManagementTitle(String managementTitle) {
        this.managementTitle = managementTitle;
    }

    public String getAssignedDepartment() {
        return assignedDepartment;
    }

    public void setAssignedDepartment(String assignedDepartment) {
        this.assignedDepartment = assignedDepartment;
    }

    @Override
    public String getRoleSpecificInfo() {
        return String.format("Title: %s | Division: %s | Oversight: %s", managementTitle, division, assignedDepartment);
    }

    @Override
    public String toChildFileString() {
        return String.join("|",
                getId(),
                getDivision(),
                getManagementTitle(),
                getAssignedDepartment()
        );
    }

    @Override
    public String toFileString() {
        return String.join("|",
                getRole().name(),
                getId(),
                getUsername(),
                getPassword(),
                getFullName(),
                getEmail(),
                getPhoneNumber(),
                getDivision(),
                getManagementTitle(),
                getAssignedDepartment()
        );
    }
}
