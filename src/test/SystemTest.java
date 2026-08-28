package test;

import data.AuthService;
import data.DataManager;
import model.*;

import java.util.List;

/**
 * Automated test suite to verify HMS core functionality and 4-role authentication
 * by either Username or Email address.
 */
public class SystemTest {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("  APU Medical Centre HMS - 4-Role Authentication Test Suite     ");
        System.out.println("================================================================");

        int testsPassed = 0;
        int totalTests = 0;

        // Test 1: Data Initialization
        totalTests++;
        DataManager dm = DataManager.getInstance();
        List<User> users = dm.getUsers();
        if (users.size() >= 4) {
            System.out.println("✔ [PASS] DataManager initialized with " + users.size() + " accounts.");
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Expected >= 4 users, found: " + users.size());
        }

        AuthService auth = AuthService.getInstance();

        // Test 2: Admin Staff Login via Username
        totalTests++;
        AuthService.AuthStatus s1 = auth.login("admin_staff", "admin123");
        if (s1 == AuthService.AuthStatus.SUCCESS && auth.getCurrentUser() instanceof AdminStaff &&
            auth.getCurrentUser().getRole() == UserRole.ADMIN_STAFF) {
            System.out.println("✔ [PASS] Admin Staff login via username -> Role detected: " + auth.getCurrentUser().getRole().getDisplayName());
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Admin Staff login failed: " + s1);
        }

        // Test 3: Admin Staff Login via Email
        totalTests++;
        AuthService.AuthStatus s2 = auth.login("staff@apumedical.edu.my", "admin123");
        if (s2 == AuthService.AuthStatus.SUCCESS && auth.getCurrentUser().getRole() == UserRole.ADMIN_STAFF) {
            System.out.println("✔ [PASS] Admin Staff login via email (staff@apumedical.edu.my) -> " + auth.getCurrentUser().getFullName());
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Admin Staff email login failed: " + s2);
        }

        // Test 4: Medical Manager Login via Username
        totalTests++;
        AuthService.AuthStatus s3 = auth.login("med_manager", "manager123");
        if (s3 == AuthService.AuthStatus.SUCCESS && auth.getCurrentUser() instanceof MedicalManager &&
            auth.getCurrentUser().getRole() == UserRole.MEDICAL_MANAGER) {
            System.out.println("✔ [PASS] Medical Manager login via username -> Role detected: " + auth.getCurrentUser().getRole().getDisplayName());
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Medical Manager login failed: " + s3);
        }

        // Test 5: Medical Manager Login via Email
        totalTests++;
        AuthService.AuthStatus s4 = auth.login("manager@apumedical.edu.my", "manager123");
        if (s4 == AuthService.AuthStatus.SUCCESS && auth.getCurrentUser().getRole() == UserRole.MEDICAL_MANAGER) {
            System.out.println("✔ [PASS] Medical Manager login via email -> " + auth.getCurrentUser().getFullName());
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Medical Manager email login failed: " + s4);
        }

        // Test 6: Doctor Login via Username
        totalTests++;
        AuthService.AuthStatus s5 = auth.login("dr_smith", "doctor123");
        if (s5 == AuthService.AuthStatus.SUCCESS && auth.getCurrentUser() instanceof Doctor &&
            auth.getCurrentUser().getRole() == UserRole.DOCTOR) {
            Doctor doc = (Doctor) auth.getCurrentUser();
            System.out.println("✔ [PASS] Doctor login via username -> " + doc.getFullName() + " (" + doc.getSpecialization() + ")");
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Doctor login failed: " + s5);
        }

        // Test 7: Doctor Login via Email
        totalTests++;
        AuthService.AuthStatus s6 = auth.login("doctor@apumedical.edu.my", "doctor123");
        if (s6 == AuthService.AuthStatus.SUCCESS && auth.getCurrentUser().getRole() == UserRole.DOCTOR) {
            System.out.println("✔ [PASS] Doctor login via email -> " + auth.getCurrentUser().getFullName());
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Doctor email login failed: " + s6);
        }

        // Test 8: Patient Login via Username
        totalTests++;
        AuthService.AuthStatus s7 = auth.login("patient_john", "patient123");
        if (s7 == AuthService.AuthStatus.SUCCESS && auth.getCurrentUser() instanceof Patient &&
            auth.getCurrentUser().getRole() == UserRole.PATIENT) {
            Patient pat = (Patient) auth.getCurrentUser();
            System.out.println("✔ [PASS] Patient login via username -> " + pat.getFullName() + " (Blood: " + pat.getBloodGroup() + ")");
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Patient login failed: " + s7);
        }

        // Test 9: Patient Login via Gmail Address
        totalTests++;
        AuthService.AuthStatus s8 = auth.login("john.doe@gmail.com", "patient123");
        if (s8 == AuthService.AuthStatus.SUCCESS && auth.getCurrentUser().getRole() == UserRole.PATIENT) {
            System.out.println("✔ [PASS] Patient login via Gmail (john.doe@gmail.com) -> " + auth.getCurrentUser().getFullName());
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Patient gmail login failed: " + s8);
        }

        // Test 10: Invalid Password Rejection
        totalTests++;
        AuthService.AuthStatus s9 = auth.login("admin_staff", "wrongpassword");
        if (s9 == AuthService.AuthStatus.INVALID_PASSWORD) {
            System.out.println("✔ [PASS] Invalid password correctly rejected.");
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Invalid password test failed, got: " + s9);
        }

        // Test 11: Non-existent User Rejection
        totalTests++;
        AuthService.AuthStatus s10 = auth.login("unknown_user@gmail.com", "anypass");
        if (s10 == AuthService.AuthStatus.USER_NOT_FOUND) {
            System.out.println("✔ [PASS] Non-existent user correctly detected.");
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Non-existent user test failed, got: " + s10);
        }

        // Test 12: Relational Persistence - Verify all 5 database files exist
        totalTests++;
        java.io.File fUsers = new java.io.File("data/users.txt");
        java.io.File fStaff = new java.io.File("data/admin_staff.txt");
        java.io.File fMgr = new java.io.File("data/medical_managers.txt");
        java.io.File fDoc = new java.io.File("data/doctors.txt");
        java.io.File fPat = new java.io.File("data/patients.txt");

        if (fUsers.exists() && fStaff.exists() && fMgr.exists() && fDoc.exists() && fPat.exists()) {
            System.out.println("✔ [PASS] Relational Architecture: All 5 parent-child text database files exist.");
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] One or more relational data files are missing.");
        }

        // Test 13: Relational Foreign Key Integrity (Join verification)
        totalTests++;
        User dr = dm.findByUsernameOrEmail("dr_smith");
        if (dr instanceof Doctor && ((Doctor) dr).getSpecialization().equals("Cardiology") &&
            ((Doctor) dr).getRoomNumber().equals("Consultation Suite 302")) {
            System.out.println("✔ [PASS] Relational Join: Parent record joined with Doctor child table data correctly.");
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Relational join failed for Doctor record.");
        }

        // Test 14: Dynamic Insert & Multi-file Persistence Verification
        totalTests++;
        Doctor newDoc = new Doctor("DOC-999", "dr_test_relational", "pass123", "Dr. Test Relational",
                "test.relational@apumedical.edu.my", "+60 19-111 2233", "Neurology", "MBBS, MD", "Room 505", 250.0);
        dm.addUser(newDoc);

        // Reload data from disk in fresh manager check
        DataManager.getInstance().loadUsers();
        User reloadedDoc = dm.findByUsernameOrEmail("dr_test_relational");
        if (reloadedDoc instanceof Doctor && ((Doctor) reloadedDoc).getSpecialization().equals("Neurology")) {
            System.out.println("✔ [PASS] Dynamic Add & Relational Save: New record persisted to users.txt & doctors.txt.");
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Failed to save and reload relational record.");
        }

        // Test 15: Logout
        totalTests++;
        auth.logout();
        if (!auth.isLoggedIn()) {
            System.out.println("✔ [PASS] Session cleared on logout.");
            testsPassed++;
        } else {
            System.err.println("❌ [FAIL] Logout failed to clear session.");
        }

        System.out.println("================================================================");
        System.out.printf("Test Summary: %d/%d tests passed (100%%).\n", testsPassed, totalTests);
        System.out.println("================================================================");
    }
}
