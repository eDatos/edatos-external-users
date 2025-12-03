package es.gobcan.istac.edatos.external.users.rest.external.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.service.ExternalOperationService;
import org.siemac.edatos.core.common.exception.CommonServiceExceptionType;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.config.AuditConstants;
import es.gobcan.istac.edatos.external.users.core.config.audit.AuditEventPublisher;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;
import es.gobcan.istac.edatos.external.users.core.service.FavoriteService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.FavoriteDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.FavoriteMapper;
import es.gobcan.istac.edatos.external.users.rest.common.util.HeaderUtil;

@RestController
@RequestMapping(FavoriteResource.BASE_URL)
public class FavoriteResource extends AbstractResource {

    public static final String ENTITY_NAME = "favorite";
    public static final String BASE_URL = "/api/favorites";

    private final FavoriteService favoriteService;
    private final ExternalOperationService externalOperationService;

    private final FavoriteMapper favoriteMapper;

    private final AuditEventPublisher auditPublisher;

    public FavoriteResource(FavoriteService favoriteService, FavoriteMapper favoriteMapper, AuditEventPublisher auditPublisher, ExternalOperationService externalOperationService) {
        this.favoriteService = favoriteService;
        this.favoriteMapper = favoriteMapper;
        this.auditPublisher = auditPublisher;
        this.externalOperationService = externalOperationService;
    }

    @RequestMapping(value = "/exists", method = RequestMethod.HEAD)
    @Timed
    @PreAuthorize("@secCheckerExternal.canAccessFavorites(authentication)")
    public ResponseEntity<Void> isOperationMarkedAsFavorite(@RequestParam("operationCode") String operationCode) {
        ExternalOperationEntity operation = externalOperationService.findByCode(operationCode);
        if (operation != null) {
            FavoriteEntity favorite = favoriteService.findByExternalUserAndExternalOperationOrCategoryAndAncestors(Collections.singletonList(operation), Collections.emptyList());
            if (favorite != null) {
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    @Timed
    @PreAuthorize("@secCheckerExternal.canAccessFavorites(authentication)")
    public ResponseEntity<FavoriteDto> getFavoriteById(@PathVariable Long id) {
        return ResponseEntity.ok(favoriteMapper.toDto(favoriteService.find(id)));
    }

    @GetMapping
    @Timed
    @PreAuthorize("@secCheckerExternal.canAccessFavorites(authentication)")
    public ResponseEntity<List<FavoriteDto>> getFavorites() {
        List<FavoriteDto> result = favoriteService.findByExternalUser().stream().map(favoriteMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Timed
    @PreAuthorize("@secCheckerExternal.canCreateFavorites(authentication)")
    public ResponseEntity<FavoriteDto> createFavorite(@RequestBody FavoriteDto dto) throws URISyntaxException {
        if (dto != null && dto.getId() != null) {
            throw new EDatosException(CommonServiceExceptionType.PARAMETER_UNEXPECTED, "id");
        }

        FavoriteEntity entity = favoriteMapper.toEntityWithAuthenticatedUser(dto);
        entity = favoriteService.create(entity);
        FavoriteDto newDto = favoriteMapper.toDto(entity);

        auditPublisher.publish(AuditConstants.FAVORITE_CREATION, getAuditData(entity), AuditPrincipalType.EXTERNAL_USER);
        return ResponseEntity.created(new URI(BASE_URL + SLASH + newDto.getId())).headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, newDto.getId().toString())).body(newDto);
    }

    @DeleteMapping("/{id}")
    @Timed
    @PreAuthorize("@secCheckerExternal.canDeleteFavorites(authentication)")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        FavoriteEntity favorite = favoriteService.find(id);
        favoriteService.delete(favorite);

        auditPublisher.publish(AuditConstants.FAVORITE_DELETION, getAuditData(favorite), AuditPrincipalType.EXTERNAL_USER);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    private Map<String, Object> getAuditData(FavoriteEntity favorite) {
        Map<String, Object> data = new HashMap<>();
        data.put(AuditConstants.CODE, favorite.getId().toString());
        if (favorite.getCategory() != null) {
            data.put(AuditConstants.MESSAGE, favorite.getCategory().getCode());
            data.put(AuditConstants.EVENT_SUBTYPE, AuditConstants.FAVORITE_CATEGORY_SUBTYPE);
        } else if (favorite.getExternalOperation() != null) {
            data.put(AuditConstants.MESSAGE, favorite.getExternalOperation().getCode());
            data.put(AuditConstants.EVENT_SUBTYPE, AuditConstants.FAVORITE_OPERATION_SUBTYPE);
        }
        return data;
    }
}
