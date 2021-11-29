package es.gobcan.istac.edatos.external.users.core.service;

import java.time.Instant;

public interface DisabledTokenService {

    boolean exists(String token);
    void deleteByExpirationDateBefore(Instant date);
}
