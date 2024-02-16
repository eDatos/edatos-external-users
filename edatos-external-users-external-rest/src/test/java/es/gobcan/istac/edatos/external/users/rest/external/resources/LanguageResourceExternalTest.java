package es.gobcan.istac.edatos.external.users.rest.external.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.gobcan.istac.edatos.external.users.EdatosExternalUsersRestTestApp;
import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EdatosExternalUsersRestTestApp.class)
@Transactional
public class LanguageResourceExternalTest {

    @Mock
    private MetadataProperties metadataProperties;

    @InjectMocks
    private LanguageResource languageResource;

    @Before
    public void setUp() {
        when(metadataProperties.getAvailableLanguages()).thenReturn(Arrays.asList("es", "ca"));
        when(metadataProperties.getInternationalizationLanguages()).thenReturn(Arrays.asList("en", "es", "fr", "ca"));
    }

    @Test
    public void getAllowedLanguagesReturnsCorrectValues() {
        ResponseEntity<List<String>> response = languageResource.getAllowedLanguages();
        assertEquals(Arrays.asList("es", "ca"), response.getBody());
    }

    @Test
    public void getInternationalizationLanguagesReturnsCorrectValues() {
        ResponseEntity<List<String>> response = languageResource.getInternationalizationLanguages();
        assertEquals(Arrays.asList("en", "es", "fr", "ca"), response.getBody());
    }

    @Test
    public void getAllowedLanguagesReturnsEmptyListWhenNoLanguagesAvailable() {
        when(metadataProperties.getAvailableLanguages()).thenReturn(Arrays.asList());
        ResponseEntity<List<String>> response = languageResource.getAllowedLanguages();
        assertEquals(Arrays.asList(), response.getBody());
    }

    @Test
    public void getInternationalizationLanguagesReturnsEmptyListWhenNoLanguagesAvailable() {
        when(metadataProperties.getInternationalizationLanguages()).thenReturn(Arrays.asList());
        ResponseEntity<List<String>> response = languageResource.getInternationalizationLanguages();
        assertEquals(Arrays.asList(), response.getBody());
    }

}
