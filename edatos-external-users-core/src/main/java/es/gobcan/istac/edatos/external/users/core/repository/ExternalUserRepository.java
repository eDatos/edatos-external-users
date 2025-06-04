package es.gobcan.istac.edatos.external.users.core.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;

@Repository
public interface ExternalUserRepository extends JpaRepository<ExternalUserEntity, Long> {

    Optional<ExternalUserEntity> findOneById(Long id);

    Optional<ExternalUserEntity> findOneByEmailIgnoreCase(String email);

    Optional<ExternalUserEntity> findOneByRegistrationToken(String token);

    Page<ExternalUserEntity> findAll(DetachedCriteria criteria, Pageable pageable);

    List<ExternalUserEntity> findAll(DetachedCriteria criteria);

    Optional<ExternalUserEntity> findOneByEmailIgnoreCaseAndEnabledTrue(String email);

    List<ExternalUserEntity> deleteAllByEnabledFalseAndPasswordIsNotAndCreatedDateIsBefore(String migrationPassword,Instant date);

    Boolean existsByEmailIgnoreCase(String email);

    Optional<ExternalUserEntity> findOneWithRolesByEmailIgnoreCase(String email);

    Optional<ExternalUserEntity> findOneByResetKey(String key);

    Optional<ExternalUserEntity> findOneByEmailModificationRequest_Key(String key);

    @Query("select u from ExternalUserEntity as u WHERE lower(u.email) IN ?1")
    List<ExternalUserEntity> findAllByLowerCaseEmailIn(List<String> emails);
}
