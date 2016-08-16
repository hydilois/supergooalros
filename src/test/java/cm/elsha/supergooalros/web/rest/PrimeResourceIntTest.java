package cm.elsha.supergooalros.web.rest;

import cm.elsha.supergooalros.SupergooalrosApp;
import cm.elsha.supergooalros.domain.Prime;
import cm.elsha.supergooalros.repository.PrimeRepository;
import cm.elsha.supergooalros.repository.search.PrimeSearchRepository;

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
 * Test class for the PrimeResource REST controller.
 *
 * @see PrimeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SupergooalrosApp.class)
@WebAppConfiguration
@IntegrationTest
public class PrimeResourceIntTest {


    private static final Double DEFAULT_MONTANT = 1D;
    private static final Double UPDATED_MONTANT = 2D;

    private static final LocalDate DEFAULT_DATE_FIXATION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FIXATION = LocalDate.now(ZoneId.systemDefault());
    private static final String DEFAULT_RAISON = "AAAAA";
    private static final String UPDATED_RAISON = "BBBBB";

    @Inject
    private PrimeRepository primeRepository;

    @Inject
    private PrimeSearchRepository primeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPrimeMockMvc;

    private Prime prime;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PrimeResource primeResource = new PrimeResource();
        ReflectionTestUtils.setField(primeResource, "primeSearchRepository", primeSearchRepository);
        ReflectionTestUtils.setField(primeResource, "primeRepository", primeRepository);
        this.restPrimeMockMvc = MockMvcBuilders.standaloneSetup(primeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        primeSearchRepository.deleteAll();
        prime = new Prime();
        prime.setMontant(DEFAULT_MONTANT);
        prime.setDateFixation(DEFAULT_DATE_FIXATION);
        prime.setRaison(DEFAULT_RAISON);
    }

    @Test
    @Transactional
    public void createPrime() throws Exception {
        int databaseSizeBeforeCreate = primeRepository.findAll().size();

        // Create the Prime

        restPrimeMockMvc.perform(post("/api/primes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(prime)))
                .andExpect(status().isCreated());

        // Validate the Prime in the database
        List<Prime> primes = primeRepository.findAll();
        assertThat(primes).hasSize(databaseSizeBeforeCreate + 1);
        Prime testPrime = primes.get(primes.size() - 1);
        assertThat(testPrime.getMontant()).isEqualTo(DEFAULT_MONTANT);
        assertThat(testPrime.getDateFixation()).isEqualTo(DEFAULT_DATE_FIXATION);
        assertThat(testPrime.getRaison()).isEqualTo(DEFAULT_RAISON);

        // Validate the Prime in ElasticSearch
        Prime primeEs = primeSearchRepository.findOne(testPrime.getId());
        assertThat(primeEs).isEqualToComparingFieldByField(testPrime);
    }

    @Test
    @Transactional
    public void checkMontantIsRequired() throws Exception {
        int databaseSizeBeforeTest = primeRepository.findAll().size();
        // set the field null
        prime.setMontant(null);

        // Create the Prime, which fails.

        restPrimeMockMvc.perform(post("/api/primes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(prime)))
                .andExpect(status().isBadRequest());

        List<Prime> primes = primeRepository.findAll();
        assertThat(primes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPrimes() throws Exception {
        // Initialize the database
        primeRepository.saveAndFlush(prime);

        // Get all the primes
        restPrimeMockMvc.perform(get("/api/primes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(prime.getId().intValue())))
                .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())))
                .andExpect(jsonPath("$.[*].dateFixation").value(hasItem(DEFAULT_DATE_FIXATION.toString())))
                .andExpect(jsonPath("$.[*].raison").value(hasItem(DEFAULT_RAISON.toString())));
    }

    @Test
    @Transactional
    public void getPrime() throws Exception {
        // Initialize the database
        primeRepository.saveAndFlush(prime);

        // Get the prime
        restPrimeMockMvc.perform(get("/api/primes/{id}", prime.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(prime.getId().intValue()))
            .andExpect(jsonPath("$.montant").value(DEFAULT_MONTANT.doubleValue()))
            .andExpect(jsonPath("$.dateFixation").value(DEFAULT_DATE_FIXATION.toString()))
            .andExpect(jsonPath("$.raison").value(DEFAULT_RAISON.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPrime() throws Exception {
        // Get the prime
        restPrimeMockMvc.perform(get("/api/primes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePrime() throws Exception {
        // Initialize the database
        primeRepository.saveAndFlush(prime);
        primeSearchRepository.save(prime);
        int databaseSizeBeforeUpdate = primeRepository.findAll().size();

        // Update the prime
        Prime updatedPrime = new Prime();
        updatedPrime.setId(prime.getId());
        updatedPrime.setMontant(UPDATED_MONTANT);
        updatedPrime.setDateFixation(UPDATED_DATE_FIXATION);
        updatedPrime.setRaison(UPDATED_RAISON);

        restPrimeMockMvc.perform(put("/api/primes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPrime)))
                .andExpect(status().isOk());

        // Validate the Prime in the database
        List<Prime> primes = primeRepository.findAll();
        assertThat(primes).hasSize(databaseSizeBeforeUpdate);
        Prime testPrime = primes.get(primes.size() - 1);
        assertThat(testPrime.getMontant()).isEqualTo(UPDATED_MONTANT);
        assertThat(testPrime.getDateFixation()).isEqualTo(UPDATED_DATE_FIXATION);
        assertThat(testPrime.getRaison()).isEqualTo(UPDATED_RAISON);

        // Validate the Prime in ElasticSearch
        Prime primeEs = primeSearchRepository.findOne(testPrime.getId());
        assertThat(primeEs).isEqualToComparingFieldByField(testPrime);
    }

    @Test
    @Transactional
    public void deletePrime() throws Exception {
        // Initialize the database
        primeRepository.saveAndFlush(prime);
        primeSearchRepository.save(prime);
        int databaseSizeBeforeDelete = primeRepository.findAll().size();

        // Get the prime
        restPrimeMockMvc.perform(delete("/api/primes/{id}", prime.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean primeExistsInEs = primeSearchRepository.exists(prime.getId());
        assertThat(primeExistsInEs).isFalse();

        // Validate the database is empty
        List<Prime> primes = primeRepository.findAll();
        assertThat(primes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPrime() throws Exception {
        // Initialize the database
        primeRepository.saveAndFlush(prime);
        primeSearchRepository.save(prime);

        // Search the prime
        restPrimeMockMvc.perform(get("/api/_search/primes?query=id:" + prime.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prime.getId().intValue())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())))
            .andExpect(jsonPath("$.[*].dateFixation").value(hasItem(DEFAULT_DATE_FIXATION.toString())))
            .andExpect(jsonPath("$.[*].raison").value(hasItem(DEFAULT_RAISON.toString())));
    }
}
