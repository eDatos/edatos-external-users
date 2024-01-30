package es.gobcan.istac.edatos.external.users.rest.common.mapper;

import org.mapstruct.Mapper;
import org.siemac.metamac.rest.notices.v1_0.domain.Notice;

import es.gobcan.istac.edatos.external.users.core.domain.vo.NotificationBaseVO;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.config.AuditingMapperConfig;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.resolver.GenericMapperResolver;

@Mapper(componentModel = "spring", config = AuditingMapperConfig.class, uses = {GenericMapperResolver.class})
public abstract class NotificationBaseMapper implements EntityMapper<NotificationBaseVO, Notice> {

}
