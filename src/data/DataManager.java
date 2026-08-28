package data;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages user accounts and file persistence (data/users.txt).
 * Supports lookups by username or email address.
 */
public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.txt";

    private final List<User> users = new ArrayList<>();
    private static DataManager instance;

    private DataManager() {
        initDataDirectory();
        loadUsers();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void initDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public synchronized void loadUsers() {
        users.clear();
        File file = new File(USERS_FILE);

        if (!file.exists()) {
            seedDefaultUsers();
            saveUsers();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                User user = parseUser(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("[DataManager] Error reading users file: " + e.getMessage());
            if (users.isEmpty()) {
                seedDefaultUsers();
                saveUsers();
            }
        }

        // If file was empty or had outdated format
        if (users.isEmpty()) {
            seedDefaultUsers();
            saveUsers();
        }
    }

    private User parseUser(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 8) {
            return null;
        }

        try {
            UserRole role = UserRole.valueOf(parts[0].trim().toUpperCase());
            String id = parts[1].trim();
            String username = parts[2].trim();
            String password = parts[3].trim();
            String fullName = parts[4].trim();
            String email = parts[5].trim();
            String phoneNumber = parts[6].trim();
            String createdAt = parts[7].trim();

            switch (role) {
                case ADMIN_STAFF:
                    String department = parts.length > 8 ? parts[8].trim() : "Front Desk & Admissions";
                    String shift = parts.length > 9 ? parts[9].trim() : "Morning (08:00 - 16:00)";
                    String rank = parts.length > 10 ? parts[10].trim() : "Senior Admin Officer";
                    return new AdminStaff(id, username, password, fullName, email, phoneNumber, createdAt, department, shift, rank);

                case MEDICAL_MANAGER:
                    String division = parts.length > 8 ? parts[8].trim() : "Clinical Operations";
                    String title = parts.length > 9 ? parts[9].trim() : "Head of Medical Services";
                    String assignedDept = parts.length > 10 ? parts[10].trim() : "All Hospital Wards";
                    return new MedicalManager(id, username, password, fullName, email, phoneNumber, createdAt, division, title, assignedDept);

                case DOCTOR:
                    String spec = parts.length > 8 ? parts[8].trim() : "Cardiology";
                    String qual = parts.length > 9 ? parts[9].trim() : "MBBS, MD";
                    String room = parts.length > 10 ? parts[10].trim() : "Consultation Room 302";
                    double fee = 150.0;
                    if (parts.length > 11 && !parts[11].trim().isEmpty()) {
                        try {
                            fee = Double.parseDouble(parts[11].trim());
                        } catch (NumberFormatException ignored) {}
                    }
                    return new Doctor(id, username, password, fullName, email, phoneNumber, createdAt, spec, qual, room, fee);

                case PATIENT:
                    String dob = parts.length > 8 ? parts[8].trim() : "1994-06-12";
                    String gender = parts.length > 9 ? parts[9].trim() : "Male";
                    String blood = parts.length > 10 ? parts[10].trim() : "O+";
                    String emerg = parts.length > 11 ? parts[11].trim() : "N/A";
                    String history = parts.length > 12 ? parts[12].trim() : "None";
                    return new Patient(id, username, password, fullName, email, phoneNumber, createdAt, dob, gender, blood, emerg, history);

                default:
                    return null;
            }
        } catch (Exception ex) {
            System.err.println("[DataManager] Error parsing line: " + line + " -> " + ex.getMessage());
            return null;
        }
    }

    public synchronized boolean saveUsers() {
        initDataDirectory();
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            writer.println("# APU Medical Centre - User Accounts Database");
            writer.println("# Format: ROLE|ID|USERNAME|PASSWORD|FULL_NAME|EMAIL|PHONE|CREATED_AT|[ROLE_SPECIFIC_FIELDS...]");
            for (User user : users) {
                writer.println(user.toFileString());
            }
            return true;
        } catch (IOException e) {
            System.err.println("[DataManager] Error writing users file: " + e.getMessage());
            return false;
        }
    }

    private void seedDefaultUsers() {
        users.clear();

        // 1. Admin Staff
        users.add(new AdminStaff(
                "STF-101",
                "admin_staff",
                "admin123",
                "Anna Watson",
                "staff@apumedical.edu.my",
                "+60 14-222 3456",
                "Front Desk & Patient Admissions",
                "Morning (08:00 - 16:00)",
                "Lead Administrative Officer"
        ));

        // 2. Medical Manager
        users.add(new MedicalManager(
                "MGR-201",
                "med_manager",
                "manager123",
                "Prof. Dr. Alex Wong",
                "manager@apumedical.edu.my",
                "+60 12-345 6789",
                "Clinical Services & Governance",
                "Chief Medical Director",
                "Inpatient & Outpatient Divisions"
        ));

        // 3. Doctors
        users.add(new Doctor(
                "DOC-301",
                "dr_smith",
                "doctor123",
                "Dr. Sarah Smith",
                "doctor@apumedical.edu.my",
                "+60 13-888 1234",
                "Cardiology",
                "MD, FACC, FRCP",
                "Consultation Suite 302",
                180.00
        ));
        users.add(new Doctor(
                "DOC-302",
                "dr_lee",
                "doctor123",
                "Dr. Michael Lee",
                "m.lee@apumedical.edu.my",
                "+60 17-777 5678",
                "Pediatrics & Child Health",
                "MBBS, MRCPCH",
                "Pediatric Clinic 108",
                120.00
        ));

        // 4. Patients
        users.add(new Patient(
                "PAT-401",
                "patient_john",
                "patient123",
                "Johnathan Doe",
                "john.doe@gmail.com",
                "+60 18-999 1122",
                "1992-08-14",
                "Male",
                "O+",
                "+60 18-999 3344 (Mary Doe - Spouse)",
                "Hypertension (Stage 1), Penicillin Allergy"
        ));
        users.add(new Patient(
                "PAT-402",
                "patient_emily",
                "patient123",
                "Emily Chen",
                "emily.chen@gmail.com",
                "+60 11-555 7788",
                "1998-03-22",
                "Female",
                "B+",
                "+60 11-555 9900 (Chen Wei - Father)",
                "Asthma (Mild intermittent), No known allergies"
        ));
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Finds a user by either username OR email address (case-insensitive).
     */
    public User findByUsernameOrEmail(String identifier) {
        if (identifier == null) return null;
        String query = identifier.trim().toLowerCase();
        for (User u : users) {
            if (u.getUsername().toLowerCase().equals(query) || 
                u.getEmail().toLowerCase().equals(query)) {
                return u;
            }
        }
        return null;
    }

    public void addUser(User user) {
        if (user != null) {
            users.add(user);
            saveUsers();
        }
    }
}
