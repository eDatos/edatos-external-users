package es.gobcan.istac.edatos.external.users.rest.external.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;

@RestController
@RequestMapping(LanguageResource.BASE_URL)
public class LanguageResource extends AbstractResource {

    public static final String BASE_URL = "/api/languages";

    private final MetadataProperties metadataProperties;

    public LanguageResource(MetadataProperties metadataProperties) {
        this.metadataProperties = metadataProperties;
    }

    @GetMapping("/allowed")
    @Timed
    @PreAuthorize("@secCheckerExternal.canAccessCategory(authentication)")
    public ResponseEntity<List<String>> getAllowedLanguages() {
        return ResponseEntity.ok(metadataProperties.getAvailableLanguages().stream().map(String::toLowerCase).collect(Collectors.toList()));
    }

    @GetMapping("/internationalization")
    @Timed
    public ResponseEntity<List<String>> getInternationalizationLanguages() {
        return ResponseEntity.ok(metadataProperties.getInternationalizationLanguages().stream().map(String::toLowerCase).collect(Collectors.toList()));
    }
}
