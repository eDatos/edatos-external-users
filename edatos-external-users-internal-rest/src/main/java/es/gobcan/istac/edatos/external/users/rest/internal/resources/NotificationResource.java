package es.gobcan.istac.edatos.external.users.rest.internal.resources;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.siemac.metamac.rest.notices.v1_0.domain.Receivers;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;
import es.gobcan.istac.edatos.external.users.core.service.NotificationService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.NotificationDto;

@RestController
@RequestMapping(NotificationResource.BASE_URL)
public class NotificationResource extends AbstractResource {

    public static final String BASE_URL = "/api/notifications";

    private final NotificationService notificationService;

    private final ExternalUserService externalUserService;

    public NotificationResource(NotificationService notificationService, ExternalUserService externalUserService) {
        this.notificationService = notificationService;
        this.externalUserService = externalUserService;
    }

    @Timed
    @PostMapping
    @PreAuthorize("@secChecker.canSendNotification(authentication)")
    public ResponseEntity<NotificationDto> send(@Valid @RequestBody NotificationDto notificationDto) {
        HashMap<String, Receivers> receiversByLanguage = notificationService.createReceiversGroupedByLanguage(notificationDto.getReceivers(), notificationDto.getCategoriesIds(),
                notificationDto.getExternalOperationsIds());
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
    public ResponseEntity<NotificationDto> findAllReceivers() {
        List<ExternalUserEntity> externalUsersDto = externalUserService.findAllReceivers();
        List<String> receivers = externalUsersDto.stream().map(ExternalUserEntity::getEmail).collect(Collectors.toList());
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setReceivers(receivers);
        return ResponseEntity.ok().body(notificationDto);
    }
}
