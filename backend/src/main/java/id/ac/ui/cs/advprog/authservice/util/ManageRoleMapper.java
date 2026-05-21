package id.ac.ui.cs.advprog.authservice.util;

import java.util.Locale;

/**
 * Maps auth DB role names to claims consumed by palmery-manage and palmery-fe.
 */
public final class ManageRoleMapper {

    private ManageRoleMapper() {
    }

    public static String toManageRole(String authRole) {
        if (authRole == null || authRole.isBlank()) {
            return "BURUH";
        }
        return switch (authRole.trim().toUpperCase(Locale.ROOT)) {
            case "DRIVER" -> "SUPIR";
            case "SUPERVISOR" -> "MANDOR";
            case "WORKER" -> "BURUH";
            case "ADMIN" -> "ADMIN";
            default -> authRole.trim().toUpperCase(Locale.ROOT);
        };
    }
}
