package es.gobcan.istac.edatos.external.users.core.service.impl;

import es.gobcan.istac.edatos.external.users.core.repository.DisabledTokenRepository;
import es.gobcan.istac.edatos.external.users.core.service.DisabledTokenService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DisabledTokenServiceImpl implements DisabledTokenService {

    private final DisabledTokenRepository disabledTokenRepository;

    public DisabledTokenServiceImpl(DisabledTokenRepository disabledTokenRepository) {
        this.disabledTokenRepository = disabledTokenRepository;
    }

    @Override
    public boolean exists(String token) {
        return disabledTokenRepository.exists(token);
    }

    @Override
    public void deleteByExpirationDateBefore(Instant date) {
        disabledTokenRepository.deleteByExpirationDateBefore(date);
    }
}
