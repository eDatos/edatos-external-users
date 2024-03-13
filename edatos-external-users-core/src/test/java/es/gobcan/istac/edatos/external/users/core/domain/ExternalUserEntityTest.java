package es.gobcan.istac.edatos.external.users.core.domain;

import es.gobcan.istac.edatos.external.users.EdatosExternalUsersCoreTestApp;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EdatosExternalUsersCoreTestApp.class)
public class ExternalUserEntityTest {


    @Autowired
    private ExternalUserRepository externalUserRepository;

    private ExternalUserEntity user1;
    private ExternalUserEntity user2;

    @Before
    public void populateDatabase() {
        user1 = new ExternalUserEntity();
        user1.setName("Montserrat");
        user1.setSurname1("Pons");
        user1.setSurname2("Ferrer");
        user1.setPassword("password1");
        user1.setEmail("user1@gmail.com");
        user1.setLanguage("ca");
        externalUserRepository.saveAndFlush(user1);
        user2 = new ExternalUserEntity();
        user2.setName("Mateo");
        user2.setSurname1("González");
        user2.setSurname2("Hernández");
        user2.setPassword("12345678");
        user2.setEmail("user2@gmail.com");
        user2.setLanguage("ca");
        externalUserRepository.saveAndFlush(user2);
    }

    @Test
    public void testCategorySetIsNotAddingDuplications() {
        Set<ExternalUserEntity> externalUserEntities = new HashSet<>();
        externalUserEntities.add(user1);
        externalUserEntities.add(externalUserRepository.findOneByEmailIgnoreCase("user1@gmail.com").orElse(null));
        externalUserEntities.add(user2);
        externalUserEntities.add(externalUserRepository.findOneByEmailIgnoreCase("user2@gmail.com").orElse(null));

        assertThat(externalUserEntities).hasSize(2);
    }
}
