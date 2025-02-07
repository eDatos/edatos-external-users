package es.gobcan.istac.edatos.external.users.rest.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.siemac.metamac.rest.notices.v1_0.domain.Notice;
import org.siemac.metamac.rest.notices.v1_0.domain.Receiver;
import org.siemac.metamac.rest.notices.v1_0.domain.Receivers;

import es.gobcan.istac.edatos.external.users.core.domain.vo.NotificationBaseVO;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.config.AuditingMapperConfig;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.resolver.GenericMapperResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class, uses = {GenericMapperResolver.class})
public abstract class NotificationBaseMapper implements EntityMapper<NotificationBaseVO, Notice> {

    @Override
    @Mapping(target = "receivers", source = "receivers", qualifiedByName = "receiversToStrings")
    public abstract NotificationBaseVO toDto(Notice notice);

    @Named("receiversToStrings")
    public List<String> receiversToStrings(Receivers receivers){
        if (receivers == null || receivers.getReceivers() == null) {
            return new ArrayList<>();
        }
        return receivers.getReceivers().stream().map(Receiver::getUsername).collect(Collectors.toList());
    }

    @Override
    @Mapping(target = "receivers", source = "receivers", qualifiedByName = "stringsToReceivers")
    public abstract Notice toEntity(NotificationBaseVO notice);

    @Named("stringsToReceivers")
    public Receivers stringsToReceivers(List<String> receiversStrings){
        Receivers receivers = new Receivers();
        if (receiversStrings == null) {
            return receivers;
        }
        receivers.getReceivers().addAll(receiversStrings.stream().map(this::stringToReceiver).collect(Collectors.toList()));
        return receivers;
    }

    public Receiver stringToReceiver(String receiverString){
        Receiver receiver = new Receiver();
        receiver.setUsername(receiverString);
        return receiver;
    }
}
