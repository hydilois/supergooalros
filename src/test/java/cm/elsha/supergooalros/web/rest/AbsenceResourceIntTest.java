package cm.elsha.supergooalros.web.rest;

import cm.elsha.supergooalros.SupergooalrosApp;
import cm.elsha.supergooalros.domain.Absence;
import cm.elsha.supergooalros.repository.AbsenceRepository;
import cm.elsha.supergooalros.repository.search.AbsenceSearchRepository;

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
 * Test class for the AbsenceResource REST controller.
 *
 * @see AbsenceResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SupergooalrosApp.class)
@WebAppConfiguration
@IntegrationTest
public class AbsenceResourceIntTest {


    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_JUSTIFIE = false;
    private static final Boolean UPDATED_JUSTIFIE = true;
    private static final String DEFAULT_JUSTIFICATION = "AAAAA";
    private static final String UPDATED_JUSTIFICATION = "BBBBB";

    @Inject
    private AbsenceRepository absenceRepository;

    @Inject
    private AbsenceSearchRepository absenceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAbsenceMockMvc;

    private Absence absence;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AbsenceResource absenceResource = new AbsenceResource();
        ReflectionTestUtils.setField(absenceResource, "absenceSearchRepository", absenceSearchRepository);
        ReflectionTestUtils.setField(absenceResource, "absenceRepository", absenceRepository);
        this.restAbsenceMockMvc = MockMvcBuilders.standaloneSetup(absenceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        absenceSearchRepository.deleteAll();
        absence = new Absence();
        absence.setDate(DEFAULT_DATE);
        absence.setJustifie(DEFAULT_JUSTIFIE);
        absence.setJustification(DEFAULT_JUSTIFICATION);
    }

    @Test
    @Transactional
    public void createAbsence() throws Exception {
        int databaseSizeBeforeCreate = absenceRepository.findAll().size();

        // Create the Absence

        restAbsenceMockMvc.perform(post("/api/absences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(absence)))
                .andExpect(status().isCreated());

        // Validate the Absence in the database
        List<Absence> absences = absenceRepository.findAll();
        assertThat(absences).hasSize(databaseSizeBeforeCreate + 1);
        Absence testAbsence = absences.get(absences.size() - 1);
        assertThat(testAbsence.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testAbsence.isJustifie()).isEqualTo(DEFAULT_JUSTIFIE);
        assertThat(testAbsence.getJustification()).isEqualTo(DEFAULT_JUSTIFICATION);

        // Validate the Absence in ElasticSearch
        Absence absenceEs = absenceSearchRepository.findOne(testAbsence.getId());
        assertThat(absenceEs).isEqualToComparingFieldByField(testAbsence);
    }

    @Test
    @Transactional
    public void getAllAbsences() throws Exception {
        // Initialize the database
        absenceRepository.saveAndFlush(absence);

        // Get all the absences
        restAbsenceMockMvc.perform(get("/api/absences?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(absence.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].justifie").value(hasItem(DEFAULT_JUSTIFIE.booleanValue())))
                .andExpect(jsonPath("$.[*].justification").value(hasItem(DEFAULT_JUSTIFICATION.toString())));
    }

    @Test
    @Transactional
    public void getAbsence() throws Exception {
        // Initialize the database
        absenceRepository.saveAndFlush(absence);

        // Get the absence
        restAbsenceMockMvc.perform(get("/api/absences/{id}", absence.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(absence.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.justifie").value(DEFAULT_JUSTIFIE.booleanValue()))
            .andExpect(jsonPath("$.justification").value(DEFAULT_JUSTIFICATION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAbsence() throws Exception {
        // Get the absence
        restAbsenceMockMvc.perform(get("/api/absences/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAbsence() throws Exception {
        // Initialize the database
        absenceRepository.saveAndFlush(absence);
        absenceSearchRepository.save(absence);
        int databaseSizeBeforeUpdate = absenceRepository.findAll().size();

        // Update the absence
        Absence updatedAbsence = new Absence();
        updatedAbsence.setId(absence.getId());
        updatedAbsence.setDate(UPDATED_DATE);
        updatedAbsence.setJustifie(UPDATED_JUSTIFIE);
        updatedAbsence.setJustification(UPDATED_JUSTIFICATION);

        restAbsenceMockMvc.perform(put("/api/absences")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAbsence)))
                .andExpect(status().isOk());

        // Validate the Absence in the database
        List<Absence> absences = absenceRepository.findAll();
        assertThat(absences).hasSize(databaseSizeBeforeUpdate);
        Absence testAbsence = absences.get(absences.size() - 1);
        assertThat(testAbsence.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testAbsence.isJustifie()).isEqualTo(UPDATED_JUSTIFIE);
        assertThat(testAbsence.getJustification()).isEqualTo(UPDATED_JUSTIFICATION);

        // Validate the Absence in ElasticSearch
        Absence absenceEs = absenceSearchRepository.findOne(testAbsence.getId());
        assertThat(absenceEs).isEqualToComparingFieldByField(testAbsence);
    }

    @Test
    @Transactional
    public void deleteAbsence() throws Exception {
        // Initialize the database
        absenceRepository.saveAndFlush(absence);
        absenceSearchRepository.save(absence);
        int databaseSizeBeforeDelete = absenceRepository.findAll().size();

        // Get the absence
        restAbsenceMockMvc.perform(delete("/api/absences/{id}", absence.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean absenceExistsInEs = absenceSearchRepository.exists(absence.getId());
        assertThat(absenceExistsInEs).isFalse();

        // Validate the database is empty
        List<Absence> absences = absenceRepository.findAll();
        assertThat(absences).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAbsence() throws Exception {
        // Initialize the database
        absenceRepository.saveAndFlush(absence);
        absenceSearchRepository.save(absence);

        // Search the absence
        restAbsenceMockMvc.perform(get("/api/_search/absences?query=id:" + absence.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(absence.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].justifie").value(hasItem(DEFAULT_JUSTIFIE.booleanValue())))
            .andExpect(jsonPath("$.[*].justification").value(hasItem(DEFAULT_JUSTIFICATION.toString())));
    }
}
