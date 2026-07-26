package com.gym.model.user;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Admin {
    
    // ===== FIELDS =====
    private String adminId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role;
    private String status;
    private String joinDate;
    private String lastLoginDate;
    private int loginAttempts;
    private boolean isLocked;
    private List<String> permissions;
    private String department;
    private String profileImage;
    
    // ===== CONSTANTS =====
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_SUPPORT = "SUPPORT";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Constructor - MATCHES THE CALL
     */
    public Admin(String adminId, String name, String email, String password) {
        this(adminId, name, email, password, null, "ADMIN", "ACTIVE", 
             LocalDate.now().toString());
    }
    
    /**
     * Full constructor with all fields
     */
    public Admin(String adminId, String name, String email, String password, 
                 String phone, String role, String status, String joinDate) {
        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role != null ? role : "ADMIN";
        this.status = status != null ? status : "ACTIVE";
        this.joinDate = joinDate != null ? joinDate : LocalDate.now().toString();
        this.lastLoginDate = "Never";
        this.loginAttempts = 0;
        this.isLocked = false;
        this.permissions = new ArrayList<>();
        this.department = "General";
        initializePermissions();
    }
    
    /**
     * Constructor for creating a super admin
     */
    public Admin(String adminId, String name, String email, String password, String role) {
        this(adminId, name, email, password, null, role, "ACTIVE", 
             LocalDate.now().toString());
        if (ROLE_SUPER_ADMIN.equals(role)) {
            initializeSuperAdminPermissions();
        }
    }
    
    /**
     * Constructor for creating an admin with minimal information
     */
    public Admin(String adminId, String name, String email) {
        this(adminId, name, email, "default123", null, "ADMIN", "ACTIVE", 
             LocalDate.now().toString());
    }
    
    // ===== INITIALIZATION =====
    
    private void initializePermissions() {
        // Basic permissions for all admins
        permissions.add("VIEW_PROFILES");
        permissions.add("VIEW_REPORTS");
        permissions.add("VIEW_DASHBOARD");
        
        if (ROLE_ADMIN.equals(role) || ROLE_SUPER_ADMIN.equals(role)) {
            permissions.add("MANAGE_USERS");
            permissions.add("MANAGE_MEMBERSHIPS");
            permissions.add("MANAGE_CLASSES");
            permissions.add("MANAGE_BOOKINGS");
            permissions.add("MANAGE_PAYMENTS");
        }
        
        if (ROLE_SUPER_ADMIN.equals(role)) {
            permissions.add("MANAGE_ADMINS");
            permissions.add("SYSTEM_CONFIG");
            permissions.add("VIEW_LOGS");
            permissions.add("DELETE_DATA");
            permissions.add("EXPORT_DATA");
            permissions.add("IMPORT_DATA");
        }
        
        if (ROLE_MANAGER.equals(role)) {
            permissions.add("MANAGE_STAFF");
            permissions.add("MANAGE_SCHEDULES");
            permissions.add("MANAGE_ATTENDANCE");
        }
        
        if (ROLE_SUPPORT.equals(role)) {
            permissions.add("VIEW_PROFILES");
            permissions.add("VIEW_REPORTS");
            permissions.add("ASSIST_USERS");
        }
    }
    
    private void initializeSuperAdminPermissions() {
        permissions.clear();
        // All permissions
        String[] allPermissions = {
            "VIEW_PROFILES", "VIEW_REPORTS", "VIEW_DASHBOARD", 
            "MANAGE_USERS", "MANAGE_MEMBERSHIPS", "MANAGE_CLASSES",
            "MANAGE_BOOKINGS", "MANAGE_PAYMENTS", "MANAGE_ADMINS",
            "SYSTEM_CONFIG", "VIEW_LOGS", "DELETE_DATA",
            "EXPORT_DATA", "IMPORT_DATA", "MANAGE_STAFF",
            "MANAGE_SCHEDULES", "MANAGE_ATTENDANCE", "ASSIST_USERS",
            "MANAGE_GYM_EQUIPMENT", "MANAGE_FACILITIES", "SYSTEM_BACKUP"
        };
        for (String perm : allPermissions) {
            permissions.add(perm);
        }
    }
    
    // ===== GETTERS =====
    public String getAdminId() { return adminId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public String getJoinDate() { return joinDate; }
    public String getLastLoginDate() { return lastLoginDate; }
    public int getLoginAttempts() { return loginAttempts; }
    public boolean isLocked() { return isLocked; }
    public List<String> getPermissions() { return new ArrayList<>(permissions); }
    public String getDepartment() { return department; }
    public String getProfileImage() { return profileImage; }
    
    // ===== SETTERS =====
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) { 
        this.role = role;
        initializePermissions();
    }
    public void setStatus(String status) { this.status = status; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }
    public void setDepartment(String department) { this.department = department; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    
    // ===== BUSINESS METHODS =====
    
    /**
     * Check if admin has a specific permission
     */
    public boolean hasPermission(String permission) {
        if (!isActive() || isLocked) {
            return false;
        }
        return permissions.contains(permission);
    }
    
    /**
     * Add a permission
     */
    public boolean addPermission(String permission) {
        if (permission == null || permission.trim().isEmpty()) {
            return false;
        }
        // Super admin cannot have permissions removed but can add more
        if (!permissions.contains(permission)) {
            permissions.add(permission);
            return true;
        }
        return false;
    }
    
    /**
     * Remove a permission
     */
    public boolean removePermission(String permission) {
        // Prevent removing critical permissions from super admin
        if (ROLE_SUPER_ADMIN.equals(role) && 
            ("SYSTEM_CONFIG".equals(permission) || "MANAGE_ADMINS".equals(permission))) {
            System.out.println("⚠️ Cannot remove critical permission from Super Admin.");
            return false;
        }
        return permissions.remove(permission);
    }
    
    /**
     * Record a successful login
     */
    public void recordSuccessfulLogin() {
        this.lastLoginDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"));
        this.loginAttempts = 0;
        this.isLocked = false;
    }
    
    /**
     * Record a failed login attempt
     */
    public void recordFailedLogin() {
        this.loginAttempts++;
        if (loginAttempts >= 5) {
            this.isLocked = true;
            System.out.println("⚠️ Admin account " + adminId + " has been locked due to too many failed attempts.");
        }
    }
    
    /**
     * Unlock admin account
     */
    public boolean unlockAccount() {
        if (!isLocked) {
            return false;
        }
        this.isLocked = false;
        this.loginAttempts = 0;
        System.out.println("✅ Admin account " + adminId + " has been unlocked.");
        return true;
    }
    
    /**
     * Check if admin is active
     */
    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }
    
    /**
     * Activate the admin account
     */
    public void activate() {
        this.status = STATUS_ACTIVE;
        System.out.println("✅ Admin " + adminId + " has been activated.");
    }
    
    /**
     * Deactivate the admin account
     */
    public void deactivate() {
        this.status = STATUS_INACTIVE;
        System.out.println("⚠️ Admin " + adminId + " has been deactivated.");
    }
    
    /**
     * Suspend the admin account
     */
    public void suspend() {
        this.status = STATUS_SUSPENDED;
        System.out.println("⚠️ Admin " + adminId + " has been suspended.");
    }
    
    /**
     * Check if admin can manage users
     */
    public boolean canManageUsers() {
        return hasPermission("MANAGE_USERS") || hasPermission("MANAGE_ADMINS");
    }
    
    /**
     * Check if admin can view reports
     */
    public boolean canViewReports() {
        return hasPermission("VIEW_REPORTS");
    }
    
    /**
     * Check if admin is super admin
     */
    public boolean isSuperAdmin() {
        return ROLE_SUPER_ADMIN.equals(role);
    }
    
    /**
     * Check if admin is manager
     */
    public boolean isManager() {
        return ROLE_MANAGER.equals(role);
    }
    
    /**
     * Check if admin is support staff
     */
    public boolean isSupport() {
        return ROLE_SUPPORT.equals(role);
    }
    
    /**
     * Validate admin credentials
     */
    public boolean validateCredentials(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }
    
    /**
     * Change password
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword)) {
            System.out.println("❌ Current password is incorrect.");
            return false;
        }
        if (newPassword == null || newPassword.length() < 6) {
            System.out.println("❌ New password must be at least 6 characters.");
            return false;
        }
        this.password = newPassword;
        System.out.println("✅ Password changed successfully.");
        return true;
    }
    
    /**
     * Get role display name
     */
    public String getRoleDisplay() {
        return switch (role) {
            case ROLE_SUPER_ADMIN -> "🔴 Super Administrator";
            case ROLE_ADMIN -> "🟠 Administrator";
            case ROLE_MANAGER -> "🔵 Manager";
            case ROLE_SUPPORT -> "🟢 Support Staff";
            default -> "⚪ " + role;
        };
    }
    
    /**
     * Get status display
     */
    public String getStatusDisplay() {
        return switch (status) {
            case STATUS_ACTIVE -> "✅ Active";
            case STATUS_INACTIVE -> "❌ Inactive";
            case STATUS_SUSPENDED -> "⛔ Suspended";
            default -> "❓ " + status;
        };
    }
    
    /**
     * Get formatted join date
     */
    public String getFormattedJoinDate() {
        try {
            LocalDate date = LocalDate.parse(joinDate);
            return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return joinDate;
        }
    }
    
    /**
     * Get days since joined
     */
    public long getDaysSinceJoined() {
        try {
            LocalDate joined = LocalDate.parse(joinDate);
            return java.time.temporal.ChronoUnit.DAYS.between(joined, LocalDate.now());
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get admin profile information
     */
    public String viewProfile() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("         ADMIN PROFILE\n");
        sb.append("═══════════════════════════════════════\n");
        sb.append("Admin ID:    ").append(adminId).append("\n");
        sb.append("Name:        ").append(name).append("\n");
        sb.append("Email:       ").append(email).append("\n");
        sb.append("Phone:       ").append(phone != null ? phone : "N/A").append("\n");
        sb.append("Role:        ").append(getRoleDisplay()).append("\n");
        sb.append("Department:  ").append(department).append("\n");
        sb.append("Status:      ").append(getStatusDisplay()).append("\n");
        sb.append("Join Date:   ").append(getFormattedJoinDate());
        sb.append(" (").append(getDaysSinceJoined()).append(" days ago)\n");
        sb.append("Last Login:  ").append(lastLoginDate).append("\n");
        sb.append("Login Attempts: ").append(loginAttempts).append("\n");
        sb.append("Account:     ").append(isLocked ? "🔒 Locked" : "🔓 Unlocked").append("\n");
        sb.append("Permissions: ").append(permissions.size()).append(" permissions\n");
        if (!permissions.isEmpty()) {
            sb.append("  • ").append(String.join("\n  • ", permissions)).append("\n");
        }
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    /**
     * Get admin summary for display in lists
     */
    public String getSummary() {
        return String.format("%s (%s) - %s, %s, %s",
            name, adminId, getRoleDisplay(), department, getStatusDisplay());
    }
    
    /**
     * Get minimal admin info
     */
    public String getShortInfo() {
        return String.format("%s (%s)", name, adminId);
    }
    
    // ===== OVERRIDE METHODS =====
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Admin admin = (Admin) obj;
        return adminId != null && adminId.equals(admin.adminId);
    }
    
    @Override
    public int hashCode() {
        return adminId != null ? adminId.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return String.format("Admin{id='%s', name='%s', email='%s', role='%s', status='%s'}",
            adminId, name, email, role, status);
    }
}