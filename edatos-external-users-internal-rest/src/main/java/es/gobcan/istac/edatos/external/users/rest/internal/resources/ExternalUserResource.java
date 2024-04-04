package es.gobcan.istac.edatos.external.users.rest.internal.resources;

import static es.gobcan.istac.edatos.external.users.rest.internal.resources.ExternalUserResource.BASE_URL;

import java.util.List;

import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;
import es.gobcan.istac.edatos.external.users.core.service.NotificationService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ExternalUserDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.ExternalUserMapper;
import es.gobcan.istac.edatos.external.users.rest.common.util.PaginationUtil;

@RestController
@RequestMapping(BASE_URL)
public class ExternalUserResource extends AbstractResource {

    public static final String BASE_URL = "/api/external-users";

    private static final String ENTITY_NAME = "externalUser";

    private final ExternalUserService externalUserService;
    private final ExternalUserMapper externalUserMapper;
    private final NotificationService notificationService;

    public ExternalUserResource(ExternalUserService externalUserService, ExternalUserMapper externalUserMapper, NotificationService notificationService) {
        this.externalUserService = externalUserService;
        this.externalUserMapper = externalUserMapper;
        this.notificationService = notificationService;
    }

    @Timed
    @GetMapping
    @PreAuthorize("@secChecker.canAccessUser(authentication)")
    public ResponseEntity<List<ExternalUserDto>> find(Pageable pageable, Boolean includeDeleted, String query) {
        Page<ExternalUserDto> page = externalUserService.find(pageable, includeDeleted, query).map(externalUserMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, BASE_URL);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    @Timed
    @PreAuthorize("@secChecker.canAccessUser(authentication)")
    public ResponseEntity<ExternalUserDto> getExternalUserById(@PathVariable Long id) {
        return ResponseEntity.ok(externalUserMapper.toDto(externalUserService.find(id)));
    }

    @Timed
    @DeleteMapping("/{id}")
    @PreAuthorize("@secChecker.canModifyUserStatus(authentication)")
    public ResponseEntity<Void> deleteExternalUser(@PathVariable Long id) {
        try {
            notificationService.createDeleteExternalUserAccountNotification(externalUserService.delete(id));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new EDatosException(ServiceExceptionType.EXTERNAL_USER_DELETE_FAILED);
        }
    }
}
