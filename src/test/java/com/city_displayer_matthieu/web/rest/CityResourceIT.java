package com.city_displayer_matthieu.web.rest;

import com.city_displayer_matthieu.CityDisplayerApp;
import com.city_displayer_matthieu.domain.City;
import com.city_displayer_matthieu.domain.User;
import com.city_displayer_matthieu.repository.CityRepository;
import com.city_displayer_matthieu.service.CityService;
import com.city_displayer_matthieu.web.rest.errors.ExceptionTranslator;
import com.city_displayer_matthieu.service.dto.CityCriteria;
import com.city_displayer_matthieu.service.CityQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.city_displayer_matthieu.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CityResource} REST controller.
 */
@SpringBootTest(classes = CityDisplayerApp.class)
public class CityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_INHABITANTS = 1L;
    private static final Long UPDATED_INHABITANTS = 2L;
    private static final Long SMALLER_INHABITANTS = 1L - 1L;

    private static final Integer DEFAULT_POSTAL_CODE = 1;
    private static final Integer UPDATED_POSTAL_CODE = 2;
    private static final Integer SMALLER_POSTAL_CODE = 1 - 1;

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATED_DATE = LocalDate.ofEpochDay(-1L);

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CityService cityService;

    @Autowired
    private CityQueryService cityQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restCityMockMvc;

    private City city;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CityResource cityResource = new CityResource(cityService, cityQueryService);
        this.restCityMockMvc = MockMvcBuilders.standaloneSetup(cityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static City createEntity(EntityManager em) {
        City city = new City()
            .name(DEFAULT_NAME)
            .inhabitants(DEFAULT_INHABITANTS)
            .postal_code(DEFAULT_POSTAL_CODE)
            .created_date(DEFAULT_CREATED_DATE);
        return city;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static City createUpdatedEntity(EntityManager em) {
        City city = new City()
            .name(UPDATED_NAME)
            .inhabitants(UPDATED_INHABITANTS)
            .postal_code(UPDATED_POSTAL_CODE)
            .created_date(UPDATED_CREATED_DATE);
        return city;
    }

    @BeforeEach
    public void initTest() {
        city = createEntity(em);
    }

    @Test
    @Transactional
    public void createCity() throws Exception {
        int databaseSizeBeforeCreate = cityRepository.findAll().size();

        // Create the City
        restCityMockMvc.perform(post("/api/cities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(city)))
            .andExpect(status().isCreated());

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeCreate + 1);
        City testCity = cityList.get(cityList.size() - 1);
        assertThat(testCity.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCity.getInhabitants()).isEqualTo(DEFAULT_INHABITANTS);
        assertThat(testCity.getPostal_code()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testCity.getCreated_date()).isEqualTo(DEFAULT_CREATED_DATE);
    }

    @Test
    @Transactional
    public void createCityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cityRepository.findAll().size();

        // Create the City with an existing ID
        city.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCityMockMvc.perform(post("/api/cities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(city)))
            .andExpect(status().isBadRequest());

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllCities() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList
        restCityMockMvc.perform(get("/api/cities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(city.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].inhabitants").value(hasItem(DEFAULT_INHABITANTS.intValue())))
            .andExpect(jsonPath("$.[*].postal_code").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].created_date").value(hasItem(DEFAULT_CREATED_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getCity() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get the city
        restCityMockMvc.perform(get("/api/cities/{id}", city.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(city.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.inhabitants").value(DEFAULT_INHABITANTS.intValue()))
            .andExpect(jsonPath("$.postal_code").value(DEFAULT_POSTAL_CODE))
            .andExpect(jsonPath("$.created_date").value(DEFAULT_CREATED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getAllCitiesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where name equals to DEFAULT_NAME
        defaultCityShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the cityList where name equals to UPDATED_NAME
        defaultCityShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCitiesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where name not equals to DEFAULT_NAME
        defaultCityShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the cityList where name not equals to UPDATED_NAME
        defaultCityShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCitiesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCityShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the cityList where name equals to UPDATED_NAME
        defaultCityShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCitiesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where name is not null
        defaultCityShouldBeFound("name.specified=true");

        // Get all the cityList where name is null
        defaultCityShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllCitiesByNameContainsSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where name contains DEFAULT_NAME
        defaultCityShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the cityList where name contains UPDATED_NAME
        defaultCityShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCitiesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where name does not contain DEFAULT_NAME
        defaultCityShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the cityList where name does not contain UPDATED_NAME
        defaultCityShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllCitiesByInhabitantsIsEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where inhabitants equals to DEFAULT_INHABITANTS
        defaultCityShouldBeFound("inhabitants.equals=" + DEFAULT_INHABITANTS);

        // Get all the cityList where inhabitants equals to UPDATED_INHABITANTS
        defaultCityShouldNotBeFound("inhabitants.equals=" + UPDATED_INHABITANTS);
    }

    @Test
    @Transactional
    public void getAllCitiesByInhabitantsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where inhabitants not equals to DEFAULT_INHABITANTS
        defaultCityShouldNotBeFound("inhabitants.notEquals=" + DEFAULT_INHABITANTS);

        // Get all the cityList where inhabitants not equals to UPDATED_INHABITANTS
        defaultCityShouldBeFound("inhabitants.notEquals=" + UPDATED_INHABITANTS);
    }

    @Test
    @Transactional
    public void getAllCitiesByInhabitantsIsInShouldWork() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where inhabitants in DEFAULT_INHABITANTS or UPDATED_INHABITANTS
        defaultCityShouldBeFound("inhabitants.in=" + DEFAULT_INHABITANTS + "," + UPDATED_INHABITANTS);

        // Get all the cityList where inhabitants equals to UPDATED_INHABITANTS
        defaultCityShouldNotBeFound("inhabitants.in=" + UPDATED_INHABITANTS);
    }

    @Test
    @Transactional
    public void getAllCitiesByInhabitantsIsNullOrNotNull() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where inhabitants is not null
        defaultCityShouldBeFound("inhabitants.specified=true");

        // Get all the cityList where inhabitants is null
        defaultCityShouldNotBeFound("inhabitants.specified=false");
    }

    @Test
    @Transactional
    public void getAllCitiesByInhabitantsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where inhabitants is greater than or equal to DEFAULT_INHABITANTS
        defaultCityShouldBeFound("inhabitants.greaterThanOrEqual=" + DEFAULT_INHABITANTS);

        // Get all the cityList where inhabitants is greater than or equal to UPDATED_INHABITANTS
        defaultCityShouldNotBeFound("inhabitants.greaterThanOrEqual=" + UPDATED_INHABITANTS);
    }

    @Test
    @Transactional
    public void getAllCitiesByInhabitantsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where inhabitants is less than or equal to DEFAULT_INHABITANTS
        defaultCityShouldBeFound("inhabitants.lessThanOrEqual=" + DEFAULT_INHABITANTS);

        // Get all the cityList where inhabitants is less than or equal to SMALLER_INHABITANTS
        defaultCityShouldNotBeFound("inhabitants.lessThanOrEqual=" + SMALLER_INHABITANTS);
    }

    @Test
    @Transactional
    public void getAllCitiesByInhabitantsIsLessThanSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where inhabitants is less than DEFAULT_INHABITANTS
        defaultCityShouldNotBeFound("inhabitants.lessThan=" + DEFAULT_INHABITANTS);

        // Get all the cityList where inhabitants is less than UPDATED_INHABITANTS
        defaultCityShouldBeFound("inhabitants.lessThan=" + UPDATED_INHABITANTS);
    }

    @Test
    @Transactional
    public void getAllCitiesByInhabitantsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where inhabitants is greater than DEFAULT_INHABITANTS
        defaultCityShouldNotBeFound("inhabitants.greaterThan=" + DEFAULT_INHABITANTS);

        // Get all the cityList where inhabitants is greater than SMALLER_INHABITANTS
        defaultCityShouldBeFound("inhabitants.greaterThan=" + SMALLER_INHABITANTS);
    }


    @Test
    @Transactional
    public void getAllCitiesByPostal_codeIsEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where postal_code equals to DEFAULT_POSTAL_CODE
        defaultCityShouldBeFound("postal_code.equals=" + DEFAULT_POSTAL_CODE);

        // Get all the cityList where postal_code equals to UPDATED_POSTAL_CODE
        defaultCityShouldNotBeFound("postal_code.equals=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    public void getAllCitiesByPostal_codeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where postal_code not equals to DEFAULT_POSTAL_CODE
        defaultCityShouldNotBeFound("postal_code.notEquals=" + DEFAULT_POSTAL_CODE);

        // Get all the cityList where postal_code not equals to UPDATED_POSTAL_CODE
        defaultCityShouldBeFound("postal_code.notEquals=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    public void getAllCitiesByPostal_codeIsInShouldWork() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where postal_code in DEFAULT_POSTAL_CODE or UPDATED_POSTAL_CODE
        defaultCityShouldBeFound("postal_code.in=" + DEFAULT_POSTAL_CODE + "," + UPDATED_POSTAL_CODE);

        // Get all the cityList where postal_code equals to UPDATED_POSTAL_CODE
        defaultCityShouldNotBeFound("postal_code.in=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    public void getAllCitiesByPostal_codeIsNullOrNotNull() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where postal_code is not null
        defaultCityShouldBeFound("postal_code.specified=true");

        // Get all the cityList where postal_code is null
        defaultCityShouldNotBeFound("postal_code.specified=false");
    }

    @Test
    @Transactional
    public void getAllCitiesByPostal_codeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where postal_code is greater than or equal to DEFAULT_POSTAL_CODE
        defaultCityShouldBeFound("postal_code.greaterThanOrEqual=" + DEFAULT_POSTAL_CODE);

        // Get all the cityList where postal_code is greater than or equal to UPDATED_POSTAL_CODE
        defaultCityShouldNotBeFound("postal_code.greaterThanOrEqual=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    public void getAllCitiesByPostal_codeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where postal_code is less than or equal to DEFAULT_POSTAL_CODE
        defaultCityShouldBeFound("postal_code.lessThanOrEqual=" + DEFAULT_POSTAL_CODE);

        // Get all the cityList where postal_code is less than or equal to SMALLER_POSTAL_CODE
        defaultCityShouldNotBeFound("postal_code.lessThanOrEqual=" + SMALLER_POSTAL_CODE);
    }

    @Test
    @Transactional
    public void getAllCitiesByPostal_codeIsLessThanSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where postal_code is less than DEFAULT_POSTAL_CODE
        defaultCityShouldNotBeFound("postal_code.lessThan=" + DEFAULT_POSTAL_CODE);

        // Get all the cityList where postal_code is less than UPDATED_POSTAL_CODE
        defaultCityShouldBeFound("postal_code.lessThan=" + UPDATED_POSTAL_CODE);
    }

    @Test
    @Transactional
    public void getAllCitiesByPostal_codeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where postal_code is greater than DEFAULT_POSTAL_CODE
        defaultCityShouldNotBeFound("postal_code.greaterThan=" + DEFAULT_POSTAL_CODE);

        // Get all the cityList where postal_code is greater than SMALLER_POSTAL_CODE
        defaultCityShouldBeFound("postal_code.greaterThan=" + SMALLER_POSTAL_CODE);
    }


    @Test
    @Transactional
    public void getAllCitiesByCreated_dateIsEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where created_date equals to DEFAULT_CREATED_DATE
        defaultCityShouldBeFound("created_date.equals=" + DEFAULT_CREATED_DATE);

        // Get all the cityList where created_date equals to UPDATED_CREATED_DATE
        defaultCityShouldNotBeFound("created_date.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllCitiesByCreated_dateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where created_date not equals to DEFAULT_CREATED_DATE
        defaultCityShouldNotBeFound("created_date.notEquals=" + DEFAULT_CREATED_DATE);

        // Get all the cityList where created_date not equals to UPDATED_CREATED_DATE
        defaultCityShouldBeFound("created_date.notEquals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllCitiesByCreated_dateIsInShouldWork() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where created_date in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultCityShouldBeFound("created_date.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the cityList where created_date equals to UPDATED_CREATED_DATE
        defaultCityShouldNotBeFound("created_date.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllCitiesByCreated_dateIsNullOrNotNull() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where created_date is not null
        defaultCityShouldBeFound("created_date.specified=true");

        // Get all the cityList where created_date is null
        defaultCityShouldNotBeFound("created_date.specified=false");
    }

    @Test
    @Transactional
    public void getAllCitiesByCreated_dateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where created_date is greater than or equal to DEFAULT_CREATED_DATE
        defaultCityShouldBeFound("created_date.greaterThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the cityList where created_date is greater than or equal to UPDATED_CREATED_DATE
        defaultCityShouldNotBeFound("created_date.greaterThanOrEqual=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllCitiesByCreated_dateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where created_date is less than or equal to DEFAULT_CREATED_DATE
        defaultCityShouldBeFound("created_date.lessThanOrEqual=" + DEFAULT_CREATED_DATE);

        // Get all the cityList where created_date is less than or equal to SMALLER_CREATED_DATE
        defaultCityShouldNotBeFound("created_date.lessThanOrEqual=" + SMALLER_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllCitiesByCreated_dateIsLessThanSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where created_date is less than DEFAULT_CREATED_DATE
        defaultCityShouldNotBeFound("created_date.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the cityList where created_date is less than UPDATED_CREATED_DATE
        defaultCityShouldBeFound("created_date.lessThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllCitiesByCreated_dateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);

        // Get all the cityList where created_date is greater than DEFAULT_CREATED_DATE
        defaultCityShouldNotBeFound("created_date.greaterThan=" + DEFAULT_CREATED_DATE);

        // Get all the cityList where created_date is greater than SMALLER_CREATED_DATE
        defaultCityShouldBeFound("created_date.greaterThan=" + SMALLER_CREATED_DATE);
    }


    @Test
    @Transactional
    public void getAllCitiesByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        cityRepository.saveAndFlush(city);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        city.setUser(user);
        cityRepository.saveAndFlush(city);
        Long userId = user.getId();

        // Get all the cityList where user equals to userId
        defaultCityShouldBeFound("userId.equals=" + userId);

        // Get all the cityList where user equals to userId + 1
        defaultCityShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCityShouldBeFound(String filter) throws Exception {
        restCityMockMvc.perform(get("/api/cities?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(city.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].inhabitants").value(hasItem(DEFAULT_INHABITANTS.intValue())))
            .andExpect(jsonPath("$.[*].postal_code").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].created_date").value(hasItem(DEFAULT_CREATED_DATE.toString())));

        // Check, that the count call also returns 1
        restCityMockMvc.perform(get("/api/cities/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCityShouldNotBeFound(String filter) throws Exception {
        restCityMockMvc.perform(get("/api/cities?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCityMockMvc.perform(get("/api/cities/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingCity() throws Exception {
        // Get the city
        restCityMockMvc.perform(get("/api/cities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCity() throws Exception {
        // Initialize the database
        cityService.save(city);

        int databaseSizeBeforeUpdate = cityRepository.findAll().size();

        // Update the city
        City updatedCity = cityRepository.findById(city.getId()).get();
        // Disconnect from session so that the updates on updatedCity are not directly saved in db
        em.detach(updatedCity);
        updatedCity
            .name(UPDATED_NAME)
            .inhabitants(UPDATED_INHABITANTS)
            .postal_code(UPDATED_POSTAL_CODE)
            .created_date(UPDATED_CREATED_DATE);

        restCityMockMvc.perform(put("/api/cities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCity)))
            .andExpect(status().isOk());

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
        City testCity = cityList.get(cityList.size() - 1);
        assertThat(testCity.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCity.getInhabitants()).isEqualTo(UPDATED_INHABITANTS);
        assertThat(testCity.getPostal_code()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testCity.getCreated_date()).isEqualTo(UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingCity() throws Exception {
        int databaseSizeBeforeUpdate = cityRepository.findAll().size();

        // Create the City

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCityMockMvc.perform(put("/api/cities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(city)))
            .andExpect(status().isBadRequest());

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCity() throws Exception {
        // Initialize the database
        cityService.save(city);

        int databaseSizeBeforeDelete = cityRepository.findAll().size();

        // Delete the city
        restCityMockMvc.perform(delete("/api/cities/{id}", city.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<City> cityList = cityRepository.findAll();
        assertThat(cityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(City.class);
        City city1 = new City();
        city1.setId(1L);
        City city2 = new City();
        city2.setId(city1.getId());
        assertThat(city1).isEqualTo(city2);
        city2.setId(2L);
        assertThat(city1).isNotEqualTo(city2);
        city1.setId(null);
        assertThat(city1).isNotEqualTo(city2);
    }
}
