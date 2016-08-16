package cm.elsha.supergooalros.web.rest;

import cm.elsha.supergooalros.SupergooalrosApp;
import cm.elsha.supergooalros.domain.QuotaHebdo;
import cm.elsha.supergooalros.repository.QuotaHebdoRepository;
import cm.elsha.supergooalros.repository.search.QuotaHebdoSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the QuotaHebdoResource REST controller.
 *
 * @see QuotaHebdoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SupergooalrosApp.class)
@WebAppConfiguration
@IntegrationTest
public class QuotaHebdoResourceIntTest {


    private static final Double DEFAULT_MONTANT = 1D;
    private static final Double UPDATED_MONTANT = 2D;

    private static final Double DEFAULT_PRIME_HEBDO = 1D;
    private static final Double UPDATED_PRIME_HEBDO = 2D;

    @Inject
    private QuotaHebdoRepository quotaHebdoRepository;

    @Inject
    private QuotaHebdoSearchRepository quotaHebdoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restQuotaHebdoMockMvc;

    private QuotaHebdo quotaHebdo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        QuotaHebdoResource quotaHebdoResource = new QuotaHebdoResource();
        ReflectionTestUtils.setField(quotaHebdoResource, "quotaHebdoSearchRepository", quotaHebdoSearchRepository);
        ReflectionTestUtils.setField(quotaHebdoResource, "quotaHebdoRepository", quotaHebdoRepository);
        this.restQuotaHebdoMockMvc = MockMvcBuilders.standaloneSetup(quotaHebdoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        quotaHebdoSearchRepository.deleteAll();
        quotaHebdo = new QuotaHebdo();
        quotaHebdo.setMontant(DEFAULT_MONTANT);
        quotaHebdo.setPrimeHebdo(DEFAULT_PRIME_HEBDO);
    }

    @Test
    @Transactional
    public void createQuotaHebdo() throws Exception {
        int databaseSizeBeforeCreate = quotaHebdoRepository.findAll().size();

        // Create the QuotaHebdo

        restQuotaHebdoMockMvc.perform(post("/api/quota-hebdos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(quotaHebdo)))
                .andExpect(status().isCreated());

        // Validate the QuotaHebdo in the database
        List<QuotaHebdo> quotaHebdos = quotaHebdoRepository.findAll();
        assertThat(quotaHebdos).hasSize(databaseSizeBeforeCreate + 1);
        QuotaHebdo testQuotaHebdo = quotaHebdos.get(quotaHebdos.size() - 1);
        assertThat(testQuotaHebdo.getMontant()).isEqualTo(DEFAULT_MONTANT);
        assertThat(testQuotaHebdo.getPrimeHebdo()).isEqualTo(DEFAULT_PRIME_HEBDO);

        // Validate the QuotaHebdo in ElasticSearch
        QuotaHebdo quotaHebdoEs = quotaHebdoSearchRepository.findOne(testQuotaHebdo.getId());
        assertThat(quotaHebdoEs).isEqualToComparingFieldByField(testQuotaHebdo);
    }

    @Test
    @Transactional
    public void checkMontantIsRequired() throws Exception {
        int databaseSizeBeforeTest = quotaHebdoRepository.findAll().size();
        // set the field null
        quotaHebdo.setMontant(null);

        // Create the QuotaHebdo, which fails.

        restQuotaHebdoMockMvc.perform(post("/api/quota-hebdos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(quotaHebdo)))
                .andExpect(status().isBadRequest());

        List<QuotaHebdo> quotaHebdos = quotaHebdoRepository.findAll();
        assertThat(quotaHebdos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllQuotaHebdos() throws Exception {
        // Initialize the database
        quotaHebdoRepository.saveAndFlush(quotaHebdo);

        // Get all the quotaHebdos
        restQuotaHebdoMockMvc.perform(get("/api/quota-hebdos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(quotaHebdo.getId().intValue())))
                .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())))
                .andExpect(jsonPath("$.[*].primeHebdo").value(hasItem(DEFAULT_PRIME_HEBDO.doubleValue())));
    }

    @Test
    @Transactional
    public void getQuotaHebdo() throws Exception {
        // Initialize the database
        quotaHebdoRepository.saveAndFlush(quotaHebdo);

        // Get the quotaHebdo
        restQuotaHebdoMockMvc.perform(get("/api/quota-hebdos/{id}", quotaHebdo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(quotaHebdo.getId().intValue()))
            .andExpect(jsonPath("$.montant").value(DEFAULT_MONTANT.doubleValue()))
            .andExpect(jsonPath("$.primeHebdo").value(DEFAULT_PRIME_HEBDO.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingQuotaHebdo() throws Exception {
        // Get the quotaHebdo
        restQuotaHebdoMockMvc.perform(get("/api/quota-hebdos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuotaHebdo() throws Exception {
        // Initialize the database
        quotaHebdoRepository.saveAndFlush(quotaHebdo);
        quotaHebdoSearchRepository.save(quotaHebdo);
        int databaseSizeBeforeUpdate = quotaHebdoRepository.findAll().size();

        // Update the quotaHebdo
        QuotaHebdo updatedQuotaHebdo = new QuotaHebdo();
        updatedQuotaHebdo.setId(quotaHebdo.getId());
        updatedQuotaHebdo.setMontant(UPDATED_MONTANT);
        updatedQuotaHebdo.setPrimeHebdo(UPDATED_PRIME_HEBDO);

        restQuotaHebdoMockMvc.perform(put("/api/quota-hebdos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedQuotaHebdo)))
                .andExpect(status().isOk());

        // Validate the QuotaHebdo in the database
        List<QuotaHebdo> quotaHebdos = quotaHebdoRepository.findAll();
        assertThat(quotaHebdos).hasSize(databaseSizeBeforeUpdate);
        QuotaHebdo testQuotaHebdo = quotaHebdos.get(quotaHebdos.size() - 1);
        assertThat(testQuotaHebdo.getMontant()).isEqualTo(UPDATED_MONTANT);
        assertThat(testQuotaHebdo.getPrimeHebdo()).isEqualTo(UPDATED_PRIME_HEBDO);

        // Validate the QuotaHebdo in ElasticSearch
        QuotaHebdo quotaHebdoEs = quotaHebdoSearchRepository.findOne(testQuotaHebdo.getId());
        assertThat(quotaHebdoEs).isEqualToComparingFieldByField(testQuotaHebdo);
    }

    @Test
    @Transactional
    public void deleteQuotaHebdo() throws Exception {
        // Initialize the database
        quotaHebdoRepository.saveAndFlush(quotaHebdo);
        quotaHebdoSearchRepository.save(quotaHebdo);
        int databaseSizeBeforeDelete = quotaHebdoRepository.findAll().size();

        // Get the quotaHebdo
        restQuotaHebdoMockMvc.perform(delete("/api/quota-hebdos/{id}", quotaHebdo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean quotaHebdoExistsInEs = quotaHebdoSearchRepository.exists(quotaHebdo.getId());
        assertThat(quotaHebdoExistsInEs).isFalse();

        // Validate the database is empty
        List<QuotaHebdo> quotaHebdos = quotaHebdoRepository.findAll();
        assertThat(quotaHebdos).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchQuotaHebdo() throws Exception {
        // Initialize the database
        quotaHebdoRepository.saveAndFlush(quotaHebdo);
        quotaHebdoSearchRepository.save(quotaHebdo);

        // Search the quotaHebdo
        restQuotaHebdoMockMvc.perform(get("/api/_search/quota-hebdos?query=id:" + quotaHebdo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quotaHebdo.getId().intValue())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())))
            .andExpect(jsonPath("$.[*].primeHebdo").value(hasItem(DEFAULT_PRIME_HEBDO.doubleValue())));
    }
}
