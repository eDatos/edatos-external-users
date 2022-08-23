package es.gobcan.istac.edatos.external.users.rest.external.resources;

import es.gobcan.istac.edatos.external.users.core.domain.enumeration.PermalinkType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import es.gobcan.istac.edatos.external.users.EdatosExternalUsersRestTestApp;
import es.gobcan.istac.edatos.external.users.core.domain.ExternalUserEntity;
import es.gobcan.istac.edatos.external.users.core.domain.FilterEntity;
import es.gobcan.istac.edatos.external.users.core.domain.enumeration.Language;
import es.gobcan.istac.edatos.external.users.core.errors.ExceptionTranslator;
import es.gobcan.istac.edatos.external.users.core.repository.ExternalUserRepository;
import es.gobcan.istac.edatos.external.users.core.repository.FilterRepository;
import es.gobcan.istac.edatos.external.users.rest.common.dto.FilterDto;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.ExternalUserMapper;
import es.gobcan.istac.edatos.external.users.rest.common.mapper.FilterMapper;
import es.gobcan.istac.edatos.external.users.rest.common.util.TestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EdatosExternalUsersRestTestApp.class)
@Transactional
public class FilterResourceExternalTest {

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
    private ExternalUserMapper externalUserMapper;

    @Autowired
    private FilterMapper filterMapper;

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
        this.mockMvc = MockMvcBuilders.standaloneSetup(filterResource).setControllerAdvice(exceptionTranslator).setCustomArgumentResolvers(pageableArgumentResolver).build();
    }

    @Before
    public void populateDatabase() {
        user1 = new ExternalUserEntity();
        user1.setName("Montserrat");
        user1.setSurname1("Pons");
        user1.setSurname2("Ferrer");
        user1.setPassword("password1");
        user1.setEmail("user1@gmail.com");
        user1.setLanguage(Language.CATALAN);
        externalUserRepository.saveAndFlush(user1);
        user2 = new ExternalUserEntity();
        user2.setName("Mateo");
        user2.setSurname1("González");
        user2.setSurname2("Hernández");
        user2.setPassword("12345678");
        user2.setEmail("user2@gmail.com");
        user2.setLanguage(Language.CATALAN);
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
    @WithMockUser(username = "user1@gmail.com", authorities = {"USER"}, password = "user")
    public void testSaveTwoFiltersWithTheSameUserAndPermalink() throws Exception {
        filter2.setPermalink(filter1.getPermalink());
        this.mockMvc.perform(post(ENDPOINT_URL).contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(filterMapper.toDto(filter2)))).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1@gmail.com", authorities = {"USER"}, password = "user")
    public void testSaveANonUrlPermalink() throws Exception {
        filter1.setPermalink("not a url");
        this.mockMvc.perform(post(ENDPOINT_URL).contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(filterMapper.toDto(filter1)))).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user1@gmail.com", authorities = {"USER"}, password = "user")
    public void testCreateFilterFail() throws Exception {
        FilterDto dto = new FilterDto();
        dto.setExternalUser(externalUserMapper.toDto(user2));
        dto.setPermalink(PERMALINK_1);
        this.mockMvc.perform(post(ENDPOINT_URL).contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(dto))).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user2@gmail.com", authorities = {"USER"}, password = "user")
    public void testCreateFilterSucceeds() throws Exception {
        FilterDto dto = new FilterDto();
        dto.setPermalink(PERMALINK_1);
        dto.setResourceName("resource name");
        dto.setPermalinkType(PermalinkType.VISUALIZER);
        this.mockMvc.perform(post(ENDPOINT_URL).contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(dto))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("resource name")));

        dto.setName("filter name");
        dto.setPermalink(PERMALINK_2);
        this.mockMvc.perform(post(ENDPOINT_URL).contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(dto))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("filter name")));
    }

    @Test
    @WithMockUser(username = "user1@gmail.com", authorities = {"USER"}, password = "user")
    public void testCanUpdateFilter() throws Exception {
        FilterDto dto = filterMapper.toDto(filter1);
        dto.setName("My new filter name");

        this.mockMvc.perform(put(ENDPOINT_URL).contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(dto))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("My new filter name")));

        assertThat(filter1.getLastModifiedDate()).isNotEqualTo(dto.getLastModifiedDate());
    }

    @Test
    @WithMockUser(username = "user1@gmail.com", authorities = {"USER"}, password = "user")
    public void testDeleteFilter() throws Exception {
        this.mockMvc.perform(delete(ENDPOINT_URL + "/" + filter1.getId())).andExpect(status().isNoContent());
        assertThat(filterRepository.findOne(filter1.getId())).isNull();
    }

    @Test
    @WithMockUser(username = "user1@gmail.com", authorities = {"USER"}, password = "user")
    public void testGetFilters() throws Exception {
        this.mockMvc.perform(get(ENDPOINT_URL)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].name", is("My filter 1")))
                .andExpect(jsonPath("$[1].name", is("My filter 2")));
    }

    @Test
    @WithMockUser(username = "user1@gmail.com", authorities = {"USER"}, password = "user")
    public void testGetFilterByName() throws Exception {
        this.mockMvc.perform(get(ENDPOINT_URL + "?query=NAME ILIKE '%filter 2'")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("My filter 2")));
    }

    @Test
    @WithMockUser(username = "user1@gmail.com", authorities = {"USER"}, password = "user")
    public void testIfTheFilterDoesntExists404IsReturned() throws Exception {
        this.mockMvc.perform(get(ENDPOINT_URL + "/8")).andExpect(status().isNotFound());
    }
}
