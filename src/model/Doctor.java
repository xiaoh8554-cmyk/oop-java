package model;

/**
 * Represents a Physician / Doctor in APU Medical Centre.
 */
public class Doctor extends User {
    private static final long serialVersionUID = 1L;

    private String specialization;
    private String qualification;
    private String roomNumber;
    private double consultationFee;

    public Doctor(String id, String username, String password, String fullName, String email, String phoneNumber,
                  String specialization, String qualification, String roomNumber, double consultationFee) {
        super(id, username, password, fullName, email, phoneNumber, UserRole.DOCTOR);
        this.specialization = specialization != null ? specialization : "General Medicine";
        this.qualification = qualification != null ? qualification : "MBBS";
        this.roomNumber = roomNumber != null ? roomNumber : "Room 101";
        this.consultationFee = consultationFee;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    @Override
    public String getRoleSpecificInfo() {
        return String.format("Specialization: %s | Room: %s | Fee: RM %.2f", specialization, roomNumber, consultationFee);
    }

    @Override
    public String toChildFileString() {
        return String.join("|",
                getId(),
                getSpecialization(),
                getQualification(),
                getRoomNumber(),
                String.valueOf(getConsultationFee())
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
                getSpecialization(),
                getQualification(),
                getRoomNumber(),
                String.valueOf(getConsultationFee())
        );
    }
}
