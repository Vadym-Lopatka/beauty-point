package com.beautypoint.app.web.rest

import com.beautypoint.app.BeautyPointApp
import com.beautypoint.app.domain.Image
import com.beautypoint.app.domain.enumeration.ImageTypeEnum
import com.beautypoint.app.repository.ImageRepository
import com.beautypoint.app.repository.search.ImageSearchRepository
import com.beautypoint.app.service.ImageQueryService
import com.beautypoint.app.service.ImageService
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
 * Test class for the ImageResource REST controller.
 *
 * @see ImageResource
 */
@SpringBootTest(classes = [BeautyPointApp::class])
class ImageResourceIT {

    @Autowired
    private lateinit var imageRepository: ImageRepository

    @Autowired
    private lateinit var imageService: ImageService

    /**
     * This repository is mocked in the com.beautypoint.app.repository.search test package.
     *
     * @see com.beautypoint.app.repository.search.ImageSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockImageSearchRepository: ImageSearchRepository

    @Autowired
    private lateinit var imageQueryService: ImageQueryService

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

    private lateinit var restImageMockMvc: MockMvc

    private lateinit var image: Image

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val imageResource = ImageResource(imageService, imageQueryService)
        this.restImageMockMvc = MockMvcBuilders.standaloneSetup(imageResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        image = createEntity(em)
    }

    @Test
    @Transactional
    fun createImage() {
        val databaseSizeBeforeCreate = imageRepository.findAll().size

        // Create the Image
        restImageMockMvc.perform(
            post("/api/images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(image))
        ).andExpect(status().isCreated)

        // Validate the Image in the database
        val imageList = imageRepository.findAll()
        assertThat(imageList).hasSize(databaseSizeBeforeCreate + 1)
        val testImage = imageList[imageList.size - 1]
        assertThat(testImage.source).isEqualTo(DEFAULT_SOURCE)
        assertThat(testImage.imageType).isEqualTo(DEFAULT_IMAGE_TYPE)

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(1)).save(testImage)
    }

    @Test
    @Transactional
    fun createImageWithExistingId() {
        val databaseSizeBeforeCreate = imageRepository.findAll().size

        // Create the Image with an existing ID
        image.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restImageMockMvc.perform(
            post("/api/images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(image))
        ).andExpect(status().isBadRequest)

        // Validate the Image in the database
        val imageList = imageRepository.findAll()
        assertThat(imageList).hasSize(databaseSizeBeforeCreate)

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(0)).save(image)
    }


    @Test
    @Transactional
    fun checkSourceIsRequired() {
        val databaseSizeBeforeTest = imageRepository.findAll().size
        // set the field null
        image.source = null

        // Create the Image, which fails.

        restImageMockMvc.perform(
            post("/api/images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(image))
        ).andExpect(status().isBadRequest)

        val imageList = imageRepository.findAll()
        assertThat(imageList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkImageTypeIsRequired() {
        val databaseSizeBeforeTest = imageRepository.findAll().size
        // set the field null
        image.imageType = null

        // Create the Image, which fails.

        restImageMockMvc.perform(
            post("/api/images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(image))
        ).andExpect(status().isBadRequest)

        val imageList = imageRepository.findAll()
        assertThat(imageList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun getAllImages() {
        // Initialize the database
        imageRepository.saveAndFlush(image)

        // Get all the imageList
        restImageMockMvc.perform(get("/api/images?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(image.id?.toInt())))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
            .andExpect(jsonPath("$.[*].imageType").value(hasItem(DEFAULT_IMAGE_TYPE.toString())))
    }

    @Test
    @Transactional
    fun getImage() {
        // Initialize the database
        imageRepository.saveAndFlush(image)

        val id = image.id
        assertNotNull(id)

        // Get the image
        restImageMockMvc.perform(get("/api/images/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(id.toInt()))
            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE))
            .andExpect(jsonPath("$.imageType").value(DEFAULT_IMAGE_TYPE.toString()))
    }

    @Test
    @Transactional
    fun getAllImagesBySourceIsEqualToSomething() {
        // Initialize the database
        imageRepository.saveAndFlush(image)

        // Get all the imageList where source equals to DEFAULT_SOURCE
        defaultImageShouldBeFound("source.equals=$DEFAULT_SOURCE")

        // Get all the imageList where source equals to UPDATED_SOURCE
        defaultImageShouldNotBeFound("source.equals=$UPDATED_SOURCE")
    }

    @Test
    @Transactional
    fun getAllImagesBySourceIsInShouldWork() {
        // Initialize the database
        imageRepository.saveAndFlush(image)

        // Get all the imageList where source in DEFAULT_SOURCE or UPDATED_SOURCE
        defaultImageShouldBeFound("source.in=$DEFAULT_SOURCE,$UPDATED_SOURCE")

        // Get all the imageList where source equals to UPDATED_SOURCE
        defaultImageShouldNotBeFound("source.in=$UPDATED_SOURCE")
    }

    @Test
    @Transactional
    fun getAllImagesBySourceIsNullOrNotNull() {
        // Initialize the database
        imageRepository.saveAndFlush(image)

        // Get all the imageList where source is not null
        defaultImageShouldBeFound("source.specified=true")

        // Get all the imageList where source is null
        defaultImageShouldNotBeFound("source.specified=false")
    }

    @Test
    @Transactional
    fun getAllImagesByImageTypeIsEqualToSomething() {
        // Initialize the database
        imageRepository.saveAndFlush(image)

        // Get all the imageList where imageType equals to DEFAULT_IMAGE_TYPE
        defaultImageShouldBeFound("imageType.equals=$DEFAULT_IMAGE_TYPE")

        // Get all the imageList where imageType equals to UPDATED_IMAGE_TYPE
        defaultImageShouldNotBeFound("imageType.equals=$UPDATED_IMAGE_TYPE")
    }

    @Test
    @Transactional
    fun getAllImagesByImageTypeIsInShouldWork() {
        // Initialize the database
        imageRepository.saveAndFlush(image)

        // Get all the imageList where imageType in DEFAULT_IMAGE_TYPE or UPDATED_IMAGE_TYPE
        defaultImageShouldBeFound("imageType.in=$DEFAULT_IMAGE_TYPE,$UPDATED_IMAGE_TYPE")

        // Get all the imageList where imageType equals to UPDATED_IMAGE_TYPE
        defaultImageShouldNotBeFound("imageType.in=$UPDATED_IMAGE_TYPE")
    }

    @Test
    @Transactional
    fun getAllImagesByImageTypeIsNullOrNotNull() {
        // Initialize the database
        imageRepository.saveAndFlush(image)

        // Get all the imageList where imageType is not null
        defaultImageShouldBeFound("imageType.specified=true")

        // Get all the imageList where imageType is null
        defaultImageShouldNotBeFound("imageType.specified=false")
    }

    @Test
    @Transactional
    fun getAllImagesByOwnerIsEqualToSomething() {
        // Initialize the database
        val owner = UserResourceIT.createEntity(em)
        em.persist(owner)
        em.flush()
        image.owner = owner
        imageRepository.saveAndFlush(image)
        val ownerId = owner.id

        // Get all the imageList where owner equals to ownerId
        defaultImageShouldBeFound("ownerId.equals=$ownerId")

        // Get all the imageList where owner equals to ownerId + 1
        defaultImageShouldNotBeFound("ownerId.equals=${ownerId?.plus(1)}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private fun defaultImageShouldBeFound(filter: String) {
        restImageMockMvc.perform(get("/api/images?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(image.id?.toInt())))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
            .andExpect(jsonPath("$.[*].imageType").value(hasItem(DEFAULT_IMAGE_TYPE.toString())))

        // Check, that the count call also returns 1
        restImageMockMvc.perform(get("/api/images/count?sort=id,desc&" + filter))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private fun defaultImageShouldNotBeFound(filter: String) {
        restImageMockMvc.perform(get("/api/images?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restImageMockMvc.perform(get("/api/images/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"))
    }

    @Test
    @Transactional
    fun getNonExistingImage() {
        // Get the image
        restImageMockMvc.perform(get("/api/images/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateImage() {
        // Initialize the database
        imageService.save(image)
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockImageSearchRepository)

        val databaseSizeBeforeUpdate = imageRepository.findAll().size

        // Update the image
        val id = image.id
        assertNotNull(id)
        val updatedImage = imageRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedImage are not directly saved in db
        em.detach(updatedImage)
        updatedImage.source = UPDATED_SOURCE
        updatedImage.imageType = UPDATED_IMAGE_TYPE

        restImageMockMvc.perform(
            put("/api/images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedImage))
        ).andExpect(status().isOk)

        // Validate the Image in the database
        val imageList = imageRepository.findAll()
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate)
        val testImage = imageList[imageList.size - 1]
        assertThat(testImage.source).isEqualTo(UPDATED_SOURCE)
        assertThat(testImage.imageType).isEqualTo(UPDATED_IMAGE_TYPE)

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(1)).save(testImage)
    }

    @Test
    @Transactional
    fun updateNonExistingImage() {
        val databaseSizeBeforeUpdate = imageRepository.findAll().size

        // Create the Image

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImageMockMvc.perform(
            put("/api/images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(image))
        ).andExpect(status().isBadRequest)

        // Validate the Image in the database
        val imageList = imageRepository.findAll()
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate)

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(0)).save(image)
    }

    @Test
    @Transactional
    fun deleteImage() {
        // Initialize the database
        imageService.save(image)

        val databaseSizeBeforeDelete = imageRepository.findAll().size

        val id = image.id
        assertNotNull(id)

        // Delete the image
        restImageMockMvc.perform(
            delete("/api/images/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent)

        // Validate the database is empty
        val imageList = imageRepository.findAll()
        assertThat(imageList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the Image in Elasticsearch
        verify(mockImageSearchRepository, times(1)).deleteById(id)
    }

    @Test
    @Transactional
    fun searchImage() {
        // Initialize the database
        imageService.save(image)
        `when`(mockImageSearchRepository.search(queryStringQuery("id:" + image.id), PageRequest.of(0, 20)))
            .thenReturn(PageImpl(Collections.singletonList(image), PageRequest.of(0, 1), 1))
        // Search the image
        restImageMockMvc.perform(get("/api/_search/images?query=id:" + image.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(image.id?.toInt())))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
            .andExpect(jsonPath("$.[*].imageType").value(hasItem(DEFAULT_IMAGE_TYPE.toString())))
    }

    @Test
    @Transactional
    fun equalsVerifier() {
        TestUtil.equalsVerifier(Image::class.java)
        val image1 = Image()
        image1.id = 1L
        val image2 = Image()
        image2.id = image1.id
        assertThat(image1).isEqualTo(image2)
        image2.id = 2L
        assertThat(image1).isNotEqualTo(image2)
        image1.id = null
        assertThat(image1).isNotEqualTo(image2)
    }

    companion object {

        private const val DEFAULT_SOURCE: String = "AAAAAAAAAA"
        private const val UPDATED_SOURCE = "BBBBBBBBBB"

        private val DEFAULT_IMAGE_TYPE: ImageTypeEnum = ImageTypeEnum.PROFILE
        private val UPDATED_IMAGE_TYPE: ImageTypeEnum = ImageTypeEnum.SALON
        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Image {
            val image = Image()
            image.source = DEFAULT_SOURCE
            image.imageType = DEFAULT_IMAGE_TYPE

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            image.owner = user
            return image
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Image {
            val image = Image()
            image.source = UPDATED_SOURCE
            image.imageType = UPDATED_IMAGE_TYPE

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            image.owner = user
            return image
        }
    }
}
