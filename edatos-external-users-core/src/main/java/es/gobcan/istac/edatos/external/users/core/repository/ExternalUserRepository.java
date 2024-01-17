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

    Optional<ExternalUserEntity> findOneByEmail(String email);

    List<ExternalUserEntity> findAllByEmailIn(List<String> emails);

    Optional<ExternalUserEntity> findOneByRegistrationToken(String token);

    Optional<ExternalUserEntity> findOneByEmailAndIdNot(String email, Long id);

    Optional<ExternalUserEntity> findOneById(Long id);

    Page<ExternalUserEntity> findAll(DetachedCriteria criteria, Pageable pageable);

    Optional<ExternalUserEntity> findOneByEmailIgnoreCaseAndEnabledTrue(String email);

    void deleteAllByEnabledFalseAndCreatedDateIsBefore(Instant date);

    Boolean existsByEmailIgnoreCase(String email);

    Optional<ExternalUserEntity> findOneWithRolesByEmail(String email);

    Optional<ExternalUserEntity> findOneByResetKey(String key);

    List<ExternalUserEntity> findByFavorites_category_idIn(List<Long> categoriesIds);

    @Query("select u from ExternalUserEntity as u join FavoriteEntity as f on u.id = f.externalUser.id WHERE f.category.id in ?1 or f.externalOperation.id in ?2 group by u.id having count(u.id) = ?3")
    List<ExternalUserEntity> findByCategoriesAndExternalOperations(List<Long> categories, List<Long> operations, Long categoriesSizePlusOperationsSize);

    @Query("select u from ExternalUserEntity as u join FavoriteEntity as f on u.id = f.externalUser.id where f.category.id in ?1 group by u.id having count(u.id) = ?2")
    List<ExternalUserEntity> findByCategories(List<Long> categories, Long categoriesSize);

    @Query("select u from ExternalUserEntity as u join FavoriteEntity as f on u.id = f.externalUser.id where f.externalOperation.id in ?1 group by u.id having count(u.id) = ?2")
    List<ExternalUserEntity> findByExternalOperations(List<Long> operations, Long operationsSize);
}
