package data;

import model.*;

import java.io.*;
import java.util.*;

/**
 * Manages user accounts and parent-child relational file persistence.
 *
 * Architecture:
 * - Parent Table: data/users.txt
 *     (ROLE|ID|USERNAME|PASSWORD|FULL_NAME|EMAIL|PHONE|CREATED_AT)
 * - Child Tables (linked by ID):
 *     1. data/admin_staff.txt      (ID|DEPARTMENT|SHIFT|STAFF_RANK)
 *     2. data/medical_managers.txt (ID|DIVISION|MANAGEMENT_TITLE|ASSIGNED_DEPARTMENT)
 *     3. data/doctors.txt          (ID|SPECIALIZATION|QUALIFICATION|ROOM_NUMBER|CONSULTATION_FEE)
 *     4. data/patients.txt         (ID|DATE_OF_BIRTH|GENDER|BLOOD_GROUP|EMERGENCY_CONTACT|MEDICAL_HISTORY)
 */
public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.txt";
    private static final String ADMIN_STAFF_FILE = DATA_DIR + File.separator + "admin_staff.txt";
    private static final String MEDICAL_MANAGERS_FILE = DATA_DIR + File.separator + "medical_managers.txt";
    private static final String DOCTORS_FILE = DATA_DIR + File.separator + "doctors.txt";
    private static final String PATIENTS_FILE = DATA_DIR + File.separator + "patients.txt";

    private final List<User> users = new ArrayList<>();
    private static DataManager instance;

    /**
     * Temporary holding structure for base user records read from parent table.
     */
    private static class BaseUserData {
    UserRole role;
    String id;
    String username;
    String password;
    String fullName;
    String email;
    String phoneNumber;

    BaseUserData(UserRole role, String id, String username, String password,
                 String fullName, String email, String phoneNumber) {
        this.role = role;
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}


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

    /**
     * Loads users using parent-child relational joins on ID.
     */
    public synchronized void loadUsers() {
        users.clear();
        File usersFile = new File(USERS_FILE);
        if (!usersFile.exists()) {
            return;
        }

        // Step 1: Read parent users.txt table
        Map<String, BaseUserData> baseUserMap = new LinkedHashMap<>();
        List<User> monolithicFallbackList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 7) {
                    try {
                        UserRole role = UserRole.valueOf(parts[0].trim().toUpperCase());
                        String id = parts[1].trim();
                        String username = parts[2].trim();
                        String password = parts[3].trim();
                        String fullName = parts[4].trim();
                        String email = parts[5].trim();
                        String phone = parts[6].trim();

                        baseUserMap.put(id, new BaseUserData(role, id, username, password, fullName, email, phone));
                    } catch (Exception ex) {
                        System.err.println("[DataManager] Error parsing parent user: " + line + " -> " + ex.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[DataManager] Error reading users.txt: " + e.getMessage());
        }

        // Step 2: Read child tables and perform join on ID
        Set<String> processedIds = new HashSet<>();

        // 2a. Admin Staff child table
        loadAdminStaffChildren(baseUserMap, processedIds);

        // 2b. Medical Managers child table
        loadMedicalManagerChildren(baseUserMap, processedIds);

        // 2c. Doctors child table
        loadDoctorChildren(baseUserMap, processedIds);

        // 2d. Patients child table
        loadPatientChildren(baseUserMap, processedIds);

        // Step 3: Handle any un-joined parent records with sensible defaults
        for (Map.Entry<String, BaseUserData> entry : baseUserMap.entrySet()) {
            if (!processedIds.contains(entry.getKey())) {
                BaseUserData b = entry.getValue();
                switch (b.role) {
                    case ADMIN_STAFF:
                        users.add(new AdminStaff(b.id, b.username, b.password, b.fullName, b.email, b.phoneNumber,
                                "Administration", "Morning (08:00 - 16:00)", "Admin Officer"));
                        break;
                    case MEDICAL_MANAGER:
                        users.add(new MedicalManager(b.id, b.username, b.password, b.fullName, b.email, b.phoneNumber,
                                "Clinical Operations", "Medical Director", "General"));
                        break;
                    case DOCTOR:
                        users.add(new Doctor(b.id, b.username, b.password, b.fullName, b.email, b.phoneNumber,
                                "General Medicine", "MBBS", "Room 101", 150.0));
                        break;
                    case PATIENT:
                        users.add(new Patient(b.id, b.username, b.password, b.fullName, b.email, b.phoneNumber,
                                "2000-01-01", "Unspecified", "O+", "N/A", "None"));
                        break;
                }
            }
        }
    }

    private void loadAdminStaffChildren(Map<String, BaseUserData> baseUserMap, Set<String> processedIds) {
        File file = new File(ADMIN_STAFF_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 4) {
                    String id = parts[0].trim();
                    BaseUserData b = baseUserMap.get(id);
                    if (b != null && b.role == UserRole.ADMIN_STAFF) {
                        String dept = parts[1].trim();
                        String shift = parts[2].trim();
                        String rank = parts[3].trim();
                        users.add(new AdminStaff(b.id, b.username, b.password, b.fullName, b.email, b.phoneNumber, dept, shift, rank));
                        processedIds.add(id);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[DataManager] Error reading admin_staff.txt: " + e.getMessage());
        }
    }

    private void loadMedicalManagerChildren(Map<String, BaseUserData> baseUserMap, Set<String> processedIds) {
        File file = new File(MEDICAL_MANAGERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 4) {
                    String id = parts[0].trim();
                    BaseUserData b = baseUserMap.get(id);
                    if (b != null && b.role == UserRole.MEDICAL_MANAGER) {
                        String division = parts[1].trim();
                        String title = parts[2].trim();
                        String assignedDept = parts[3].trim();
                        users.add(new MedicalManager(b.id, b.username, b.password, b.fullName, b.email, b.phoneNumber, division, title, assignedDept));
                        processedIds.add(id);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[DataManager] Error reading medical_managers.txt: " + e.getMessage());
        }
    }

    private void loadDoctorChildren(Map<String, BaseUserData> baseUserMap, Set<String> processedIds) {
        File file = new File(DOCTORS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 5) {
                    String id = parts[0].trim();
                    BaseUserData b = baseUserMap.get(id);
                    if (b != null && b.role == UserRole.DOCTOR) {
                        String spec = parts[1].trim();
                        String qual = parts[2].trim();
                        String room = parts[3].trim();
                        double fee = 150.0;
                        try {
                            fee = Double.parseDouble(parts[4].trim());
                        } catch (NumberFormatException ignored) {}
                        users.add(new Doctor(b.id, b.username, b.password, b.fullName, b.email, b.phoneNumber, spec, qual, room, fee));
                        processedIds.add(id);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[DataManager] Error reading doctors.txt: " + e.getMessage());
        }
    }

    private void loadPatientChildren(Map<String, BaseUserData> baseUserMap, Set<String> processedIds) {
        File file = new File(PATIENTS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 6) {
                    String id = parts[0].trim();
                    BaseUserData b = baseUserMap.get(id);
                    if (b != null && b.role == UserRole.PATIENT) {
                        String dob = parts[1].trim();
                        String gender = parts[2].trim();
                        String blood = parts[3].trim();
                        String emerg = parts[4].trim();
                        String history = parts[5].trim();
                        users.add(new Patient(b.id, b.username, b.password, b.fullName, b.email, b.phoneNumber, dob, gender, blood, emerg, history));
                        processedIds.add(id);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[DataManager] Error reading patients.txt: " + e.getMessage());
        }
    }

    private User parseMonolithicUser(String[] parts, UserRole role, String id, String username,
                                     String password, String fullName, String email, String phone, String createdAt) {
        switch (role) {
            case ADMIN_STAFF:
                String dept = parts.length > 8 ? parts[8].trim() : "Front Desk & Admissions";
                String shift = parts.length > 9 ? parts[9].trim() : "Morning (08:00 - 16:00)";
                String rank = parts.length > 10 ? parts[10].trim() : "Senior Admin Officer";
                return new AdminStaff(id, username, password, fullName, email, phone, createdAt, dept, shift, rank);

            case MEDICAL_MANAGER:
                String division = parts.length > 8 ? parts[8].trim() : "Clinical Operations";
                String title = parts.length > 9 ? parts[9].trim() : "Head of Medical Services";
                String assignedDept = parts.length > 10 ? parts[10].trim() : "All Hospital Wards";
                return new MedicalManager(id, username, password, fullName, email, phone, division, title, assignedDept);

            case DOCTOR:
                String spec = parts.length > 8 ? parts[8].trim() : "Cardiology";
                String qual = parts.length > 9 ? parts[9].trim() : "MBBS, MD";
                String room = parts.length > 10 ? parts[10].trim() : "Consultation Room 302";
                double fee = 150.0;
                if (parts.length > 11 && !parts[11].trim().isEmpty()) {
                    try { fee = Double.parseDouble(parts[11].trim()); } catch (NumberFormatException ignored) {}
                }
                return new Doctor(id, username, password, fullName, email, phone, spec, qual, room, fee);

            case PATIENT:
                String dob = parts.length > 8 ? parts[8].trim() : "1994-06-12";
                String gender = parts.length > 9 ? parts[9].trim() : "Male";
                String blood = parts.length > 10 ? parts[10].trim() : "O+";
                String emerg = parts.length > 11 ? parts[11].trim() : "N/A";
                String history = parts.length > 12 ? parts[12].trim() : "None";
                return new Patient(id, username, password, fullName, email, phone, dob, gender, blood, emerg, history);

            default:
                return null;
        }
    }

    /**
     * Persists all user accounts across parent (users.txt) and the four child tables.
     */
    public synchronized boolean saveUsers() {
        initDataDirectory();
        try {
            // 1. Save Parent table (users.txt)
            try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
                writer.println("# APU Medical Centre - Parent Users Table (Authentication & Common Info)");
                writer.println("# Format: ROLE|ID|USERNAME|PASSWORD|FULL_NAME|EMAIL|PHONE");
                for (User user : users) {
                    writer.println(user.toBaseFileString());
                }
            }

            // 2. Save Admin Staff child table (admin_staff.txt)
            try (PrintWriter writer = new PrintWriter(new FileWriter(ADMIN_STAFF_FILE))) {
                writer.println("# APU Medical Centre - Admin Staff Child Table");
                writer.println("# Format: ID|DEPARTMENT|SHIFT|STAFF_RANK");
                for (User user : users) {
                    if (user instanceof AdminStaff) {
                        writer.println(user.toChildFileString());
                    }
                }
            }

            // 3. Save Medical Managers child table (medical_managers.txt)
            try (PrintWriter writer = new PrintWriter(new FileWriter(MEDICAL_MANAGERS_FILE))) {
                writer.println("# APU Medical Centre - Medical Managers Child Table");
                writer.println("# Format: ID|DIVISION|MANAGEMENT_TITLE|ASSIGNED_DEPARTMENT");
                for (User user : users) {
                    if (user instanceof MedicalManager) {
                        writer.println(user.toChildFileString());
                    }
                }
            }

            // 4. Save Doctors child table (doctors.txt)
            try (PrintWriter writer = new PrintWriter(new FileWriter(DOCTORS_FILE))) {
                writer.println("# APU Medical Centre - Doctors Child Table");
                writer.println("# Format: ID|SPECIALIZATION|QUALIFICATION|ROOM_NUMBER|CONSULTATION_FEE");
                for (User user : users) {
                    if (user instanceof Doctor) {
                        writer.println(user.toChildFileString());
                    }
                }
            }

            // 5. Save Patients child table (patients.txt)
            try (PrintWriter writer = new PrintWriter(new FileWriter(PATIENTS_FILE))) {
                writer.println("# APU Medical Centre - Patients Child Table");
                writer.println("# Format: ID|DATE_OF_BIRTH|GENDER|BLOOD_GROUP|EMERGENCY_CONTACT|MEDICAL_HISTORY");
                for (User user : users) {
                    if (user instanceof Patient) {
                        writer.println(user.toChildFileString());
                    }
                }
            }

            return true;
        } catch (IOException e) {
            System.err.println("[DataManager] Error saving relational files: " + e.getMessage());
            return false;
        }
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

    public boolean isUsernameTaken(String username) {
        if (username == null) return false;
        String query = username.trim().toLowerCase();
        for (User u : users) {
            if (u.getUsername().toLowerCase().equals(query)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmailTaken(String email) {
        if (email == null) return false;
        String query = email.trim().toLowerCase();
        for (User u : users) {
            if (u.getEmail().toLowerCase().equals(query)) {
                return true;
            }
        }
        return false;
    }

    public synchronized String generateNextPatientId() {
        int maxId = 400;
        for (User u : users) {
            if (u instanceof Patient && u.getId() != null && u.getId().startsWith("PAT-")) {
                try {
                    int num = Integer.parseInt(u.getId().substring(4));
                    if (num > maxId) {
                        maxId = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("PAT-%03d", maxId + 1);
    }

    public void addUser(User user) {
        if (user != null) {
            users.add(user);
            saveUsers();
        }
    }
}
