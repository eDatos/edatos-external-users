package es.gobcan.istac.edatos.external.users.core.repository;

import es.gobcan.istac.edatos.external.users.EdatosExternalUsersCoreTestApp;
import es.gobcan.istac.edatos.external.users.core.domain.AuditEventEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.AuditPrincipalType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the PersistenceAuditEventRepository class.
 *
 * @see PersistenceAuditEventRepository
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EdatosExternalUsersCoreTestApp.class)
@Transactional
public class PersistenceAuditEventRepositoryTest {

    @Autowired
    private PersistenceAuditEventRepository persistenceAuditEventRepository;

    private AuditEventEntity testUserTypeAdminEvent;

    private AuditEventEntity testOtherUserTypeExternalEvent;

    private AuditEventEntity testUserTypeExternalEvent;

    @Before
    public void setup() {
        persistenceAuditEventRepository.deleteAll();
        Instant oneHourAgo = Instant.now().minusSeconds(3600);

        testUserTypeAdminEvent = new AuditEventEntity();
        testUserTypeAdminEvent.setPrincipal("test-user");
        testUserTypeAdminEvent.setPrincipalType(AuditPrincipalType.ADMIN);
        testUserTypeAdminEvent.setAuditEventType("test-type");
        testUserTypeAdminEvent.setAuditEventDate(oneHourAgo);
        Map<String, String> data = new HashMap<>();
        data.put("test-key", "test-value");
        testUserTypeAdminEvent.setData(data);

        testUserTypeExternalEvent = new AuditEventEntity();
        testUserTypeExternalEvent.setPrincipal("test-user");
        testUserTypeExternalEvent.setPrincipalType(AuditPrincipalType.EXTERNAL_USER);
        testUserTypeExternalEvent.setAuditEventType("test-type");
        testUserTypeExternalEvent.setAuditEventDate(oneHourAgo.minusSeconds(10000));

        testOtherUserTypeExternalEvent = new AuditEventEntity();
        testOtherUserTypeExternalEvent.setPrincipal("other-test-user");
        testOtherUserTypeExternalEvent.setPrincipalType(AuditPrincipalType.EXTERNAL_USER);
        testOtherUserTypeExternalEvent.setAuditEventType("test-type");
        testOtherUserTypeExternalEvent.setAuditEventDate(oneHourAgo);
    }

    @Test
    public void testFindAllByPrincipalAndPrincipalType() {
        persistenceAuditEventRepository.save(testUserTypeAdminEvent);
        persistenceAuditEventRepository.save(testUserTypeExternalEvent);
        persistenceAuditEventRepository.save(testOtherUserTypeExternalEvent);

        List<AuditEventEntity> events = persistenceAuditEventRepository.findAllByPrincipalAndPrincipalType("test-user", AuditPrincipalType.EXTERNAL_USER);
        assertThat(events).hasSize(1);
        AuditEventEntity event = events.get(0);
        assertThat(event.getPrincipal()).isEqualTo(testUserTypeExternalEvent.getPrincipal());
        assertThat(event.getPrincipalType()).isEqualTo(testUserTypeExternalEvent.getPrincipalType());
        assertThat(event.getAuditEventType()).isEqualTo("test-type");
    }
}
