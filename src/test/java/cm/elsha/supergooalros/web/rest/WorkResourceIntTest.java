package cm.elsha.supergooalros.web.rest;

import cm.elsha.supergooalros.SupergooalrosApp;
import cm.elsha.supergooalros.domain.Work;
import cm.elsha.supergooalros.repository.WorkRepository;
import cm.elsha.supergooalros.repository.search.WorkSearchRepository;

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
 * Test class for the WorkResource REST controller.
 *
 * @see WorkResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SupergooalrosApp.class)
@WebAppConfiguration
@IntegrationTest
public class WorkResourceIntTest {


    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final String DEFAULT_HEUREDEBUT = "AAAAA";
    private static final String UPDATED_HEUREDEBUT = "BBBBB";
    private static final String DEFAULT_HEURE_FIN = "AAAAA";
    private static final String UPDATED_HEURE_FIN = "BBBBB";

    private static final Integer DEFAULT_NOMBRE_TICKETS = 1;
    private static final Integer UPDATED_NOMBRE_TICKETS = 2;

    private static final Double DEFAULT_SOMME_ENCAISSEE = 1D;
    private static final Double UPDATED_SOMME_ENCAISSEE = 2D;

    @Inject
    private WorkRepository workRepository;

    @Inject
    private WorkSearchRepository workSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restWorkMockMvc;

    private Work work;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WorkResource workResource = new WorkResource();
        ReflectionTestUtils.setField(workResource, "workSearchRepository", workSearchRepository);
        ReflectionTestUtils.setField(workResource, "workRepository", workRepository);
        this.restWorkMockMvc = MockMvcBuilders.standaloneSetup(workResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        workSearchRepository.deleteAll();
        work = new Work();
        work.setDate(DEFAULT_DATE);
        work.setHeuredebut(DEFAULT_HEUREDEBUT);
        work.setHeureFin(DEFAULT_HEURE_FIN);
        work.setNombreTickets(DEFAULT_NOMBRE_TICKETS);
        work.setSommeEncaissee(DEFAULT_SOMME_ENCAISSEE);
    }

    @Test
    @Transactional
    public void createWork() throws Exception {
        int databaseSizeBeforeCreate = workRepository.findAll().size();

        // Create the Work

        restWorkMockMvc.perform(post("/api/works")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(work)))
                .andExpect(status().isCreated());

        // Validate the Work in the database
        List<Work> works = workRepository.findAll();
        assertThat(works).hasSize(databaseSizeBeforeCreate + 1);
        Work testWork = works.get(works.size() - 1);
        assertThat(testWork.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testWork.getHeuredebut()).isEqualTo(DEFAULT_HEUREDEBUT);
        assertThat(testWork.getHeureFin()).isEqualTo(DEFAULT_HEURE_FIN);
        assertThat(testWork.getNombreTickets()).isEqualTo(DEFAULT_NOMBRE_TICKETS);
        assertThat(testWork.getSommeEncaissee()).isEqualTo(DEFAULT_SOMME_ENCAISSEE);

        // Validate the Work in ElasticSearch
        Work workEs = workSearchRepository.findOne(testWork.getId());
        assertThat(workEs).isEqualToComparingFieldByField(testWork);
    }

    @Test
    @Transactional
    public void checkHeuredebutIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRepository.findAll().size();
        // set the field null
        work.setHeuredebut(null);

        // Create the Work, which fails.

        restWorkMockMvc.perform(post("/api/works")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(work)))
                .andExpect(status().isBadRequest());

        List<Work> works = workRepository.findAll();
        assertThat(works).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkHeureFinIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRepository.findAll().size();
        // set the field null
        work.setHeureFin(null);

        // Create the Work, which fails.

        restWorkMockMvc.perform(post("/api/works")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(work)))
                .andExpect(status().isBadRequest());

        List<Work> works = workRepository.findAll();
        assertThat(works).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNombreTicketsIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRepository.findAll().size();
        // set the field null
        work.setNombreTickets(null);

        // Create the Work, which fails.

        restWorkMockMvc.perform(post("/api/works")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(work)))
                .andExpect(status().isBadRequest());

        List<Work> works = workRepository.findAll();
        assertThat(works).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSommeEncaisseeIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRepository.findAll().size();
        // set the field null
        work.setSommeEncaissee(null);

        // Create the Work, which fails.

        restWorkMockMvc.perform(post("/api/works")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(work)))
                .andExpect(status().isBadRequest());

        List<Work> works = workRepository.findAll();
        assertThat(works).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWorks() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the works
        restWorkMockMvc.perform(get("/api/works?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(work.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].heuredebut").value(hasItem(DEFAULT_HEUREDEBUT.toString())))
                .andExpect(jsonPath("$.[*].heureFin").value(hasItem(DEFAULT_HEURE_FIN.toString())))
                .andExpect(jsonPath("$.[*].nombreTickets").value(hasItem(DEFAULT_NOMBRE_TICKETS)))
                .andExpect(jsonPath("$.[*].sommeEncaissee").value(hasItem(DEFAULT_SOMME_ENCAISSEE.doubleValue())));
    }

    @Test
    @Transactional
    public void getWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get the work
        restWorkMockMvc.perform(get("/api/works/{id}", work.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(work.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.heuredebut").value(DEFAULT_HEUREDEBUT.toString()))
            .andExpect(jsonPath("$.heureFin").value(DEFAULT_HEURE_FIN.toString()))
            .andExpect(jsonPath("$.nombreTickets").value(DEFAULT_NOMBRE_TICKETS))
            .andExpect(jsonPath("$.sommeEncaissee").value(DEFAULT_SOMME_ENCAISSEE.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingWork() throws Exception {
        // Get the work
        restWorkMockMvc.perform(get("/api/works/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);
        workSearchRepository.save(work);
        int databaseSizeBeforeUpdate = workRepository.findAll().size();

        // Update the work
        Work updatedWork = new Work();
        updatedWork.setId(work.getId());
        updatedWork.setDate(UPDATED_DATE);
        updatedWork.setHeuredebut(UPDATED_HEUREDEBUT);
        updatedWork.setHeureFin(UPDATED_HEURE_FIN);
        updatedWork.setNombreTickets(UPDATED_NOMBRE_TICKETS);
        updatedWork.setSommeEncaissee(UPDATED_SOMME_ENCAISSEE);

        restWorkMockMvc.perform(put("/api/works")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedWork)))
                .andExpect(status().isOk());

        // Validate the Work in the database
        List<Work> works = workRepository.findAll();
        assertThat(works).hasSize(databaseSizeBeforeUpdate);
        Work testWork = works.get(works.size() - 1);
        assertThat(testWork.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testWork.getHeuredebut()).isEqualTo(UPDATED_HEUREDEBUT);
        assertThat(testWork.getHeureFin()).isEqualTo(UPDATED_HEURE_FIN);
        assertThat(testWork.getNombreTickets()).isEqualTo(UPDATED_NOMBRE_TICKETS);
        assertThat(testWork.getSommeEncaissee()).isEqualTo(UPDATED_SOMME_ENCAISSEE);

        // Validate the Work in ElasticSearch
        Work workEs = workSearchRepository.findOne(testWork.getId());
        assertThat(workEs).isEqualToComparingFieldByField(testWork);
    }

    @Test
    @Transactional
    public void deleteWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);
        workSearchRepository.save(work);
        int databaseSizeBeforeDelete = workRepository.findAll().size();

        // Get the work
        restWorkMockMvc.perform(delete("/api/works/{id}", work.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean workExistsInEs = workSearchRepository.exists(work.getId());
        assertThat(workExistsInEs).isFalse();

        // Validate the database is empty
        List<Work> works = workRepository.findAll();
        assertThat(works).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);
        workSearchRepository.save(work);

        // Search the work
        restWorkMockMvc.perform(get("/api/_search/works?query=id:" + work.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(work.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].heuredebut").value(hasItem(DEFAULT_HEUREDEBUT.toString())))
            .andExpect(jsonPath("$.[*].heureFin").value(hasItem(DEFAULT_HEURE_FIN.toString())))
            .andExpect(jsonPath("$.[*].nombreTickets").value(hasItem(DEFAULT_NOMBRE_TICKETS)))
            .andExpect(jsonPath("$.[*].sommeEncaissee").value(hasItem(DEFAULT_SOMME_ENCAISSEE.doubleValue())));
    }
}
