package es.gobcan.istac.edatos.external.users.core.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.gobcan.istac.edatos.external.users.core.domain.ExternalOperationEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FilterEntity;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.repository.FilterRepository;
import es.gobcan.istac.edatos.external.users.core.security.SecurityUtils;
import es.gobcan.istac.edatos.external.users.core.service.FilterService;
import es.gobcan.istac.edatos.external.users.core.util.QueryUtil;

@Service
public class FilterServiceImpl implements FilterService {

    private final FilterRepository filterRepository;
    private final ExternalUserRepository externalUserRepository;

    private final QueryUtil queryUtil;

    public FilterServiceImpl(FilterRepository filterRepository, ExternalUserRepository externalUserRepository, QueryUtil queryUtil) {
        this.filterRepository = filterRepository;
        this.externalUserRepository = externalUserRepository;
        this.queryUtil = queryUtil;
    }

    @Override
    public FilterEntity create(FilterEntity filter) {
        return filterRepository.saveAndFlush(filter);
    }

    @Override
    public FilterEntity update(FilterEntity filter) {
        return filterRepository.saveAndFlush(filter);
    }

    @Override
    public List<FilterEntity> update(List<FilterEntity> filters) {
        return filterRepository.save(filters);
    }

    @Override
    public FilterEntity find(Long id) {
        return filterRepository.findOne(id);
    }

    @Override
    public List<FilterEntity> findAll() {
        return filterRepository.findAll();
    }

    @Override
    public List<FilterEntity> findAllByUser(ExternalUserEntity user) {
        return filterRepository.findAllByExternalUserOrderByCreatedDate(user);
    }

    @Override
    public Page<FilterEntity> find(String query, Pageable pageable) {
        DetachedCriteria criteria = queryUtil.queryToFilterCriteria(query, pageable);
        return filterRepository.findAll(criteria, pageable);
    }

    @Override
    public List<FilterEntity> find(String query, Sort sort) {
        DetachedCriteria criteria = queryUtil.queryToFilterSortCriteria(query, sort);
        return filterRepository.findAll(criteria);
    }

    @Override
    public Page<FilterEntity> findByExternalUser(String query, Pageable pageable) {
        DetachedCriteria criteria = queryUtil.queryToFilterCriteria(query, pageable);
        ExternalUserEntity externalUser = externalUserRepository.findOneByEmailIgnoreCase(SecurityUtils.getCurrentUserLogin()).get();
        criteria.add(Restrictions.eq("externalUser", externalUser));
        return filterRepository.findAll(criteria, pageable);
    }

    @Override
    public List<FilterEntity> findByPermalink(String permalink) {
        return filterRepository.findByPermalink(permalink);
    }

    @Override
    public List<FilterEntity> findByExternalOperation(List<ExternalOperationEntity> externalOperations) {
        return filterRepository.findAllByExternalOperationIn(externalOperations);
    }

    @Override
    public Map<String, Long> getOperationFilters() {
        return filterRepository.getOperationFilters().stream().collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }

    @Override
    public void delete(FilterEntity filter) {
        filterRepository.delete(filter);
    }
}
