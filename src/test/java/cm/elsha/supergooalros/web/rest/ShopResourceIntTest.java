package cm.elsha.supergooalros.web.rest;

import cm.elsha.supergooalros.SupergooalrosApp;
import cm.elsha.supergooalros.domain.Shop;
import cm.elsha.supergooalros.repository.ShopRepository;
import cm.elsha.supergooalros.repository.search.ShopSearchRepository;

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
 * Test class for the ShopResource REST controller.
 *
 * @see ShopResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SupergooalrosApp.class)
@WebAppConfiguration
@IntegrationTest
public class ShopResourceIntTest {

    private static final String DEFAULT_NOM = "AAAAA";
    private static final String UPDATED_NOM = "BBBBB";
    private static final String DEFAULT_QUARTIER = "AAAAA";
    private static final String UPDATED_QUARTIER = "BBBBB";

    @Inject
    private ShopRepository shopRepository;

    @Inject
    private ShopSearchRepository shopSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restShopMockMvc;

    private Shop shop;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ShopResource shopResource = new ShopResource();
        ReflectionTestUtils.setField(shopResource, "shopSearchRepository", shopSearchRepository);
        ReflectionTestUtils.setField(shopResource, "shopRepository", shopRepository);
        this.restShopMockMvc = MockMvcBuilders.standaloneSetup(shopResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        shopSearchRepository.deleteAll();
        shop = new Shop();
        shop.setNom(DEFAULT_NOM);
        shop.setQuartier(DEFAULT_QUARTIER);
    }

    @Test
    @Transactional
    public void createShop() throws Exception {
        int databaseSizeBeforeCreate = shopRepository.findAll().size();

        // Create the Shop

        restShopMockMvc.perform(post("/api/shops")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(shop)))
                .andExpect(status().isCreated());

        // Validate the Shop in the database
        List<Shop> shops = shopRepository.findAll();
        assertThat(shops).hasSize(databaseSizeBeforeCreate + 1);
        Shop testShop = shops.get(shops.size() - 1);
        assertThat(testShop.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testShop.getQuartier()).isEqualTo(DEFAULT_QUARTIER);

        // Validate the Shop in ElasticSearch
        Shop shopEs = shopSearchRepository.findOne(testShop.getId());
        assertThat(shopEs).isEqualToComparingFieldByField(testShop);
    }

    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = shopRepository.findAll().size();
        // set the field null
        shop.setNom(null);

        // Create the Shop, which fails.

        restShopMockMvc.perform(post("/api/shops")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(shop)))
                .andExpect(status().isBadRequest());

        List<Shop> shops = shopRepository.findAll();
        assertThat(shops).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllShops() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        // Get all the shops
        restShopMockMvc.perform(get("/api/shops?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(shop.getId().intValue())))
                .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
                .andExpect(jsonPath("$.[*].quartier").value(hasItem(DEFAULT_QUARTIER.toString())));
    }

    @Test
    @Transactional
    public void getShop() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        // Get the shop
        restShopMockMvc.perform(get("/api/shops/{id}", shop.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(shop.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.quartier").value(DEFAULT_QUARTIER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingShop() throws Exception {
        // Get the shop
        restShopMockMvc.perform(get("/api/shops/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateShop() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);
        shopSearchRepository.save(shop);
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();

        // Update the shop
        Shop updatedShop = new Shop();
        updatedShop.setId(shop.getId());
        updatedShop.setNom(UPDATED_NOM);
        updatedShop.setQuartier(UPDATED_QUARTIER);

        restShopMockMvc.perform(put("/api/shops")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedShop)))
                .andExpect(status().isOk());

        // Validate the Shop in the database
        List<Shop> shops = shopRepository.findAll();
        assertThat(shops).hasSize(databaseSizeBeforeUpdate);
        Shop testShop = shops.get(shops.size() - 1);
        assertThat(testShop.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testShop.getQuartier()).isEqualTo(UPDATED_QUARTIER);

        // Validate the Shop in ElasticSearch
        Shop shopEs = shopSearchRepository.findOne(testShop.getId());
        assertThat(shopEs).isEqualToComparingFieldByField(testShop);
    }

    @Test
    @Transactional
    public void deleteShop() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);
        shopSearchRepository.save(shop);
        int databaseSizeBeforeDelete = shopRepository.findAll().size();

        // Get the shop
        restShopMockMvc.perform(delete("/api/shops/{id}", shop.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean shopExistsInEs = shopSearchRepository.exists(shop.getId());
        assertThat(shopExistsInEs).isFalse();

        // Validate the database is empty
        List<Shop> shops = shopRepository.findAll();
        assertThat(shops).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchShop() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);
        shopSearchRepository.save(shop);

        // Search the shop
        restShopMockMvc.perform(get("/api/_search/shops?query=id:" + shop.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shop.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].quartier").value(hasItem(DEFAULT_QUARTIER.toString())));
    }
}
