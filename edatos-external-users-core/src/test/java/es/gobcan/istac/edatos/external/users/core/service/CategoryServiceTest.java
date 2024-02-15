package es.gobcan.istac.edatos.external.users.core.service;

import static es.gobcan.istac.edatos.external.users.util.CategoryEntityBuilder.categoryBuilder;
import static es.gobcan.istac.edatos.external.users.util.CollectionUtils.toSingleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.siemac.edatos.core.common.exception.EDatosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import es.gobcan.istac.edatos.external.users.EdatosExternalUsersCoreTestApp;
import es.gobcan.istac.edatos.external.users.core.domain.CategoryEntity;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FavoriteEntity;
import es.gobcan.istac.edatos.external.users.core.errors.ServiceExceptionType;
import es.gobcan.istac.edatos.external.users.core.repository.CategoryRepository;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EdatosExternalUsersCoreTestApp.class)
public class CategoryServiceTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExternalUserService externalUserService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private CategoryService categoryService;

    private CategoryEntity node1;
    private CategoryEntity node1_1;
    private CategoryEntity node1_1_1;
    private CategoryEntity node1_1_2;
    private CategoryEntity node1_1_3;
    private CategoryEntity node1_1_4;
    private CategoryEntity node1_2;
    private CategoryEntity node1_3;
    private CategoryEntity node1_4;

    @Before
    public void populateDatabase() {
        node1 = categoryBuilder().setName("Economía").build();
        node1_1 = categoryBuilder().setName("Estadísticas sectoriales").build();
        node1_1_1 = categoryBuilder().setName("Energía").build();
        node1_1_2 = categoryBuilder().setName("Transporte").build();
        node1_1_3 = categoryBuilder().setName("Turismo").build();
        node1_1_4 = categoryBuilder().setName("Banca").build();
        node1_2 = categoryBuilder().setName("Ciencia, tecnología e innovación").build();
        node1_3 = categoryBuilder().setName("Coste laboral").build();
        node1_4 = categoryBuilder().setName("Estadísticas macroeconómicas").build();

        node1.addChildren(node1_1, node1_2, node1_3, node1_4);
        node1_1.addChildren(node1_1_1, node1_1_2, node1_1_3, node1_1_4);

        categoryRepository.saveAndFlush(node1);
    }

    @Test
    public void testTreeHasTheCorrectStructure() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(9);
        for (CategoryEntity category : categories) {
            assertThat(category.getIndex()).isNotNull();
        }

        List<CategoryEntity> tree = categoryService.getTree();
        assertThat(tree).hasSize(1);

        CategoryEntity n1 = tree.get(0);
        assertThat(n1).isEqualTo(node1);
        assertThat(n1.getChildren()).hasSize(4);
        assertThat(n1.getChildren().toArray()).containsExactlyInAnyOrder(node1.getChildren().toArray());

        CategoryEntity n1_1 = n1.getChildren().stream().filter(node -> Objects.equals(node.getId(), node1_1.getId())).collect(toSingleton());
        assertThat(n1_1).isNotNull();
        assertThat(n1_1.getChildren()).hasSize(4);
        assertThat(n1_1.getChildren().toArray()).containsExactlyInAnyOrder(node1_1.getChildren().toArray());
    }

    @Test
    public void testUpdateTreeMoveLeafNodeToUpperLevel() {
        node1.addChild(node1_1_4);
        categoryService.updateTree(Collections.singletonList(node1));

        List<CategoryEntity> tree = categoryService.getTree();
        CategoryEntity n1 = tree.get(0);
        assertThat(n1.getChildren()).hasSize(5);
        assertThat(n1.getChildren()).contains(node1_1_4);

        CategoryEntity n1_1 = n1.getChildren().stream().filter(node -> Objects.equals(node.getId(), node1_1.getId())).collect(toSingleton());
        assertThat(n1_1.getChildren()).hasSize(3);
    }

    @Test
    public void testUpdateTreeMoveBranchToLowerLevel() {
        node1_4.addChildren(node1_1);
        categoryService.updateTree(Collections.singletonList(node1));

        List<CategoryEntity> tree = categoryService.getTree();
        CategoryEntity n1 = tree.get(0);
        assertThat(n1.getChildren()).hasSize(3);

        CategoryEntity n1_4 = n1.getChildren().stream().filter(node -> Objects.equals(node.getId(), node1_4.getId())).collect(toSingleton());
        assertThat(n1_4.getChildren()).hasSize(1);
        assertThat(n1_4.getChildren()).contains(node1_1);
    }

    @Test
    public void testUpdateTreeAddNewRootBranch() {
        CategoryEntity newBranch = categoryBuilder().setName("Categoría raíz 2").addChildren(categoryBuilder().setName("Categoría hija de la raíz 2").build()).build();
        categoryService.updateTree(Collections.singletonList(newBranch));

        List<CategoryEntity> tree = categoryService.getTree();
        assertThat(tree).hasSize(2);
        assertThat(tree).containsExactlyInAnyOrder(node1, newBranch);
    }

    @Test
    public void testDeleteLeafNode() {
        categoryService.delete(node1_1_4.getId());
        assertThat(node1_1.getChildren()).hasSize(3);
        assertThat(categoryRepository.findAll()).hasSize(8);
    }

    @Test
    public void testDeleteNonLeafNode() {
        categoryService.delete(node1_1.getId());
        assertThat(node1.getChildren()).hasSize(3);
        assertThat(categoryRepository.findAll()).hasSize(4);
    }

    @Test
    public void testDeleteRootNode() {
        categoryService.delete(node1.getId());
        assertThat(categoryRepository.findAll()).isEmpty();
    }

    @Test
    public void testCannotDeleteCategoryIfItHasSubscribers() {
        ExternalUserEntity externalUser = new ExternalUserEntity();
        externalUser.setName("User 1");
        externalUser.setSurname1("Surname1");
        externalUser.setSurname2("Surname2");
        externalUser.setEmail("user@user.com");
        externalUser.setPassword("password1");
        externalUser.setLanguage("es");
        externalUserService.create(externalUser);

        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setCategory(node1);
        favorite.setExternalUser(externalUser);
        favoriteService.create(favorite);

        Throwable thrown = catchThrowable(() -> categoryService.delete(node1.getId()));

        assertThat(thrown)
            .isInstanceOf(EDatosException.class)
            .hasMessageContaining(ServiceExceptionType.CANNOT_DELETE_CATEGORY_IF_IT_HAS_SUBSCRIBERS.getMessageForReasonType());
    }
}
