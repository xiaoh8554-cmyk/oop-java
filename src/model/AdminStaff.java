package model;

/**
 * Represents Admin Staff in APU Medical Centre.
 * Handles patient registration, reception, scheduling, and billing coordination.
 */
public class AdminStaff extends User {
    private static final long serialVersionUID = 1L;

    private String department;
    private String shift;
    private String staffRank;

    public AdminStaff(String id, String username, String password, String fullName, String email, String phoneNumber,
                      String department, String shift, String staffRank) {
        super(id, username, password, fullName, email, phoneNumber, UserRole.ADMIN_STAFF);
        this.department = department != null ? department : "Administration";
        this.shift = shift != null ? shift : "Morning (08:00 - 16:00)";
        this.staffRank = staffRank != null ? staffRank : "Senior Admin Officer";
    }

    public AdminStaff(String id, String username, String password, String fullName, String email, String phoneNumber,
                      String createdAt, String department, String shift, String staffRank) {
        super(id, username, password, fullName, email, phoneNumber, UserRole.ADMIN_STAFF, createdAt);
        this.department = department;
        this.shift = shift;
        this.staffRank = staffRank;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getStaffRank() {
        return staffRank;
    }

    public void setStaffRank(String staffRank) {
        this.staffRank = staffRank;
    }

    @Override
    public String getRoleSpecificInfo() {
        return String.format("Rank: %s | Dept: %s | Shift: %s", staffRank, department, shift);
    }

    @Override
    public String toChildFileString() {
        return String.join("|",
                getId(),
                getDepartment(),
                getShift(),
                getStaffRank()
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
                getCreatedAt(),
                getDepartment(),
                getShift(),
                getStaffRank()
        );
    }
}
