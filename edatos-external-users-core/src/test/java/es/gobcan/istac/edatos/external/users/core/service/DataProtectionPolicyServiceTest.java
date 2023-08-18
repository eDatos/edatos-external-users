package es.gobcan.istac.edatos.external.users.core.service;

import java.util.List;
import java.util.stream.Collectors;

import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import es.gobcan.istac.edatos.external.users.EdatosExternalUsersCoreTestApp;
import es.gobcan.istac.edatos.external.users.core.config.MetadataProperties;
import es.gobcan.istac.edatos.external.users.core.domain.DataProtectionPolicyEntity;
import es.gobcan.istac.edatos.external.users.core.domain.vo.InternationalStringVO;
import es.gobcan.istac.edatos.external.users.core.domain.vo.LocalisedStringVO;
import es.gobcan.istac.edatos.external.users.core.repository.DataProtectionPolicyRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EdatosExternalUsersCoreTestApp.class)
public class DataProtectionPolicyServiceTest {

    @Autowired
    private DataProtectionPolicyRepository dataProtectionPolicyRepository;

    @Autowired
    private DataProtectionPolicyService dataProtectionPolicyService;

    @Autowired
    private MetadataProperties metadataProperties;

    private List<String> languages;

    @Before
    public void setup() {
        languages = metadataProperties.getAvailableLanguages().stream().map(language -> language.toLowerCase()).collect(Collectors.toList());

    }

    @Test
    public void testFindDataProtectionPolicyAddLanguages() throws Exception {
        DataProtectionPolicyEntity databaseEntity = dataProtectionPolicyRepository.findFirstByOrderByIdAsc();
        databaseEntity.getValue().removeAllTexts();
        dataProtectionPolicyRepository.saveAndFlush(databaseEntity);
        DataProtectionPolicyEntity returnedValue = dataProtectionPolicyService.find();

        assertThat(returnedValue.getId()).isEqualTo(databaseEntity.getId());
        assertThat(returnedValue.getOptLock()).isNotEqualTo(databaseEntity.getOptLock());
        assertThat(returnedValue.getCreatedBy()).isEqualTo(databaseEntity.getCreatedBy());
        assertThat(returnedValue.getCreatedDate()).isEqualTo(databaseEntity.getCreatedDate());
        assertThat(returnedValue.getValue().getTexts()).hasSize(languages.size());

        InternationalStringVO value = returnedValue.getValue();
        for (String language : languages) {
            assertThat(value.getLocalisedLabel(language, null)).isEmpty();
        }
    }

    @Test
    public void testFindDataProtectionPolicyRemoveLanguages() throws Exception {
        DataProtectionPolicyEntity databaseEntity = dataProtectionPolicyRepository.findFirstByOrderByIdAsc();
        InternationalStringVO updateParam = new InternationalStringVO();
        for (String language : languages) {
            LocalisedStringVO rightLocaleString = new LocalisedStringVO(language, language);
            updateParam.addText(rightLocaleString);
        }
        LocalisedStringVO wrongLocaleString = new LocalisedStringVO("klingon", "klingon");
        updateParam.addText(wrongLocaleString);
        databaseEntity.setValue(updateParam);
        dataProtectionPolicyRepository.saveAndFlush(databaseEntity);

        DataProtectionPolicyEntity returnedValue = dataProtectionPolicyService.find();
        InternationalStringVO value = returnedValue.getValue();

        assertThat(returnedValue.getId()).isEqualTo(databaseEntity.getId());
        assertThat(returnedValue.getOptLock()).isNotEqualTo(databaseEntity.getOptLock());
        assertThat(returnedValue.getCreatedBy()).isEqualTo(databaseEntity.getCreatedBy());
        assertThat(returnedValue.getCreatedDate()).isEqualTo(databaseEntity.getCreatedDate());
        for (String language : languages) {
            assertThat(value.getLocalisedLabel(language, null)).isEqualTo(language);
        }
        assertThat(value.getTexts()).hasSize(languages.size());
    }

    @Test
    public void testUpdateDataProtectionPolicyOnlyNeededLanguages() throws Exception {
        DataProtectionPolicyEntity databaseEntity = dataProtectionPolicyRepository.findFirstByOrderByIdAsc();
        DataProtectionPolicyEntity updateParam = new DataProtectionPolicyEntity();
        InternationalStringVO texts = new InternationalStringVO();
        for (String language : languages.subList(0, languages.size() - 1)) {
            LocalisedStringVO rightLocaleString = new LocalisedStringVO(language, language);
            texts.addText(rightLocaleString);
        }
        updateParam.setValue(texts);

        dataProtectionPolicyService.update(updateParam);
        DataProtectionPolicyEntity returnedValue = dataProtectionPolicyRepository.findFirstByOrderByIdAsc();
        InternationalStringVO value = returnedValue.getValue();

        assertThat(returnedValue.getId()).isEqualTo(databaseEntity.getId());
        assertThat(returnedValue.getOptLock()).isNotEqualTo(databaseEntity.getOptLock());
        assertThat(returnedValue.getCreatedBy()).isEqualTo(databaseEntity.getCreatedBy());
        assertThat(returnedValue.getCreatedDate()).isEqualTo(databaseEntity.getCreatedDate());
        for (String language : languages.subList(0, languages.size() - 1)) {
            assertThat(value.getLocalisedLabel(language, null)).isEqualTo(language);
        }
        assertThat(value.getTexts()).hasSize(languages.size() - 1);
    }

    @Test
    public void testUpdateDataProtectionPolicyIgnoreWrongLocales() throws Exception {
        DataProtectionPolicyEntity databaseEntity = dataProtectionPolicyRepository.findFirstByOrderByIdAsc();
        databaseEntity.getValue().removeAllTexts();
        databaseEntity.getValue().addText(new LocalisedStringVO("es", "es"));
        dataProtectionPolicyRepository.saveAndFlush(databaseEntity);

        DataProtectionPolicyEntity updateParam = new DataProtectionPolicyEntity();
        InternationalStringVO texts = new InternationalStringVO();
        texts.addText(new LocalisedStringVO("klingon", "klingon"));
        texts.addText(new LocalisedStringVO("Spanish klingon", "es"));
        updateParam.setValue(texts);

        dataProtectionPolicyService.update(updateParam);
        DataProtectionPolicyEntity returnedValue = dataProtectionPolicyRepository.findFirstByOrderByIdAsc();
        InternationalStringVO value = returnedValue.getValue();

        assertThat(returnedValue.getId()).isEqualTo(databaseEntity.getId());
        assertThat(returnedValue.getOptLock()).isNotEqualTo(databaseEntity.getOptLock());
        assertThat(returnedValue.getCreatedBy()).isEqualTo(databaseEntity.getCreatedBy());
        assertThat(returnedValue.getCreatedDate()).isEqualTo(databaseEntity.getCreatedDate());
        assertThat(value.getLocalisedLabel("klingon", null)).isNull();
        assertThat(value.getLocalisedLabel("es", null)).isEqualTo("Spanish klingon");
        assertThat(value.getTexts()).hasSize(1);
    }

    @Test
    public void testUpdateDataProtectionPolicyMandatoryDefaultLocale() {
        DataProtectionPolicyEntity databaseEntity = dataProtectionPolicyRepository.findFirstByOrderByIdAsc();
        databaseEntity.getValue().removeAllTexts();
        databaseEntity.getValue().addText(new LocalisedStringVO("en", "en"));
        dataProtectionPolicyRepository.saveAndFlush(databaseEntity);

        DataProtectionPolicyEntity updateParam = new DataProtectionPolicyEntity();
        InternationalStringVO texts = new InternationalStringVO();
        texts.addText(new LocalisedStringVO("klingon", "klingon"));
        updateParam.setValue(texts);

        Throwable thrown = catchThrowable(() -> dataProtectionPolicyService.update(updateParam));

        assertThat(thrown)
            .isInstanceOf(EDatosException.class)
            .hasMessageContaining(ServiceExceptionType.MISSING_DEFAULT_LOCALE_TRANSLATION.getMessageForReasonType().replaceAll("\\{0}.*", ""));
    }
}
