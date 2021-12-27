package es.gobcan.istac.edatos.external.users.core.repository;

import java.time.Instant;
import java.util.Optional;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;

@Repository
public interface ExternalUserRepository extends JpaRepository<ExternalUserEntity, Long> {

    Optional<ExternalUserEntity> findOneByEmail(String email);

    Optional<ExternalUserEntity> findOneByRegistrationToken(String token);

    Optional<ExternalUserEntity> findOneByEmailAndIdNot(String email, Long id);

    Optional<ExternalUserEntity> findOneByEmailAndDeletionDateIsNull(String login);

    Optional<ExternalUserEntity> findOneByIdAndDeletionDateIsNull(Long id);

    Optional<ExternalUserEntity> findOneByIdAndDeletionDateIsNotNull(Long id);

    Page<ExternalUserEntity> findAll(DetachedCriteria criteria, Pageable pageable);

    Optional<ExternalUserEntity> findOneByEmailIgnoreCaseAndDeletionDateIsNullAndEnabledTrue(String email);

    void deleteAllByEnabledFalseAndCreatedDateIsBefore(Instant date);

    Boolean existsByEmailIgnoreCase(String email);

    Boolean existsByEmailIgnoreCaseAndDeletionDateIsNull(String email);

    Optional<ExternalUserEntity> findOneWithRolesByEmail(String email);

    Optional<ExternalUserEntity> findOneByResetKeyAndDeletionDateIsNull(String key);
}
