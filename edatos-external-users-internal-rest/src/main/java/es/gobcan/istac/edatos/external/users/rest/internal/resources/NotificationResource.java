package es.gobcan.istac.edatos.external.users.rest.internal.resources;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.siemac.metamac.rest.notices.v1_0.domain.Notice;
import org.siemac.metamac.rest.notices.v1_0.domain.Receivers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.domain.vo.NotificationBaseVO;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.NotificationBaseMapper;
import es.gobcan.istac.edatos.external.users.rest.common.util.PaginationUtil;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;
import es.gobcan.istac.edatos.external.users.core.service.NotificationService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.NotificationDto;
import es.gobcan.istac.edatos.external.users.rest.common.dto.ReceiverDto;

@RestController
@RequestMapping(NotificationResource.BASE_URL)
public class NotificationResource extends AbstractResource {

    public static final String BASE_URL = "/api";

    private final NotificationService notificationService;
    private final NotificationBaseMapper notificationBaseMapper;
    private final ExternalUserService externalUserService;

    public NotificationResource(NotificationService notificationService, NotificationBaseMapper notificationBaseMapper, ExternalUserService externalUserService) {
        this.notificationService = notificationService;
        this.notificationBaseMapper = notificationBaseMapper;
        this.externalUserService = externalUserService;
    }

    @Timed
    @PostMapping("/notifications")
    @PreAuthorize("@secChecker.canSendNotification(authentication)")
    public ResponseEntity<NotificationDto> send(@Valid @RequestBody NotificationDto notificationDto) {
        HashMap<String, Receivers> receiversByLanguage = notificationService.createReceiversGroupedByLanguage(
            notificationDto.getReceivers(),
            notificationDto.getCategoriesIds(),
            notificationDto.getExternalOperationsIds(),
            notificationDto.getFiltersExternalOperationsIds()
        );
        List<String> failedNoticeReceivers = notificationService.createNotificationWithReceivers(notificationDto.getMessage(), notificationDto.getSubject(), receiversByLanguage);

        if (!failedNoticeReceivers.isEmpty()) {
            notificationDto.setFailedReceivers(failedNoticeReceivers);
            return ResponseEntity.badRequest().body(notificationDto);
        }

        return ResponseEntity.ok(notificationDto);
    }

    @Timed
    @GetMapping("/receivers")
    @PreAuthorize("@secChecker.canAccessUser(authentication)")
    public ResponseEntity<List<ReceiverDto>> findAllReceivers() {
        List<ExternalUserEntity> externalUsersDto = externalUserService.findAllReceivers();
        List<ReceiverDto> receivers = externalUsersDto.stream().map(externalUser -> {
            ReceiverDto receiverDto = new ReceiverDto();
            receiverDto.setEmail(externalUser.getEmail());
            return receiverDto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok().body(receivers);
    }

    @Timed
    @GetMapping("/notifications")
    @PreAuthorize("@secChecker.canAccessNotifications(authentication)")
    public ResponseEntity<List<NotificationBaseVO>> getNoticesByReceiver(Pageable pageable, @RequestParam(required = false) String query) {
        Page<Notice> notices = notificationService.findNotices(query, pageable);
        List<NotificationBaseVO> noticesDto = notificationBaseMapper.toDtos(notices.getContent());

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(notices, "/api/notices");

        return ResponseEntity.ok().headers(headers).body(noticesDto);
    }

    @Timed
    @GetMapping("/notifications/{urn}")
    @PreAuthorize("@secChecker.canAccessNotifications(authentication)")
    public ResponseEntity<NotificationBaseVO> getNoticesByReceiver(@RequestParam String urn) {
        Notice notice = notificationService.findNoticeByUrn(urn);
        NotificationBaseVO noticesDto = notificationBaseMapper.toDto(notice);

        return ResponseEntity.ok().body(noticesDto);
    }
}
