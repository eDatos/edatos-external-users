package es.gobcan.istac.edatos.external.users.core.security;

import es.gobcan.istac.edatos.external.users.core.domain.enumeration.ExternalUserRole;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("secCheckerExternal")
public class SecurityCheckerExternal {

    @Autowired
    private ExternalUserRepository externalUserRepository;

    public boolean canAccessCategory(Authentication authentication) {
        return this.isExternalUserAndIsNotDeactivated(authentication);
    }

    public boolean canAccessFilters(Authentication authentication) {
        return this.isExternalUserAndIsNotDeactivated(authentication);
    }

    public boolean canUpdateFilters(Authentication authentication) {
        return this.isExternalUserAndIsNotDeactivated(authentication);
    }

    public boolean canDeleteFilters(Authentication authentication) {
        return this.isExternalUserAndIsNotDeactivated(authentication);
    }

    public boolean canUpdateCategoryTree(Authentication authentication) {
        return this.isExternalUserAndIsNotDeactivated(authentication);
    }

    public boolean canAccessFavorites(Authentication authentication) {
        return this.isExternalUserAndIsNotDeactivated(authentication);
    }

    public boolean canCreateFavorites(Authentication authentication) {
        return this.isExternalUserAndIsNotDeactivated(authentication);
    }

    public boolean canDeleteFavorites(Authentication authentication) {
        return this.isExternalUserAndIsNotDeactivated(authentication);
    }

    public boolean canAccessUser(Authentication authentication) {
        return this.isExternalUserAndIsNotDeactivated(authentication);
    }

    public boolean canUpdateUser(Authentication authentication) {
        return this.isExternalUserAndIsNotDeactivated(authentication);
    }

    public boolean canDeleteUser(Authentication authentication) {
        return this.isExternalUserAndIsNotDeactivated(authentication);
    }

    //cambio nombre de isExternalUserAndIsNotDeactivated?
    private boolean isExternalUserAndIsNotDeactivated(Authentication authentication) {
        //return this.hasRole(authentication, ExternalUserRole.USER) && isDeactivatedUser();
        return this.hasRole(authentication, ExternalUserRole.USER);
    }

    private boolean isExternalUser(Authentication authentication) {
        return this.hasRole(authentication, ExternalUserRole.USER);
    }
    //mirar esteee
    /*private boolean isDeactivatedUser() {
        String userAuthenticated = SecurityUtils.getCurrentUserLogin();
        return externalUserRepository.findOneByEmailAndDeletionDateIsNull(userAuthenticated).isPresent();
    }*/

    private boolean hasRole(Authentication authentication, ExternalUserRole userRole) {
        return authentication.getAuthorities().stream().anyMatch(authority -> {
            String role = authority.getAuthority();
            return role.equals(userRole.name());
        });
    }
}
