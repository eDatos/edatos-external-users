package es.gobcan.istac.edatos.external.users.core.repository;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FilterEntity;

@Repository
public interface FilterRepository extends JpaRepository<FilterEntity, Long> {

    List<FilterEntity> findByPermalink(String permalink);

    Page<FilterEntity> findAll(DetachedCriteria criteria, Pageable pageable);

    List<FilterEntity> findAll(DetachedCriteria criteria);

    List<FilterEntity> findAllByExternalUserOrderByCreatedDate(ExternalUserEntity user);

    Optional<FilterEntity> findByExternalUserAndPermalink(ExternalUserEntity user, String permalink);

    void deleteAllByExternalUser(ExternalUserEntity user);

    @Query("select new org.apache.commons.lang3.tuple.ImmutablePair(e.externalOperation.urn, count(e)) from FilterEntity e where e.externalOperation is not null group by e.externalOperation.urn")
    List<ImmutablePair<String, Long>> getOperationFilters();

    @Query("select e from FilterEntity e where e.externalOperation in ?1")
    List<FilterEntity> findAllByExternalOperationIn(List<ExternalOperationEntity> filtersExternalOperations);
}
