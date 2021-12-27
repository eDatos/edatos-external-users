package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import es.gobcan.istac.edatos.external.users.core.service.validator.ExternalUserValidator;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.json.JSONObject;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.gobcan.istac.edatos.external.users.core.domain.DisabledTokenEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.errors.CustomParameterizedExceptionBuilder;
import es.gobcan.istac.edatos.external.users.core.errors.ErrorConstants;
import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import es.gobcan.istac.edatos.external.users.core.repository.DisabledTokenRepository;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.repository.FilterRepository;
import es.gobcan.istac.edatos.external.users.core.security.SecurityUtils;
import es.gobcan.istac.edatos.external.users.core.service.ExternalUserService;
import es.gobcan.istac.edatos.external.users.core.util.QueryUtil;
import es.gobcan.istac.edatos.external.users.core.util.RandomUtil;

@Service
public class ExternalUserServiceImpl implements ExternalUserService {

    private final ExternalUserRepository externalUserRepository;
    private final FilterRepository filterRepository;
    private final QueryUtil queryUtil;
    private final DisabledTokenRepository disabledTokenRepository;
    private final ExternalUserValidator externalUserValidator;

    public ExternalUserServiceImpl(ExternalUserRepository externalUserRepository, FilterRepository filterRepository, QueryUtil queryUtil,
                                   DisabledTokenRepository disabledTokenRepository, ExternalUserValidator externalUserValidator) {
        this.externalUserRepository = externalUserRepository;
        this.filterRepository = filterRepository;
        this.queryUtil = queryUtil;
        this.disabledTokenRepository = disabledTokenRepository;
        this.externalUserValidator = externalUserValidator;
    }

    @Override
    public ExternalUserEntity create(ExternalUserEntity user) {
        user.setEnabled(false);
        user.setRegistrationToken(UUID.randomUUID().toString());
        return externalUserRepository.saveAndFlush(user);
    }

    @Override
    public void logout(String token) {
        if (StringUtils.isNotBlank(token)) {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7, token.length());
            }
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getDecoder();

            String payload = new String(decoder.decode(chunks[1]));

            JSONObject obj = new JSONObject(payload);

            Instant expirationDate = Instant.ofEpochSecond(obj.getLong("exp"));
            if (Instant.now().isBefore(expirationDate)) {
                DisabledTokenEntity disabledToken = new DisabledTokenEntity(token, expirationDate);
                disabledTokenRepository.save(disabledToken);
            }
        }
    }

    @Override
    public ExternalUserEntity update(ExternalUserEntity user) {
        return externalUserRepository.saveAndFlush(user);
    }

    @Override
    public ExternalUserEntity deactivate(Long id) {
        ExternalUserEntity usuario = externalUserRepository.findOneByIdAndDeletionDateIsNull(id).orElseThrow(() -> new EDatosException(ServiceExceptionType.EXTERNAL_USER_DEACTIVATE));

        usuario.setDeletionDate(Instant.now());
        usuario.setDeletedBy(SecurityUtils.getCurrentUserLogin());
        return externalUserRepository.saveAndFlush(usuario);
    }

    @Override
    public void delete(Long id) {
        ExternalUserEntity usuario = externalUserRepository.findOneByIdAndDeletionDateIsNull(id).orElseThrow(() -> new EDatosException(ServiceExceptionType.EXTERNAL_USER_DELETED));

        filterRepository.deleteAllByExternalUser(usuario);
        externalUserRepository.delete(id);
    }

    @Override
    public ExternalUserEntity recover(Long id) {
        ExternalUserEntity usuario = externalUserRepository.findOneByIdAndDeletionDateIsNotNull(id)
                .orElseThrow(() -> new CustomParameterizedExceptionBuilder().message("Usuario no válido").code(ErrorConstants.ENTIDAD_NO_ENCONTRADA, id.toString()).build());
        usuario.setDeletionDate(null);
        usuario.setDeletedBy(null);
        return externalUserRepository.saveAndFlush(usuario);
    }

    @Override
    public ExternalUserEntity find(Long id) {
        return externalUserRepository.findOne(id);
    }

    @Override
    public Optional<ExternalUserEntity> findByEmail(String email) {
        return externalUserRepository.findOneByEmail(email);
    }

    @Override
    public Page<ExternalUserEntity> find(Pageable pageable, Boolean includeDeleted, String query) {
        DetachedCriteria criteria = buildExternalUserCriteria(pageable, includeDeleted, query);
        return externalUserRepository.findAll(criteria, pageable);
    }

    private DetachedCriteria buildExternalUserCriteria(Pageable pageable, Boolean includeDeleted, String query) {
        StringBuilder queryBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(query)) {
            queryBuilder.append(query);
        }
        String finalQuery = getFinalQuery(includeDeleted, queryBuilder);
        return queryUtil.queryToUserExternalUserCriteria(pageable, finalQuery);
    }

    private String getFinalQuery(Boolean includeDeleted, StringBuilder queryBuilder) {
        String finalQuery = queryBuilder.toString();
        if (BooleanUtils.isTrue(includeDeleted)) {
            finalQuery = queryUtil.queryIncludingDeleted(finalQuery);
        }
        return finalQuery;
    }

    @Override
    public ExternalUserEntity getUsuarioWithAuthorities() {
        ExternalUserEntity returnValue = externalUserRepository.findOneWithRolesByEmail(SecurityUtils.getCurrentUserLogin()).orElse(new ExternalUserEntity());
        if (returnValue.getDeletionDate() != null) {
            returnValue.setRoles(new HashSet<>());
        }

        return returnValue;
    }

    @Override
    public void updateExternalUserAccountPassword(ExternalUserEntity user, String currentPassword, String newPassword) {
        if (!SecurityUtils.passwordEncoderMatches(currentPassword, user.getPassword())) {
            throw new EDatosException(ServiceExceptionType.PASSWORD_NOT_MATCH);
        }
        String passwordEncoder = SecurityUtils.passwordEncoder(newPassword);
        user.setPassword(passwordEncoder);
        externalUserRepository.saveAndFlush(user);
    }

    @Override
    public Optional<ExternalUserEntity> recoverExternalUserAccountPassword(String email) {
        return externalUserRepository.findOneByEmailIgnoreCaseAndDeletionDateIsNullAndEnabledTrue(email).map(user -> {
            user.setResetKey(RandomUtil.generateResetKey());
            user.setResetDate(ZonedDateTime.now());
            return externalUserRepository.save(user);
        });
    }

    @Override
    public Optional<ExternalUserEntity> completePasswordReset(String newPassword, String key) {
        return findOneByResetKey(key).map(user -> {
            user.setPassword(SecurityUtils.passwordEncoder(newPassword));
            user.setResetKey(null);
            user.setResetDate(null);
            return externalUserRepository.save(user);
        });
    }

    @Override
    public Optional<ExternalUserEntity> findOneByResetKey(String key) {
        return externalUserRepository.findOneByResetKeyAndDeletionDateIsNull(key).filter(user -> {
            ZonedDateTime thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30);
            return user.getResetDate().isAfter(thirtyMinutesAgo);
        });
    }

    @Override
    public ExternalUserEntity confirmUserRegistration(String token) {
        ExternalUserEntity user = externalUserRepository.findOneByRegistrationToken(token).orElse(null);
        externalUserValidator.checkRegistrationTokenHasNotExpired(user);

        user.setEnabled(true);
        return externalUserRepository.save(user);
    }
}
