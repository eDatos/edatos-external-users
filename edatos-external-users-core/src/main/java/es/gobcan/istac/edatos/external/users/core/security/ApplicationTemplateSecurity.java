package es.gobcan.istac.edatos.external.users.core.security;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import es.gobcan.istac.edatos.external.users.core.domain.enumeration.Role;

@Component("secChecker")
public class ApplicationTemplateSecurity {

    private static final String ACL_APP_NAME = "GESTOR_USUARIOS_EXTERNOS";
    private static final String SEPARATOR = "#";

    public boolean canAccessCategory(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    private boolean isManager(Authentication authentication) {
        return this.hasRole(authentication, Role.GESTOR);
    }

    private boolean hasRole(Authentication authentication, Role... userRoles) {
        return authentication.getAuthorities().stream().anyMatch(authority -> {
            String[] appRole = authority.getAuthority().split(SEPARATOR);
            String application = appRole[0];
            String roleName = appRole[1];
            return application.equals(ACL_APP_NAME) && Arrays.stream(userRoles).anyMatch(role -> Objects.equals(role.name(), roleName));
        });
    }

    public boolean canUpdateCategory(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canCreateCategory(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canDeleteCategory(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canAccessFilters(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canCreateFilters(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canUpdateFilters(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canDeleteFilters(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canAccessFavorites(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canCreateFavorites(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canDeleteFavorites(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canAccessUser(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canModifyUserStatus(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canCreateUser(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canUpdateUser(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canAccessDataProtectionPolicy(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canUpdateDataProtectionPolicy(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canAccessExternalOperation(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canUpdateExternalOperation(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canAccessMetrics(Authentication authentication) {
        return isAdmin(authentication);
    }

    public boolean canAccessHealth(Authentication authentication) {
        return isAdmin(authentication);
    }

    public boolean canAccessThreadDump(Authentication authentication) {
        return isAdmin(authentication);
    }

    public boolean canAccessConfig(Authentication authentication) {
        return isAdmin(authentication);
    }

    public boolean canAccessAudit(Authentication authentication) {
        return isAdmin(authentication);
    }

    private boolean isAdmin(Authentication authentication) {
        return this.hasRole(authentication, Role.ADMINISTRADOR);
    }

    public boolean canAccessLogs(Authentication authentication) {
        return isAdmin(authentication);
    }

    public boolean canUpdateLogs(Authentication authentication) {
        return isAdmin(authentication);
    }

    public boolean canSendNotification(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }

    public boolean canAccessNotifications(Authentication authentication) {
        return isAdmin(authentication) || isManager(authentication);
    }
}
