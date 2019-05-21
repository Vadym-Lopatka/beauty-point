package com.beautypoint.app.web.rest

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.domain.Category
import com.beautypoint.app.repository.CategoryRepository
import com.beautypoint.app.repository.search.CategorySearchRepository
import com.beautypoint.app.service.CategoryQueryService
import com.beautypoint.app.service.CategoryService
import com.beautypoint.app.web.rest.TestUtil.createFormattingConversionService
import com.beautypoint.app.web.rest.errors.ExceptionTranslator
import org.assertj.core.api.Assertions.assertThat
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.util.*
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Test class for the CategoryResource REST controller.
 *
 * @see CategoryResource
 */
@SpringBootTest(classes = [BeautyPointApp::class])
class CategoryResourceIT {

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var categoryService: CategoryService

    /**
     * This repository is mocked in the com.beautypoint.app.repository.search test package.
     *
     * @see com.beautypoint.app.repository.search.CategorySearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockCategorySearchRepository: CategorySearchRepository

    @Autowired
    private lateinit var categoryQueryService: CategoryQueryService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restCategoryMockMvc: MockMvc

    private lateinit var category: Category

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val categoryResource = CategoryResource(categoryService, categoryQueryService)
        this.restCategoryMockMvc = MockMvcBuilders.standaloneSetup(categoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        category = createEntity(em)
    }

    @Test
    @Transactional
    fun createCategory() {
        val databaseSizeBeforeCreate = categoryRepository.findAll().size

        // Create the Category
        restCategoryMockMvc.perform(
            post("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(category))
        ).andExpect(status().isCreated)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate + 1)
        val testCategory = categoryList[categoryList.size - 1]
        assertThat(testCategory.name).isEqualTo(DEFAULT_NAME)
        assertThat(testCategory.main).isEqualTo(DEFAULT_MAIN)

        // Validate the Category in Elasticsearch
        verify(mockCategorySearchRepository, times(1)).save(testCategory)
    }

    @Test
    @Transactional
    fun createCategoryWithExistingId() {
        val databaseSizeBeforeCreate = categoryRepository.findAll().size

        // Create the Category with an existing ID
        category.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoryMockMvc.perform(
            post("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(category))
        ).andExpect(status().isBadRequest)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate)

        // Validate the Category in Elasticsearch
        verify(mockCategorySearchRepository, times(0)).save(category)
    }


    @Test
    @Transactional
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = categoryRepository.findAll().size
        // set the field null
        category.name = null

        // Create the Category, which fails.

        restCategoryMockMvc.perform(
            post("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(category))
        ).andExpect(status().isBadRequest)

        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkMainIsRequired() {
        val databaseSizeBeforeTest = categoryRepository.findAll().size
        // set the field null
        category.main = null

        // Create the Category, which fails.

        restCategoryMockMvc.perform(
            post("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(category))
        ).andExpect(status().isBadRequest)

        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllCategories() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList
        restCategoryMockMvc.perform(get("/api/categories?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].main").value(hasItem(DEFAULT_MAIN)))
    }

    @Test
    @Transactional
    fun getCategory() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        val id = category.id
        assertNotNull(id)

        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.main").value(DEFAULT_MAIN))
    }

    @Test
    @Transactional
    fun getAllCategoriesByNameIsEqualToSomething() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where name equals to DEFAULT_NAME
        defaultCategoryShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the categoryList where name equals to UPDATED_NAME
        defaultCategoryShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    fun getAllCategoriesByNameIsInShouldWork() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCategoryShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the categoryList where name equals to UPDATED_NAME
        defaultCategoryShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    fun getAllCategoriesByNameIsNullOrNotNull() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where name is not null
        defaultCategoryShouldBeFound("name.specified=true")

        // Get all the categoryList where name is null
        defaultCategoryShouldNotBeFound("name.specified=false")
    }

    @Test
    @Transactional
    fun getAllCategoriesByMainIsEqualToSomething() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where main equals to DEFAULT_MAIN
        defaultCategoryShouldBeFound("main.equals=$DEFAULT_MAIN")

        // Get all the categoryList where main equals to UPDATED_MAIN
        defaultCategoryShouldNotBeFound("main.equals=$UPDATED_MAIN")
    }

    @Test
    @Transactional
    fun getAllCategoriesByMainIsInShouldWork() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where main in DEFAULT_MAIN or UPDATED_MAIN
        defaultCategoryShouldBeFound("main.in=$DEFAULT_MAIN,$UPDATED_MAIN")

        // Get all the categoryList where main equals to UPDATED_MAIN
        defaultCategoryShouldNotBeFound("main.in=$UPDATED_MAIN")
    }

    @Test
    @Transactional
    fun getAllCategoriesByMainIsNullOrNotNull() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where main is not null
        defaultCategoryShouldBeFound("main.specified=true")

        // Get all the categoryList where main is null
        defaultCategoryShouldNotBeFound("main.specified=false")
    }

    @Test
    @Transactional
    fun getAllCategoriesByParentIsEqualToSomething() {
        // Initialize the database
        val parent = CategoryResourceIT.createEntity(em)
        em.persist(parent)
        em.flush()
        category.parent = parent
        categoryRepository.saveAndFlush(category)
        val parentId = parent.id

        // Get all the categoryList where parent equals to parentId
        defaultCategoryShouldBeFound("parentId.equals=$parentId")

        // Get all the categoryList where parent equals to parentId + 1
        defaultCategoryShouldNotBeFound("parentId.equals=${parentId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllCategoriesBySalonIsEqualToSomething() {
        // Initialize the database
        val salon = SalonResourceIT.createEntity(em)
        em.persist(salon)
        em.flush()
        category.addSalon(salon)
        categoryRepository.saveAndFlush(category)
        val salonId = salon.id

        // Get all the categoryList where salon equals to salonId
        defaultCategoryShouldBeFound("salonId.equals=$salonId")

        // Get all the categoryList where salon equals to salonId + 1
        defaultCategoryShouldNotBeFound("salonId.equals=${salonId?.plus(1)}")
    }

    @Test
    @Transactional
    fun getAllCategoriesByMasterIsEqualToSomething() {
        // Initialize the database
        val master = MasterResourceIT.createEntity(em)
        em.persist(master)
        em.flush()
        category.addMaster(master)
        categoryRepository.saveAndFlush(category)
        val masterId = master.id

        // Get all the categoryList where master equals to masterId
        defaultCategoryShouldBeFound("masterId.equals=$masterId")

        // Get all the categoryList where master equals to masterId + 1
        defaultCategoryShouldNotBeFound("masterId.equals=${masterId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultCategoryShouldBeFound(filter: String) {
        restCategoryMockMvc.perform(get("/api/categories?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].main").value(hasItem(DEFAULT_MAIN)))

        // Check, that the count call also returns 1
        restCategoryMockMvc.perform(get("/api/categories/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultCategoryShouldNotBeFound(filter: String) {
        restCategoryMockMvc.perform(get("/api/categories?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restCategoryMockMvc.perform(get("/api/categories/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingCategory() {
        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateCategory() {
        // Initialize the database
        categoryService.save(category)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCategorySearchRepository)

        val databaseSizeBeforeUpdate = categoryRepository.findAll().size

        // Update the category
        val id = category.id
        assertNotNull(id)
        val updatedCategory = categoryRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedCategory are not directly saved in db
        em.detach(updatedCategory)
        updatedCategory.name = UPDATED_NAME
        updatedCategory.main = UPDATED_MAIN

        restCategoryMockMvc.perform(
            put("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCategory))
        ).andExpect(status().isOk)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)
        val testCategory = categoryList[categoryList.size - 1]
        assertThat(testCategory.name).isEqualTo(UPDATED_NAME)
        assertThat(testCategory.main).isEqualTo(UPDATED_MAIN)

        // Validate the Category in Elasticsearch
        verify(mockCategorySearchRepository, times(1)).save(testCategory)
    }

    @Test
    @Transactional
    fun updateNonExistingCategory() {
        val databaseSizeBeforeUpdate = categoryRepository.findAll().size

        // Create the Category

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoryMockMvc.perform(
            put("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(category))
        ).andExpect(status().isBadRequest)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Category in Elasticsearch
        verify(mockCategorySearchRepository, times(0)).save(category)
    }

    @Test
    @Transactional
    fun deleteCategory() {
        // Initialize the database
        categoryService.save(category)

        val databaseSizeBeforeDelete = categoryRepository.findAll().size

        val id = category.id
        assertNotNull(id)

        // Delete the category
        restCategoryMockMvc.perform(
            delete("/api/categories/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Category in Elasticsearch
        verify(mockCategorySearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    fun searchCategory() {
        // Initialize the database
        categoryService.save(category)
        `when`(mockCategorySearchRepository.search(queryStringQuery("id:" + category.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(Collections.singletonList(category), PageRequest.of(0, 1), 1))
        // Search the category
        restCategoryMockMvc.perform(get("/api/_search/categories?query=id:" + category.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].main").value(hasItem(DEFAULT_MAIN)))
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(Category::class.java)
        val category1 = Category()
        category1.id = 1L
        val category2 = Category()
        category2.id = category1.id
        assertThat(category1).isEqualTo(category2)
        category2.id = 2L
        assertThat(category1).isNotEqualTo(category2)
        category1.id = null
        assertThat(category1).isNotEqualTo(category2)
    }

    companion object {

        private const val DEFAULT_NAME: String = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_MAIN: Boolean = false
        private const val UPDATED_MAIN: Boolean = true
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Category {
            val category = Category()
            category.name = DEFAULT_NAME
            category.main = DEFAULT_MAIN

            return category
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Category {
            val category = Category()
            category.name = UPDATED_NAME
            category.main = UPDATED_MAIN

            return category
        }
    }
}
