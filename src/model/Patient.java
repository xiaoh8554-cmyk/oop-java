package model;

/**
 * Represents a Patient registered at APU Medical Centre.
 */
public class Patient extends User {
    private static final long serialVersionUID = 1L;

    private String dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String emergencyContact;
    private String medicalHistorySummary;

    public Patient(String id, String username, String password, String fullName, String email, String phoneNumber,
                   String dateOfBirth, String gender, String bloodGroup, String emergencyContact, String medicalHistorySummary) {
        super(id, username, password, fullName, email, phoneNumber, UserRole.PATIENT);
        this.dateOfBirth = dateOfBirth != null ? dateOfBirth : "2000-01-01";
        this.gender = gender != null ? gender : "Unspecified";
        this.bloodGroup = bloodGroup != null ? bloodGroup : "O+";
        this.emergencyContact = emergencyContact != null ? emergencyContact : "N/A";
        this.medicalHistorySummary = medicalHistorySummary != null ? medicalHistorySummary : "None";
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getMedicalHistorySummary() {
        return medicalHistorySummary;
    }

    public void setMedicalHistorySummary(String medicalHistorySummary) {
        this.medicalHistorySummary = medicalHistorySummary;
    }

    @Override
    public String getRoleSpecificInfo() {
        return String.format("DOB: %s | Gender: %s | Blood: %s", dateOfBirth, gender, bloodGroup);
    }

    @Override
    public String toChildFileString() {
        return String.join("|",
                getId(),
                getDateOfBirth(),
                getGender(),
                getBloodGroup(),
                getEmergencyContact(),
                getMedicalHistorySummary()
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
                getDateOfBirth(),
                getGender(),
                getBloodGroup(),
                getEmergencyContact(),
                getMedicalHistorySummary()
        );
    }
}
