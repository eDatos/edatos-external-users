package es.gobcan.istac.edatos.external.users.core.domain;

import es.gobcan.istac.edatos.external.users.EdatosExternalUsersCoreTestApp;
import es.gobcan.istac.edatos.external.users.core.repository.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static es.gobcan.istac.edatos.external.users.util.CategoryEntityBuilder.categoryBuilder;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EdatosExternalUsersCoreTestApp.class)
public class CategoryEntityTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private CategoryEntity node1;
    private CategoryEntity node1_1;
    private CategoryEntity node1_1_1;

    @Before
    public void populateDatabase() {
        node1 = categoryBuilder().setName("Economía").build();
        node1_1 = categoryBuilder().setName("Estadísticas sectoriales").build();
        node1_1_1 = categoryBuilder().setName("Energía").build();

        node1.addChildren(node1_1);
        node1_1.addChildren(node1_1_1);

        categoryRepository.saveAndFlush(node1);
    }

    @Test
    public void testCategorySetIsNotAddingDuplications() {
        Set<CategoryEntity> categoryEntities = new HashSet<>();
        categoryEntities.add(node1_1_1);
        categoryEntities.add(node1_1_1.getParent());
        categoryEntities.add(node1_1);
        categoryEntities.add(node1_1.getParent());
        categoryEntities.add(node1);

        assertThat(categoryEntities).hasSize(3);
    }
}
