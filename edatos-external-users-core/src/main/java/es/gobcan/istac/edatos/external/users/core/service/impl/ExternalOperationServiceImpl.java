package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

import java.util.stream.Collectors;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalDatasetEntity;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalOperationRepository;
import es.gobcan.istac.edatos.external.users.core.service.ExternalOperationService;
import es.gobcan.istac.edatos.external.users.core.service.FavoriteService;
import es.gobcan.istac.edatos.external.users.core.util.QueryUtil;

@Service
public class ExternalOperationServiceImpl implements ExternalOperationService {

    private final ExternalOperationRepository externalOperationRepository;
    private final QueryUtil queryUtil;
    private final FavoriteService favoriteService;

    public ExternalOperationServiceImpl(ExternalOperationRepository externalOperationRepository, QueryUtil queryUtil, FavoriteService favoriteService) {
        this.externalOperationRepository = externalOperationRepository;
        this.queryUtil = queryUtil;
        this.favoriteService = favoriteService;
    }

    @Override
    public List<ExternalOperationEntity> findAll() {
        return externalOperationRepository.findAll();
    }

    @Override
    public List<ExternalOperationEntity> findAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return externalOperationRepository.findAllByIdIn(ids);
    }

    @Override
    public List<ExternalOperationEntity> findAllEnabled() {
        return externalOperationRepository.findByEnabledTrue();
    }

    @Override
    public Page<ExternalOperationEntity> find(String query, Pageable pageable) {
        DetachedCriteria criteria = queryUtil.queryToExternalOperationCriteria(query, pageable);
        return externalOperationRepository.findAll(criteria, pageable);
    }

    @Override
    public ExternalOperationEntity findByCode(String code) {
        return externalOperationRepository.findByCode(code).orElse(null);
    }

    @Override
    public ExternalOperationEntity create(ExternalOperationEntity operation) {
        return externalOperationRepository.saveAndFlush(operation);
    }

    @Override
    public ExternalOperationEntity update(ExternalOperationEntity operation) {
        if (!operation.isEnabled()) {
            deleteSubscriptions(operation);
        }

        return externalOperationRepository.saveAndFlush(operation);
    }

    @Override
    public void delete(String urn) {
        externalOperationRepository.deleteByUrn(urn);
    }

    @Override
    public void delete(ExternalOperationEntity operation) {
        externalOperationRepository.delete(operation);
    }

    @Override
    public List<ExternalOperationEntity> findByExternalOperationDatasetUrnIn(List<ExternalDatasetEntity> listDataset) {
        List<String> listUrn = listDataset.stream().map(dt -> dt.getExternalOperationUrn()).collect(Collectors.toList());

        return externalOperationRepository.findByUrnIn(listUrn);
    }

    @Override
    public ExternalOperationEntity findOperationById(Long id) {
        return externalOperationRepository.findOne(id);
    }

    @Override
    public Optional<ExternalOperationEntity> findByUrn(String urn) {
        return externalOperationRepository.findByUrn(urn);
    }

    private void deleteSubscriptions(ExternalOperationEntity operation) {
        favoriteService.deleteBySubscription(operation);
    }
}
