package es.gobcan.istac.edatos.external.users.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.gobcan.istac.edatos.external.users.core.domain.DataProtectionPolicyEntity;

@Repository
public interface DataProtectionPolicyRepository extends JpaRepository<DataProtectionPolicyEntity, Long>, DetachEntityRepositoryCustom<DataProtectionPolicyEntity> {

    DataProtectionPolicyEntity findFirstByOrderByIdAsc();
    DataProtectionPolicyEntity findFirstByOrderByIdDesc();
}
