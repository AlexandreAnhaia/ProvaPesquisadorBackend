package br.sc.provapesquisador.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.sc.provapesquisador.IntegrationTest;
import br.sc.provapesquisador.domain.Pessoa;
import br.sc.provapesquisador.repository.PessoaRepository;
import br.sc.provapesquisador.service.criteria.PessoaCriteria;
import br.sc.provapesquisador.service.dto.PessoaDTO;
import br.sc.provapesquisador.service.mapper.PessoaMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import liquibase.pro.packaged.P;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PessoaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PessoaResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CPF = "AAAAAAAAAA";
    private static final String UPDATED_CPF = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final byte[] DEFAULT_AVATAR = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_AVATAR = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_AVATAR_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_AVATAR_CONTENT_TYPE = "image/png";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BIRTH_DATE = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_EXCLUDED = false;
    private static final Boolean UPDATED_EXCLUDED = true;

    private static final String ENTITY_API_URL = "/api/pessoas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_API_URL_GETBYNAME = "/api/pessoas-search-name/?name=A";
    private static final String ENTITY_API_URL_GETBYCPF = "/api/pessoas-search-cpf/?cpf=AAA";
    private static final String ENTITY_API_URL_GETBYEMAIL = "/api/pessoas-search-email/?email=AAA";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaMapper pessoaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPessoaMockMvc;

    private Pessoa pessoa;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pessoa createEntity(EntityManager em) {
        Pessoa pessoa = new Pessoa()
            .name(DEFAULT_NAME)
            .cpf(DEFAULT_CPF)
            .email(DEFAULT_EMAIL)
            .avatar(DEFAULT_AVATAR)
            .avatarContentType(DEFAULT_AVATAR_CONTENT_TYPE)
            .birthDate(DEFAULT_BIRTH_DATE)
            .excluded(DEFAULT_EXCLUDED);
        return pessoa;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pessoa createUpdatedEntity(EntityManager em) {
        Pessoa pessoa = new Pessoa()
            .name(UPDATED_NAME)
            .cpf(UPDATED_CPF)
            .email(UPDATED_EMAIL)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .birthDate(UPDATED_BIRTH_DATE)
            .excluded(UPDATED_EXCLUDED);
        return pessoa;
    }

    @BeforeEach
    public void initTest() {
        pessoa = createEntity(em);
    }

    @Test
    @Transactional
    void createPessoa() throws Exception {
        int databaseSizeBeforeCreate = pessoaRepository.findAll().size();
        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);
        restPessoaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isCreated());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeCreate + 1);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPessoa.getCpf()).isEqualTo(DEFAULT_CPF);
        assertThat(testPessoa.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testPessoa.getAvatar()).isEqualTo(DEFAULT_AVATAR);
        assertThat(testPessoa.getAvatarContentType()).isEqualTo(DEFAULT_AVATAR_CONTENT_TYPE);
        assertThat(testPessoa.getBirthDate()).isEqualTo(DEFAULT_BIRTH_DATE);
        assertThat(testPessoa.getExcluded()).isEqualTo(DEFAULT_EXCLUDED);
    }

    @Test
    @Transactional
    void createPessoaWithExistingId() throws Exception {
        // Create the Pessoa with an existing ID
        pessoa.setId(1L);
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        int databaseSizeBeforeCreate = pessoaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPessoaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = pessoaRepository.findAll().size();
        // set the field null
        pessoa.setName(null);

        // Create the Pessoa, which fails.
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        restPessoaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isBadRequest());

        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCpfIsRequired() throws Exception {
        int databaseSizeBeforeTest = pessoaRepository.findAll().size();
        // set the field null
        pessoa.setCpf(null);

        // Create the Pessoa, which fails.
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        restPessoaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isBadRequest());

        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = pessoaRepository.findAll().size();
        // set the field null
        pessoa.setEmail(null);

        // Create the Pessoa, which fails.
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        restPessoaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isBadRequest());

        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPessoas() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get all the pessoaList
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pessoa.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].cpf").value(hasItem(DEFAULT_CPF)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].avatarContentType").value(hasItem(DEFAULT_AVATAR_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].avatar").value(hasItem(Base64Utils.encodeToString(DEFAULT_AVATAR))))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].excluded").value(hasItem(DEFAULT_EXCLUDED.booleanValue())));
    }

    @Test
    @Transactional
    void getPessoa() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        // Get the pessoa
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL_ID, pessoa.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pessoa.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.cpf").value(DEFAULT_CPF))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.avatarContentType").value(DEFAULT_AVATAR_CONTENT_TYPE))
            .andExpect(jsonPath("$.avatar").value(Base64Utils.encodeToString(DEFAULT_AVATAR)))
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()))
            .andExpect(jsonPath("$.excluded").value(DEFAULT_EXCLUDED.booleanValue()));
    }

    //    @Test
    //    @Transactional
    //    void getPessoasByIdFiltering() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        Long id = pessoa.getId();
    //
    //        defaultPessoaShouldBeFound("id.equals=" + id);
    //        defaultPessoaShouldNotBeFound("id.notEquals=" + id);
    //
    //        defaultPessoaShouldBeFound("id.greaterThanOrEqual=" + id);
    //        defaultPessoaShouldNotBeFound("id.greaterThan=" + id);
    //
    //        defaultPessoaShouldBeFound("id.lessThanOrEqual=" + id);
    //        defaultPessoaShouldNotBeFound("id.lessThan=" + id);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByNameIsEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where name equals to DEFAULT_NAME
    //        defaultPessoaShouldBeFound("name.equals=" + DEFAULT_NAME);
    //
    //        // Get all the pessoaList where name equals to UPDATED_NAME
    //        defaultPessoaShouldNotBeFound("name.equals=" + UPDATED_NAME);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByNameIsNotEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where name not equals to DEFAULT_NAME
    //        defaultPessoaShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);
    //
    //        // Get all the pessoaList where name not equals to UPDATED_NAME
    //        defaultPessoaShouldBeFound("name.notEquals=" + UPDATED_NAME);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByNameIsInShouldWork() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where name in DEFAULT_NAME or UPDATED_NAME
    //        defaultPessoaShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);
    //
    //        // Get all the pessoaList where name equals to UPDATED_NAME
    //        defaultPessoaShouldNotBeFound("name.in=" + UPDATED_NAME);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByNameIsNullOrNotNull() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where name is not null
    //        defaultPessoaShouldBeFound("name.specified=true");
    //
    //        // Get all the pessoaList where name is null
    //        defaultPessoaShouldNotBeFound("name.specified=false");
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByNameContainsSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where name contains DEFAULT_NAME
    //        defaultPessoaShouldBeFound("name.contains=" + DEFAULT_NAME);
    //
    //        // Get all the pessoaList where name contains UPDATED_NAME
    //        defaultPessoaShouldNotBeFound("name.contains=" + UPDATED_NAME);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByNameNotContainsSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where name does not contain DEFAULT_NAME
    //        defaultPessoaShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);
    //
    //        // Get all the pessoaList where name does not contain UPDATED_NAME
    //        defaultPessoaShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByCpfIsEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where cpf equals to DEFAULT_CPF
    //        defaultPessoaShouldBeFound("cpf.equals=" + DEFAULT_CPF);
    //
    //        // Get all the pessoaList where cpf equals to UPDATED_CPF
    //        defaultPessoaShouldNotBeFound("cpf.equals=" + UPDATED_CPF);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByCpfIsNotEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where cpf not equals to DEFAULT_CPF
    //        defaultPessoaShouldNotBeFound("cpf.notEquals=" + DEFAULT_CPF);
    //
    //        // Get all the pessoaList where cpf not equals to UPDATED_CPF
    //        defaultPessoaShouldBeFound("cpf.notEquals=" + UPDATED_CPF);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByCpfIsInShouldWork() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where cpf in DEFAULT_CPF or UPDATED_CPF
    //        defaultPessoaShouldBeFound("cpf.in=" + DEFAULT_CPF + "," + UPDATED_CPF);
    //
    //        // Get all the pessoaList where cpf equals to UPDATED_CPF
    //        defaultPessoaShouldNotBeFound("cpf.in=" + UPDATED_CPF);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByCpfIsNullOrNotNull() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where cpf is not null
    //        defaultPessoaShouldBeFound("cpf.specified=true");
    //
    //        // Get all the pessoaList where cpf is null
    //        defaultPessoaShouldNotBeFound("cpf.specified=false");
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByCpfContainsSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where cpf contains DEFAULT_CPF
    //        defaultPessoaShouldBeFound("cpf.contains=" + DEFAULT_CPF);
    //
    //        // Get all the pessoaList where cpf contains UPDATED_CPF
    //        defaultPessoaShouldNotBeFound("cpf.contains=" + UPDATED_CPF);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByCpfNotContainsSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where cpf does not contain DEFAULT_CPF
    //        defaultPessoaShouldNotBeFound("cpf.doesNotContain=" + DEFAULT_CPF);
    //
    //        // Get all the pessoaList where cpf does not contain UPDATED_CPF
    //        defaultPessoaShouldBeFound("cpf.doesNotContain=" + UPDATED_CPF);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByEmailIsEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where email equals to DEFAULT_EMAIL
    //        defaultPessoaShouldBeFound("email.equals=" + DEFAULT_EMAIL);
    //
    //        // Get all the pessoaList where email equals to UPDATED_EMAIL
    //        defaultPessoaShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByEmailIsNotEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where email not equals to DEFAULT_EMAIL
    //        defaultPessoaShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);
    //
    //        // Get all the pessoaList where email not equals to UPDATED_EMAIL
    //        defaultPessoaShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByEmailIsInShouldWork() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where email in DEFAULT_EMAIL or UPDATED_EMAIL
    //        defaultPessoaShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);
    //
    //        // Get all the pessoaList where email equals to UPDATED_EMAIL
    //        defaultPessoaShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByEmailIsNullOrNotNull() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where email is not null
    //        defaultPessoaShouldBeFound("email.specified=true");
    //
    //        // Get all the pessoaList where email is null
    //        defaultPessoaShouldNotBeFound("email.specified=false");
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByEmailContainsSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where email contains DEFAULT_EMAIL
    //        defaultPessoaShouldBeFound("email.contains=" + DEFAULT_EMAIL);
    //
    //        // Get all the pessoaList where email contains UPDATED_EMAIL
    //        defaultPessoaShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByEmailNotContainsSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where email does not contain DEFAULT_EMAIL
    //        defaultPessoaShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);
    //
    //        // Get all the pessoaList where email does not contain UPDATED_EMAIL
    //        defaultPessoaShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByBirthDateIsEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where birthDate equals to DEFAULT_BIRTH_DATE
    //        defaultPessoaShouldBeFound("birthDate.equals=" + DEFAULT_BIRTH_DATE);
    //
    //        // Get all the pessoaList where birthDate equals to UPDATED_BIRTH_DATE
    //        defaultPessoaShouldNotBeFound("birthDate.equals=" + UPDATED_BIRTH_DATE);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByBirthDateIsNotEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where birthDate not equals to DEFAULT_BIRTH_DATE
    //        defaultPessoaShouldNotBeFound("birthDate.notEquals=" + DEFAULT_BIRTH_DATE);
    //
    //        // Get all the pessoaList where birthDate not equals to UPDATED_BIRTH_DATE
    //        defaultPessoaShouldBeFound("birthDate.notEquals=" + UPDATED_BIRTH_DATE);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByBirthDateIsInShouldWork() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where birthDate in DEFAULT_BIRTH_DATE or UPDATED_BIRTH_DATE
    //        defaultPessoaShouldBeFound("birthDate.in=" + DEFAULT_BIRTH_DATE + "," + UPDATED_BIRTH_DATE);
    //
    //        // Get all the pessoaList where birthDate equals to UPDATED_BIRTH_DATE
    //        defaultPessoaShouldNotBeFound("birthDate.in=" + UPDATED_BIRTH_DATE);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByBirthDateIsNullOrNotNull() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where birthDate is not null
    //        defaultPessoaShouldBeFound("birthDate.specified=true");
    //
    //        // Get all the pessoaList where birthDate is null
    //        defaultPessoaShouldNotBeFound("birthDate.specified=false");
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByBirthDateIsGreaterThanOrEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where birthDate is greater than or equal to DEFAULT_BIRTH_DATE
    //        defaultPessoaShouldBeFound("birthDate.greaterThanOrEqual=" + DEFAULT_BIRTH_DATE);
    //
    //        // Get all the pessoaList where birthDate is greater than or equal to UPDATED_BIRTH_DATE
    //        defaultPessoaShouldNotBeFound("birthDate.greaterThanOrEqual=" + SMALLER_BIRTH_DATE);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByBirthDateIsLessThanOrEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where birthDate is less than or equal to DEFAULT_BIRTH_DATE
    //        defaultPessoaShouldBeFound("birthDate.lessThanOrEqual=" + DEFAULT_BIRTH_DATE);
    //
    //        // Get all the pessoaList where birthDate is less than or equal to SMALLER_BIRTH_DATE
    //        defaultPessoaShouldNotBeFound("birthDate.lessThanOrEqual=" + SMALLER_BIRTH_DATE);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByBirthDateIsLessThanSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where birthDate is less than DEFAULT_BIRTH_DATE
    //        defaultPessoaShouldNotBeFound("birthDate.lessThan=" + DEFAULT_BIRTH_DATE);
    //
    //        // Get all the pessoaList where birthDate is less than UPDATED_BIRTH_DATE
    //        defaultPessoaShouldBeFound("birthDate.lessThan=" + UPDATED_BIRTH_DATE);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByBirthDateIsGreaterThanSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where birthDate is greater than DEFAULT_BIRTH_DATE
    //        defaultPessoaShouldNotBeFound("birthDate.greaterThan=" + DEFAULT_BIRTH_DATE);
    //
    //        // Get all the pessoaList where birthDate is greater than SMALLER_BIRTH_DATE
    //        defaultPessoaShouldBeFound("birthDate.greaterThan=" + SMALLER_BIRTH_DATE);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByExcludedIsEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where excluded equals to DEFAULT_EXCLUDED
    //        defaultPessoaShouldBeFound("excluded.equals=" + DEFAULT_EXCLUDED);
    //
    //        // Get all the pessoaList where excluded equals to UPDATED_EXCLUDED
    //        defaultPessoaShouldNotBeFound("excluded.equals=" + UPDATED_EXCLUDED);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByExcludedIsNotEqualToSomething() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where excluded not equals to DEFAULT_EXCLUDED
    //        defaultPessoaShouldNotBeFound("excluded.notEquals=" + DEFAULT_EXCLUDED);
    //
    //        // Get all the pessoaList where excluded not equals to UPDATED_EXCLUDED
    //        defaultPessoaShouldBeFound("excluded.notEquals=" + UPDATED_EXCLUDED);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByExcludedIsInShouldWork() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where excluded in DEFAULT_EXCLUDED or UPDATED_EXCLUDED
    //        defaultPessoaShouldBeFound("excluded.in=" + DEFAULT_EXCLUDED + "," + UPDATED_EXCLUDED);
    //
    //        // Get all the pessoaList where excluded equals to UPDATED_EXCLUDED
    //        defaultPessoaShouldNotBeFound("excluded.in=" + UPDATED_EXCLUDED);
    //    }

    //    @Test
    //    @Transactional
    //    void getAllPessoasByExcludedIsNullOrNotNull() throws Exception {
    //        // Initialize the database
    //        pessoaRepository.saveAndFlush(pessoa);
    //
    //        // Get all the pessoaList where excluded is not null
    //        defaultPessoaShouldBeFound("excluded.specified=true");
    //
    //        // Get all the pessoaList where excluded is null
    //        defaultPessoaShouldNotBeFound("excluded.specified=false");
    //    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPessoaShouldBeFound(String filter) throws Exception {
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pessoa.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].cpf").value(hasItem(DEFAULT_CPF)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].avatarContentType").value(hasItem(DEFAULT_AVATAR_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].avatar").value(hasItem(Base64Utils.encodeToString(DEFAULT_AVATAR))))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].excluded").value(hasItem(DEFAULT_EXCLUDED.booleanValue())));

        // Check, that the count call also returns 1
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPessoaShouldNotBeFound(String filter) throws Exception {
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getExistingPessoaStartNameWithA() throws Exception {
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeAfterUpdate = pessoaRepository.findAll().size();

        restPessoaMockMvc.perform(get(ENTITY_API_URL_GETBYNAME)).andExpect(status().isOk());
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        //        Pessoa pessoaList = pessoaRepository.findById(pessoa.getId());
        assertThat(pessoaList).hasSize(databaseSizeAfterUpdate);
    }

    @Test
    @Transactional
    void getExistingPessoaStartNameWithAIsEqual() throws Exception {
        pessoaRepository.saveAndFlush(pessoa);

        restPessoaMockMvc.perform(get(ENTITY_API_URL_GETBYNAME)).andExpect(status().isOk());
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        //        Pessoa pessoaList = pessoaRepository.findById(pessoa.getId());
        assertThat(pessoaList.get(0).getName() == pessoa.getName());
    }

    @Test
    @Transactional
    void getExistingPessoaStartNameWithAIsNotEqual() throws Exception {
        pessoaRepository.saveAndFlush(pessoa);

        Pessoa pessoa1 = new Pessoa()
            .name(UPDATED_NAME)
            .cpf(UPDATED_CPF)
            .email(UPDATED_EMAIL)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .birthDate(UPDATED_BIRTH_DATE)
            .excluded(UPDATED_EXCLUDED);
        pessoaRepository.saveAndFlush(pessoa1);

        restPessoaMockMvc.perform(get(ENTITY_API_URL_GETBYNAME)).andExpect(status().isOk());
        List<Pessoa> pessoaList = pessoaRepository.findAll();

        assertThat(pessoaList.size() == 1);
        Pessoa pessoaFinal = new Pessoa();
        for (int i = 0; i < pessoaList.size(); i++) {
            if (pessoaList.get(i).getName().equals(pessoa1.getName())) {
                pessoaFinal = pessoaList.get(i);
            }
        }

        assertThat(pessoaFinal.getName() != pessoa.getName());
    }

    @Test
    @Transactional
    void getExistingPessoaStartCpfWithAIsEqual() throws Exception {
        pessoaRepository.saveAndFlush(pessoa);

        restPessoaMockMvc.perform(get(ENTITY_API_URL_GETBYCPF)).andExpect(status().isOk());
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        //        Pessoa pessoaList = pessoaRepository.findById(pessoa.getId());
        assertThat(pessoaList.get(0).getCpf() == pessoa.getCpf());
    }

    @Test
    @Transactional
    void getExistingPessoaStartCpfWithAIsNotEqual() throws Exception {
        pessoaRepository.saveAndFlush(pessoa);

        Pessoa pessoa1 = new Pessoa()
            .name(UPDATED_NAME)
            .cpf(UPDATED_CPF)
            .email(UPDATED_EMAIL)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .birthDate(UPDATED_BIRTH_DATE)
            .excluded(UPDATED_EXCLUDED);
        pessoaRepository.saveAndFlush(pessoa1);

        restPessoaMockMvc.perform(get(ENTITY_API_URL_GETBYCPF)).andExpect(status().isOk());
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        //        Pessoa pessoaList = pessoaRepository.findById(pessoa.getId());
        assertThat(pessoaList.size() == 1);
        Pessoa pessoaFinal = new Pessoa();
        for (int i = 0; i < pessoaList.size(); i++) {
            if (pessoaList.get(i).getCpf().equals(pessoa1.getCpf())) {
                pessoaFinal = pessoaList.get(i);
            }
        }

        assertThat(pessoaFinal.getCpf() != pessoa.getCpf());
    }

    @Test
    @Transactional
    void getExistingPessoaStartEmailWithAIsEqual() throws Exception {
        pessoaRepository.saveAndFlush(pessoa);

        restPessoaMockMvc.perform(get(ENTITY_API_URL_GETBYEMAIL)).andExpect(status().isOk());
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        //        Pessoa pessoaList = pessoaRepository.findById(pessoa.getId());
        assertThat(pessoaList.size() == 1);
        assertThat(pessoaList.get(0).getEmail() == pessoa.getEmail());
    }

    @Test
    @Transactional
    void getExistingPessoaStartEmailWithAIsNotEqual() throws Exception {
        pessoaRepository.saveAndFlush(pessoa);

        Pessoa pessoa1 = new Pessoa()
            .name(UPDATED_NAME)
            .cpf(UPDATED_CPF)
            .email(UPDATED_EMAIL)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .birthDate(UPDATED_BIRTH_DATE)
            .excluded(UPDATED_EXCLUDED);
        pessoaRepository.saveAndFlush(pessoa1);

        restPessoaMockMvc.perform(get(ENTITY_API_URL_GETBYEMAIL)).andExpect(status().isOk());
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        //        Pessoa pessoaList = pessoaRepository.findById(pessoa.getId());
        assertThat(pessoaList.size() == 1);
        Pessoa pessoaFinal = new Pessoa();
        for (int i = 0; i < pessoaList.size(); i++) {
            if (pessoaList.get(i).getEmail().equals(pessoa1.getEmail())) {
                pessoaFinal = pessoaList.get(i);
            }
        }

        assertThat(pessoaFinal.getEmail() != pessoa.getEmail());
    }

    @Test
    @Transactional
    void getNonExistingPessoa() throws Exception {
        // Get the pessoa
        restPessoaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPessoa() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();

        // Update the pessoa
        Pessoa updatedPessoa = pessoaRepository.findById(pessoa.getId()).get();
        // Disconnect from session so that the updates on updatedPessoa are not directly saved in db
        em.detach(updatedPessoa);
        updatedPessoa
            .name(UPDATED_NAME)
            .cpf(UPDATED_CPF)
            .email(UPDATED_EMAIL)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .birthDate(UPDATED_BIRTH_DATE)
            .excluded(UPDATED_EXCLUDED);
        PessoaDTO pessoaDTO = pessoaMapper.toDto(updatedPessoa);

        restPessoaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pessoaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPessoa.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoa.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPessoa.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testPessoa.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testPessoa.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testPessoa.getExcluded()).isEqualTo(UPDATED_EXCLUDED);
    }

    @Test
    @Transactional
    void putNonExistingPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pessoaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePessoaWithPatch() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();

        // Update the pessoa using partial update
        Pessoa partialUpdatedPessoa = new Pessoa();
        partialUpdatedPessoa.setId(pessoa.getId());

        partialUpdatedPessoa
            .name(UPDATED_NAME)
            .cpf(UPDATED_CPF)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .birthDate(UPDATED_BIRTH_DATE)
            .excluded(UPDATED_EXCLUDED);

        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPessoa.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoa))
            )
            .andExpect(status().isOk());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPessoa.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoa.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testPessoa.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testPessoa.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testPessoa.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testPessoa.getExcluded()).isEqualTo(UPDATED_EXCLUDED);
    }

    @Test
    @Transactional
    void fullUpdatePessoaWithPatch() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();

        // Update the pessoa using partial update
        Pessoa partialUpdatedPessoa = new Pessoa();
        partialUpdatedPessoa.setId(pessoa.getId());

        partialUpdatedPessoa
            .name(UPDATED_NAME)
            .cpf(UPDATED_CPF)
            .email(UPDATED_EMAIL)
            .avatar(UPDATED_AVATAR)
            .avatarContentType(UPDATED_AVATAR_CONTENT_TYPE)
            .birthDate(UPDATED_BIRTH_DATE)
            .excluded(UPDATED_EXCLUDED);

        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPessoa.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoa))
            )
            .andExpect(status().isOk());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPessoa.getCpf()).isEqualTo(UPDATED_CPF);
        assertThat(testPessoa.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPessoa.getAvatar()).isEqualTo(UPDATED_AVATAR);
        assertThat(testPessoa.getAvatarContentType()).isEqualTo(UPDATED_AVATAR_CONTENT_TYPE);
        assertThat(testPessoa.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testPessoa.getExcluded()).isEqualTo(UPDATED_EXCLUDED);
    }

    @Test
    @Transactional
    void patchNonExistingPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pessoaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Create the Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePessoa() throws Exception {
        // Initialize the database
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeDelete = pessoaRepository.findAll().size();

        // Delete the pessoa
        restPessoaMockMvc
            .perform(delete(ENTITY_API_URL_ID, pessoa.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
