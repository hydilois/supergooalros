package cm.elsha.supergooalros.web.rest;

import cm.elsha.supergooalros.SupergooalrosApp;
import cm.elsha.supergooalros.domain.Conge;
import cm.elsha.supergooalros.repository.CongeRepository;
import cm.elsha.supergooalros.repository.search.CongeSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the CongeResource REST controller.
 *
 * @see CongeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SupergooalrosApp.class)
@WebAppConfiguration
@IntegrationTest
public class CongeResourceIntTest {


    private static final LocalDate DEFAULT_DATE_DEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_DEBUT = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FIN = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private CongeRepository congeRepository;

    @Inject
    private CongeSearchRepository congeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCongeMockMvc;

    private Conge conge;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CongeResource congeResource = new CongeResource();
        ReflectionTestUtils.setField(congeResource, "congeSearchRepository", congeSearchRepository);
        ReflectionTestUtils.setField(congeResource, "congeRepository", congeRepository);
        this.restCongeMockMvc = MockMvcBuilders.standaloneSetup(congeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        congeSearchRepository.deleteAll();
        conge = new Conge();
        conge.setDateDebut(DEFAULT_DATE_DEBUT);
        conge.setDateFin(DEFAULT_DATE_FIN);
    }

    @Test
    @Transactional
    public void createConge() throws Exception {
        int databaseSizeBeforeCreate = congeRepository.findAll().size();

        // Create the Conge

        restCongeMockMvc.perform(post("/api/conges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(conge)))
                .andExpect(status().isCreated());

        // Validate the Conge in the database
        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeCreate + 1);
        Conge testConge = conges.get(conges.size() - 1);
        assertThat(testConge.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testConge.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);

        // Validate the Conge in ElasticSearch
        Conge congeEs = congeSearchRepository.findOne(testConge.getId());
        assertThat(congeEs).isEqualToComparingFieldByField(testConge);
    }

    @Test
    @Transactional
    public void getAllConges() throws Exception {
        // Initialize the database
        congeRepository.saveAndFlush(conge);

        // Get all the conges
        restCongeMockMvc.perform(get("/api/conges?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(conge.getId().intValue())))
                .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
                .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())));
    }

    @Test
    @Transactional
    public void getConge() throws Exception {
        // Initialize the database
        congeRepository.saveAndFlush(conge);

        // Get the conge
        restCongeMockMvc.perform(get("/api/conges/{id}", conge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(conge.getId().intValue()))
            .andExpect(jsonPath("$.dateDebut").value(DEFAULT_DATE_DEBUT.toString()))
            .andExpect(jsonPath("$.dateFin").value(DEFAULT_DATE_FIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingConge() throws Exception {
        // Get the conge
        restCongeMockMvc.perform(get("/api/conges/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateConge() throws Exception {
        // Initialize the database
        congeRepository.saveAndFlush(conge);
        congeSearchRepository.save(conge);
        int databaseSizeBeforeUpdate = congeRepository.findAll().size();

        // Update the conge
        Conge updatedConge = new Conge();
        updatedConge.setId(conge.getId());
        updatedConge.setDateDebut(UPDATED_DATE_DEBUT);
        updatedConge.setDateFin(UPDATED_DATE_FIN);

        restCongeMockMvc.perform(put("/api/conges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedConge)))
                .andExpect(status().isOk());

        // Validate the Conge in the database
        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeUpdate);
        Conge testConge = conges.get(conges.size() - 1);
        assertThat(testConge.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testConge.getDateFin()).isEqualTo(UPDATED_DATE_FIN);

        // Validate the Conge in ElasticSearch
        Conge congeEs = congeSearchRepository.findOne(testConge.getId());
        assertThat(congeEs).isEqualToComparingFieldByField(testConge);
    }

    @Test
    @Transactional
    public void deleteConge() throws Exception {
        // Initialize the database
        congeRepository.saveAndFlush(conge);
        congeSearchRepository.save(conge);
        int databaseSizeBeforeDelete = congeRepository.findAll().size();

        // Get the conge
        restCongeMockMvc.perform(delete("/api/conges/{id}", conge.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean congeExistsInEs = congeSearchRepository.exists(conge.getId());
        assertThat(congeExistsInEs).isFalse();

        // Validate the database is empty
        List<Conge> conges = congeRepository.findAll();
        assertThat(conges).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchConge() throws Exception {
        // Initialize the database
        congeRepository.saveAndFlush(conge);
        congeSearchRepository.save(conge);

        // Search the conge
        restCongeMockMvc.perform(get("/api/_search/conges?query=id:" + conge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conge.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())));
    }
}
