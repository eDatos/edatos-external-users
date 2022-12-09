package es.gobcan.istac.edatos.external.users.rest.external.resources;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.domain.DataProtectionPolicyEntity;
import es.gobcan.istac.edatos.external.users.core.repository.DataProtectionPolicyRepository;
import es.gobcan.istac.edatos.external.users.core.service.DataProtectionPolicyService;
import es.gobcan.istac.edatos.external.users.rest.common.dto.DataProtectionPolicyDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.DataProtectionPolicyMapper;
import io.github.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping(DataProtectionPolicyResource.BASE_URL)
public class DataProtectionPolicyResource extends AbstractResource {

    public static final String BASE_URL = "/api/data-protection-policy";

    private final DataProtectionPolicyService dataProtectionPolicyService;
    private final DataProtectionPolicyMapper dataProtectionPolicyMapper;
    private DataProtectionPolicyRepository dataProtectionPolicyRepository;

    public DataProtectionPolicyResource(DataProtectionPolicyService dataProtectionPolicyService, DataProtectionPolicyMapper dataProtectionPolicyMapper,
                                        DataProtectionPolicyRepository dataProtectionPolicyRepository) {
        this.dataProtectionPolicyService = dataProtectionPolicyService;
        this.dataProtectionPolicyMapper = dataProtectionPolicyMapper;
        this.dataProtectionPolicyRepository = dataProtectionPolicyRepository;
    }

    @GetMapping
    @Timed
    public ResponseEntity<DataProtectionPolicyDto> getDataProtectionPolicy() {
        DataProtectionPolicyEntity dataProtectionPolicy = dataProtectionPolicyRepository.findFirstByOrderByIdDesc();
        DataProtectionPolicyDto dataProtectionPolicyDto = dataProtectionPolicyMapper.toDto(dataProtectionPolicy);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(dataProtectionPolicyDto));
    }
}
