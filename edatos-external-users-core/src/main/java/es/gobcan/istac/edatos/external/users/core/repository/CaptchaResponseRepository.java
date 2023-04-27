package es.gobcan.istac.edatos.external.users.core.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.gobcan.istac.edatos.external.users.core.domain.CaptchaResponseEntity;

@Repository
public interface CaptchaResponseRepository extends JpaRepository<CaptchaResponseEntity, String> {

    Long deleteByCreatedDateBefore(Instant date);
}
