package es.gobcan.istac.edatos.external.users.rest.internal.resources;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import es.gobcan.istac.edatos.external.users.EdatosExternalUsersRestTestApp;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FilterEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.PermalinkType;
import es.gobcan.istac.edatos.external.users.core.errors.ExceptionTranslator;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.repository.FilterRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EdatosExternalUsersRestTestApp.class)
@Transactional
public class FilterResourceInternalTest {

    private static final String ENDPOINT_URL = "/api/filters";

    private static final String PERMALINK_1 = "https://visualizer/data.html?permalink=1iehlgx1d7od76vr11r31d11j5o";
    private static final String PERMALINK_2 = "https://visualizer/data.html?permalink=vr0oanlyid8rvs613sya4ysbi";

    @Autowired
    private FilterResource filterResource;

    @Autowired
    private FilterRepository filterRepository;

    @Autowired
    private ExternalUserRepository externalUserRepository;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc mockMvc;
    private ExternalUserEntity user1;
    private ExternalUserEntity user2;
    private FilterEntity filter1;
    private FilterEntity filter2;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(filterResource)
                                      .setControllerAdvice(exceptionTranslator)
                                      .setCustomArgumentResolvers(pageableArgumentResolver)
                                      .build();
    }

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

        filter1 = new FilterEntity();
        filter1.setName("My filter 1");
        filter1.setPermalink(PERMALINK_1);
        filter1.setExternalUser(user1);
        filter1.setPermalinkType(PermalinkType.VISUALIZER);
        filterRepository.saveAndFlush(filter1);
        filter2 = new FilterEntity();
        filter2.setName("My filter 2");
        filter2.setPermalink(PERMALINK_2);
        filter2.setExternalUser(user1);
        filter2.setPermalinkType(PermalinkType.VISUALIZER);
        filterRepository.saveAndFlush(filter2);
    }

    @Test
    public void testListAllFilters() throws Exception {
        this.mockMvc.perform(get(ENDPOINT_URL))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("My filter 1")))
                    .andExpect(jsonPath("$[1].name", is("My filter 2")));
    }

    @Test
    public void testGetFilterByName() throws Exception {
        this.mockMvc.perform(get(ENDPOINT_URL + "?query=NAME ILIKE '%filter 2'"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name", is("My filter 2")));
    }

    @Test
    public void testGetFilterByUserLogin() throws Exception {
        this.mockMvc.perform(get(ENDPOINT_URL + "?query=EMAIL EQ 'user1@gmail.com'"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("My filter 1")))
                    .andExpect(jsonPath("$[1].name", is("My filter 2")));

        FilterEntity filter = new FilterEntity();
        filter.setName("User 2 filter");
        filter.setPermalink(PERMALINK_1);
        filter.setExternalUser(user2);
        filter.setPermalinkType(PermalinkType.VISUALIZER);
        filterRepository.saveAndFlush(filter);

        this.mockMvc.perform(get(ENDPOINT_URL + "?query=EMAIL EQ 'user2@gmail.com'"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name", is("User 2 filter")));
    }

    @Test
    public void testSortFiltersByUserEmail() throws Exception {
        filter2.setExternalUser(user2);
        filterRepository.saveAndFlush(filter2);

        this.mockMvc.perform(get(ENDPOINT_URL + "?sort=email,desc"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].externalUser.email", is("user2@gmail.com"))).andExpect(jsonPath("$[1].externalUser.email", is("user1@gmail.com")));
    }

    @Test
    public void testIfTheFilterDoesntExists404IsReturned() throws Exception {
        this.mockMvc.perform(get(ENDPOINT_URL + "/1"))
                    .andExpect(status().isNotFound());
    }
}
