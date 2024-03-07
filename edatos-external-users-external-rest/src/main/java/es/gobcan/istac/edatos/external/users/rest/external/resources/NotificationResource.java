package es.gobcan.istac.edatos.external.users.rest.external.resources;

import java.util.List;

import org.siemac.metamac.rest.notices.v1_0.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.vo.NotificationBaseVO;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;
import es.gobcan.istac.edatos.external.users.core.service.NotificationService;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.NotificationBaseMapper;
import es.gobcan.istac.edatos.external.users.rest.common.util.PaginationUtil;

@RestController
@RequestMapping("/api/notices")
public class NotificationResource extends AbstractResource {

    private final NotificationBaseMapper notificationBaseMapper;
    private final ExternalUserService    externalUserService;
    private final NotificationService notificationService;


    public NotificationResource(NotificationBaseMapper notificationBaseMapper, NotificationService notificationService, ExternalUserService externalUserService) {
        this.notificationBaseMapper = notificationBaseMapper;
        this.notificationService = notificationService;
        this.externalUserService = externalUserService;
    }


    @GetMapping()
    @Timed
    @PreAuthorize("@secCheckerExternal.canUpdateUser(authentication)")
    public ResponseEntity<List<NotificationBaseVO>> getNoticesByReceiver(Pageable pageable) {
        ExternalUserEntity databaseUser = externalUserService.getUsuarioWithAuthorities();

        Page<Notice> notices = notificationService.findNoticesByReceiver(databaseUser.getEmail(), pageable);
        List<NotificationBaseVO> noticesDto = notificationBaseMapper.toDtos(notices.getContent());

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(notices, "/api/notices");

        return ResponseEntity.ok().headers(headers).body(noticesDto);
    }

    @GetMapping("/{urn}")
    @Timed
    @PreAuthorize("@secCheckerExternal.canUpdateUser(authentication)")
    public ResponseEntity<NotificationBaseVO> getNoticesByReceiver(@RequestParam String urn) {
        Notice notice = notificationService.findNoticeByUrn(urn);
        NotificationBaseVO noticesDto = notificationBaseMapper.toDto(notice);

        return ResponseEntity.ok().body(noticesDto);
    }
}
