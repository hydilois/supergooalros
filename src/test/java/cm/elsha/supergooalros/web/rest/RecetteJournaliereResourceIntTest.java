package cm.elsha.supergooalros.web.rest;

import cm.elsha.supergooalros.SupergooalrosApp;
import cm.elsha.supergooalros.domain.RecetteJournaliere;
import cm.elsha.supergooalros.repository.RecetteJournaliereRepository;
import cm.elsha.supergooalros.repository.search.RecetteJournaliereSearchRepository;

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
 * Test class for the RecetteJournaliereResource REST controller.
 *
 * @see RecetteJournaliereResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SupergooalrosApp.class)
@WebAppConfiguration
@IntegrationTest
public class RecetteJournaliereResourceIntTest {


    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_MONTANT = 1D;
    private static final Double UPDATED_MONTANT = 2D;

    @Inject
    private RecetteJournaliereRepository recetteJournaliereRepository;

    @Inject
    private RecetteJournaliereSearchRepository recetteJournaliereSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRecetteJournaliereMockMvc;

    private RecetteJournaliere recetteJournaliere;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RecetteJournaliereResource recetteJournaliereResource = new RecetteJournaliereResource();
        ReflectionTestUtils.setField(recetteJournaliereResource, "recetteJournaliereSearchRepository", recetteJournaliereSearchRepository);
        ReflectionTestUtils.setField(recetteJournaliereResource, "recetteJournaliereRepository", recetteJournaliereRepository);
        this.restRecetteJournaliereMockMvc = MockMvcBuilders.standaloneSetup(recetteJournaliereResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        recetteJournaliereSearchRepository.deleteAll();
        recetteJournaliere = new RecetteJournaliere();
        recetteJournaliere.setDate(DEFAULT_DATE);
        recetteJournaliere.setMontant(DEFAULT_MONTANT);
    }

    @Test
    @Transactional
    public void createRecetteJournaliere() throws Exception {
        int databaseSizeBeforeCreate = recetteJournaliereRepository.findAll().size();

        // Create the RecetteJournaliere

        restRecetteJournaliereMockMvc.perform(post("/api/recette-journalieres")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recetteJournaliere)))
                .andExpect(status().isCreated());

        // Validate the RecetteJournaliere in the database
        List<RecetteJournaliere> recetteJournalieres = recetteJournaliereRepository.findAll();
        assertThat(recetteJournalieres).hasSize(databaseSizeBeforeCreate + 1);
        RecetteJournaliere testRecetteJournaliere = recetteJournalieres.get(recetteJournalieres.size() - 1);
        assertThat(testRecetteJournaliere.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testRecetteJournaliere.getMontant()).isEqualTo(DEFAULT_MONTANT);

        // Validate the RecetteJournaliere in ElasticSearch
        RecetteJournaliere recetteJournaliereEs = recetteJournaliereSearchRepository.findOne(testRecetteJournaliere.getId());
        assertThat(recetteJournaliereEs).isEqualToComparingFieldByField(testRecetteJournaliere);
    }

    @Test
    @Transactional
    public void getAllRecetteJournalieres() throws Exception {
        // Initialize the database
        recetteJournaliereRepository.saveAndFlush(recetteJournaliere);

        // Get all the recetteJournalieres
        restRecetteJournaliereMockMvc.perform(get("/api/recette-journalieres?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(recetteJournaliere.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())));
    }

    @Test
    @Transactional
    public void getRecetteJournaliere() throws Exception {
        // Initialize the database
        recetteJournaliereRepository.saveAndFlush(recetteJournaliere);

        // Get the recetteJournaliere
        restRecetteJournaliereMockMvc.perform(get("/api/recette-journalieres/{id}", recetteJournaliere.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(recetteJournaliere.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.montant").value(DEFAULT_MONTANT.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRecetteJournaliere() throws Exception {
        // Get the recetteJournaliere
        restRecetteJournaliereMockMvc.perform(get("/api/recette-journalieres/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecetteJournaliere() throws Exception {
        // Initialize the database
        recetteJournaliereRepository.saveAndFlush(recetteJournaliere);
        recetteJournaliereSearchRepository.save(recetteJournaliere);
        int databaseSizeBeforeUpdate = recetteJournaliereRepository.findAll().size();

        // Update the recetteJournaliere
        RecetteJournaliere updatedRecetteJournaliere = new RecetteJournaliere();
        updatedRecetteJournaliere.setId(recetteJournaliere.getId());
        updatedRecetteJournaliere.setDate(UPDATED_DATE);
        updatedRecetteJournaliere.setMontant(UPDATED_MONTANT);

        restRecetteJournaliereMockMvc.perform(put("/api/recette-journalieres")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRecetteJournaliere)))
                .andExpect(status().isOk());

        // Validate the RecetteJournaliere in the database
        List<RecetteJournaliere> recetteJournalieres = recetteJournaliereRepository.findAll();
        assertThat(recetteJournalieres).hasSize(databaseSizeBeforeUpdate);
        RecetteJournaliere testRecetteJournaliere = recetteJournalieres.get(recetteJournalieres.size() - 1);
        assertThat(testRecetteJournaliere.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testRecetteJournaliere.getMontant()).isEqualTo(UPDATED_MONTANT);

        // Validate the RecetteJournaliere in ElasticSearch
        RecetteJournaliere recetteJournaliereEs = recetteJournaliereSearchRepository.findOne(testRecetteJournaliere.getId());
        assertThat(recetteJournaliereEs).isEqualToComparingFieldByField(testRecetteJournaliere);
    }

    @Test
    @Transactional
    public void deleteRecetteJournaliere() throws Exception {
        // Initialize the database
        recetteJournaliereRepository.saveAndFlush(recetteJournaliere);
        recetteJournaliereSearchRepository.save(recetteJournaliere);
        int databaseSizeBeforeDelete = recetteJournaliereRepository.findAll().size();

        // Get the recetteJournaliere
        restRecetteJournaliereMockMvc.perform(delete("/api/recette-journalieres/{id}", recetteJournaliere.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean recetteJournaliereExistsInEs = recetteJournaliereSearchRepository.exists(recetteJournaliere.getId());
        assertThat(recetteJournaliereExistsInEs).isFalse();

        // Validate the database is empty
        List<RecetteJournaliere> recetteJournalieres = recetteJournaliereRepository.findAll();
        assertThat(recetteJournalieres).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRecetteJournaliere() throws Exception {
        // Initialize the database
        recetteJournaliereRepository.saveAndFlush(recetteJournaliere);
        recetteJournaliereSearchRepository.save(recetteJournaliere);

        // Search the recetteJournaliere
        restRecetteJournaliereMockMvc.perform(get("/api/_search/recette-journalieres?query=id:" + recetteJournaliere.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recetteJournaliere.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())));
    }
}
