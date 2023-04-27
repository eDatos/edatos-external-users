package es.gobcan.istac.edatos.external.users.core.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;

@Repository
public interface ExternalOperationRepository extends AbstractExternalItemRepository<ExternalOperationEntity> {

    Page<ExternalOperationEntity> findAll(DetachedCriteria criteria, Pageable pageable);
    List<ExternalOperationEntity> findByExternalCategoryUrnIn(List<String> urns);
    List<ExternalOperationEntity> findByUrnIn(List<String> urns);
    Optional<ExternalOperationEntity> findByCode(String code);
    Optional<ExternalOperationEntity> findByUrn(String urn);
    List<ExternalOperationEntity> findByEnabledTrue();
}
