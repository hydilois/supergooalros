package cm.elsha.supergooalros.web.rest;

import cm.elsha.supergooalros.SupergooalrosApp;
import cm.elsha.supergooalros.domain.Employe;
import cm.elsha.supergooalros.repository.EmployeRepository;
import cm.elsha.supergooalros.repository.search.EmployeSearchRepository;

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
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cm.elsha.supergooalros.domain.enumeration.Sexe;
import cm.elsha.supergooalros.domain.enumeration.Fonction;

/**
 * Test class for the EmployeResource REST controller.
 *
 * @see EmployeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SupergooalrosApp.class)
@WebAppConfiguration
@IntegrationTest
public class EmployeResourceIntTest {

    private static final String DEFAULT_NOM = "AAAAA";
    private static final String UPDATED_NOM = "BBBBB";
    private static final String DEFAULT_PRENOM = "AAAAA";
    private static final String UPDATED_PRENOM = "BBBBB";

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final Sexe DEFAULT_SEXE = Sexe.MASCULIN;
    private static final Sexe UPDATED_SEXE = Sexe.FEMININ;

    private static final Fonction DEFAULT_FONCTION = Fonction.MANAGER;
    private static final Fonction UPDATED_FONCTION = Fonction.CAISSE;

    private static final Double DEFAULT_SALAIRE_BASE = 1D;
    private static final Double UPDATED_SALAIRE_BASE = 2D;

    @Inject
    private EmployeRepository employeRepository;

    @Inject
    private EmployeSearchRepository employeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restEmployeMockMvc;

    private Employe employe;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EmployeResource employeResource = new EmployeResource();
        ReflectionTestUtils.setField(employeResource, "employeSearchRepository", employeSearchRepository);
        ReflectionTestUtils.setField(employeResource, "employeRepository", employeRepository);
        this.restEmployeMockMvc = MockMvcBuilders.standaloneSetup(employeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        employeSearchRepository.deleteAll();
        employe = new Employe();
        employe.setNom(DEFAULT_NOM);
        employe.setPrenom(DEFAULT_PRENOM);
        employe.setPhoto(DEFAULT_PHOTO);
        employe.setPhotoContentType(DEFAULT_PHOTO_CONTENT_TYPE);
        employe.setSexe(DEFAULT_SEXE);
        employe.setFonction(DEFAULT_FONCTION);
        employe.setSalaireBase(DEFAULT_SALAIRE_BASE);
    }

    @Test
    @Transactional
    public void createEmploye() throws Exception {
        int databaseSizeBeforeCreate = employeRepository.findAll().size();

        // Create the Employe

        restEmployeMockMvc.perform(post("/api/employes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employe)))
                .andExpect(status().isCreated());

        // Validate the Employe in the database
        List<Employe> employes = employeRepository.findAll();
        assertThat(employes).hasSize(databaseSizeBeforeCreate + 1);
        Employe testEmploye = employes.get(employes.size() - 1);
        assertThat(testEmploye.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testEmploye.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testEmploye.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testEmploye.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testEmploye.getSexe()).isEqualTo(DEFAULT_SEXE);
        assertThat(testEmploye.getFonction()).isEqualTo(DEFAULT_FONCTION);
        assertThat(testEmploye.getSalaireBase()).isEqualTo(DEFAULT_SALAIRE_BASE);

        // Validate the Employe in ElasticSearch
        Employe employeEs = employeSearchRepository.findOne(testEmploye.getId());
        assertThat(employeEs).isEqualToComparingFieldByField(testEmploye);
    }

    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeRepository.findAll().size();
        // set the field null
        employe.setNom(null);

        // Create the Employe, which fails.

        restEmployeMockMvc.perform(post("/api/employes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employe)))
                .andExpect(status().isBadRequest());

        List<Employe> employes = employeRepository.findAll();
        assertThat(employes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSexeIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeRepository.findAll().size();
        // set the field null
        employe.setSexe(null);

        // Create the Employe, which fails.

        restEmployeMockMvc.perform(post("/api/employes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(employe)))
                .andExpect(status().isBadRequest());

        List<Employe> employes = employeRepository.findAll();
        assertThat(employes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEmployes() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get all the employes
        restEmployeMockMvc.perform(get("/api/employes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(employe.getId().intValue())))
                .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
                .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM.toString())))
                .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
                .andExpect(jsonPath("$.[*].sexe").value(hasItem(DEFAULT_SEXE.toString())))
                .andExpect(jsonPath("$.[*].fonction").value(hasItem(DEFAULT_FONCTION.toString())))
                .andExpect(jsonPath("$.[*].salaireBase").value(hasItem(DEFAULT_SALAIRE_BASE.doubleValue())));
    }

    @Test
    @Transactional
    public void getEmploye() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);

        // Get the employe
        restEmployeMockMvc.perform(get("/api/employes/{id}", employe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(employe.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM.toString()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.sexe").value(DEFAULT_SEXE.toString()))
            .andExpect(jsonPath("$.fonction").value(DEFAULT_FONCTION.toString()))
            .andExpect(jsonPath("$.salaireBase").value(DEFAULT_SALAIRE_BASE.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingEmploye() throws Exception {
        // Get the employe
        restEmployeMockMvc.perform(get("/api/employes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEmploye() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);
        employeSearchRepository.save(employe);
        int databaseSizeBeforeUpdate = employeRepository.findAll().size();

        // Update the employe
        Employe updatedEmploye = new Employe();
        updatedEmploye.setId(employe.getId());
        updatedEmploye.setNom(UPDATED_NOM);
        updatedEmploye.setPrenom(UPDATED_PRENOM);
        updatedEmploye.setPhoto(UPDATED_PHOTO);
        updatedEmploye.setPhotoContentType(UPDATED_PHOTO_CONTENT_TYPE);
        updatedEmploye.setSexe(UPDATED_SEXE);
        updatedEmploye.setFonction(UPDATED_FONCTION);
        updatedEmploye.setSalaireBase(UPDATED_SALAIRE_BASE);

        restEmployeMockMvc.perform(put("/api/employes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedEmploye)))
                .andExpect(status().isOk());

        // Validate the Employe in the database
        List<Employe> employes = employeRepository.findAll();
        assertThat(employes).hasSize(databaseSizeBeforeUpdate);
        Employe testEmploye = employes.get(employes.size() - 1);
        assertThat(testEmploye.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEmploye.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testEmploye.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testEmploye.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testEmploye.getSexe()).isEqualTo(UPDATED_SEXE);
        assertThat(testEmploye.getFonction()).isEqualTo(UPDATED_FONCTION);
        assertThat(testEmploye.getSalaireBase()).isEqualTo(UPDATED_SALAIRE_BASE);

        // Validate the Employe in ElasticSearch
        Employe employeEs = employeSearchRepository.findOne(testEmploye.getId());
        assertThat(employeEs).isEqualToComparingFieldByField(testEmploye);
    }

    @Test
    @Transactional
    public void deleteEmploye() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);
        employeSearchRepository.save(employe);
        int databaseSizeBeforeDelete = employeRepository.findAll().size();

        // Get the employe
        restEmployeMockMvc.perform(delete("/api/employes/{id}", employe.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean employeExistsInEs = employeSearchRepository.exists(employe.getId());
        assertThat(employeExistsInEs).isFalse();

        // Validate the database is empty
        List<Employe> employes = employeRepository.findAll();
        assertThat(employes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchEmploye() throws Exception {
        // Initialize the database
        employeRepository.saveAndFlush(employe);
        employeSearchRepository.save(employe);

        // Search the employe
        restEmployeMockMvc.perform(get("/api/_search/employes?query=id:" + employe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employe.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM.toString())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].sexe").value(hasItem(DEFAULT_SEXE.toString())))
            .andExpect(jsonPath("$.[*].fonction").value(hasItem(DEFAULT_FONCTION.toString())))
            .andExpect(jsonPath("$.[*].salaireBase").value(hasItem(DEFAULT_SALAIRE_BASE.doubleValue())));
    }
}
