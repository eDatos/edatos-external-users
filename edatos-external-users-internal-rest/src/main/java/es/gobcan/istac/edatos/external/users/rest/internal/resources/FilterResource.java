package es.gobcan.istac.edatos.external.users.rest.internal.resources;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.service.FilterService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.FilterDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.FilterMapper;
import es.gobcan.istac.edatos.external.users.rest.common.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping(FilterResource.BASE_URL)
public class FilterResource extends AbstractResource {

    public static final String ENTITY_NAME = "filter";
    public static final String BASE_URL = "/api/filters";

    private final FilterService filterService;

    private final FilterMapper filterMapper;

    public FilterResource(FilterService filterService, FilterMapper filterMapper) {
        this.filterService = filterService;
        this.filterMapper = filterMapper;
    }

    @GetMapping("/{id}")
    @Timed
    @PreAuthorize("@secChecker.canAccessFilters(authentication)")
    public ResponseEntity<FilterDto> getFilterById(@PathVariable Long id) {
        FilterDto filterDto = filterMapper.toDto(filterService.find(id));
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(filterDto));
    }

    @GetMapping
    @Timed
    @PreAuthorize("@secChecker.canAccessFilters(authentication)")
    public ResponseEntity<List<FilterDto>> getFilters(Pageable pageable, @RequestParam(required = false) String query) {
        Page<FilterDto> result = filterService.find(query, pageable).map(filterMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(result, BASE_URL);
        return ResponseEntity.ok().headers(headers).body(result.getContent());
    }
}
