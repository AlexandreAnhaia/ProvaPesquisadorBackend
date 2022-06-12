package br.sc.provapesquisador.web.rest;

import br.sc.provapesquisador.domain.Pessoa;
import br.sc.provapesquisador.repository.PessoaRepository;
import br.sc.provapesquisador.service.PessoaQueryService;
import br.sc.provapesquisador.service.PessoaService;
import br.sc.provapesquisador.service.criteria.PessoaCriteria;
import br.sc.provapesquisador.service.dto.PessoaDTO;
import br.sc.provapesquisador.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link br.sc.provapesquisador.domain.Pessoa}.
 */
@RestController
@RequestMapping("/api")
public class PessoaResource {

    private final Logger log = LoggerFactory.getLogger(PessoaResource.class);

    private static final String ENTITY_NAME = "pessoa";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PessoaService pessoaService;

    private final PessoaRepository pessoaRepository;

    private final PessoaQueryService pessoaQueryService;

    public PessoaResource(PessoaService pessoaService, PessoaRepository pessoaRepository, PessoaQueryService pessoaQueryService) {
        this.pessoaService = pessoaService;
        this.pessoaRepository = pessoaRepository;
        this.pessoaQueryService = pessoaQueryService;
    }

    /**
     * {@code POST  /pessoas} : Create a new pessoa.
     *
     * @param pessoaDTO the pessoaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pessoaDTO, or with status {@code 400 (Bad Request)} if the pessoa has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pessoas")
    public ResponseEntity<PessoaDTO> createPessoa(@Valid @RequestBody PessoaDTO pessoaDTO) throws URISyntaxException {
        log.debug("REST request to save Pessoa : {}", pessoaDTO);
        if (pessoaDTO.getId() != null) {
            throw new BadRequestAlertException("A new pessoa cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PessoaDTO result = pessoaService.save(pessoaDTO);
        return ResponseEntity
            .created(new URI("/api/pessoas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /pessoas/:id} : Updates an existing pessoa.
     *
     * @param id        the id of the pessoaDTO to save.
     * @param pessoaDTO the pessoaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaDTO,
     * or with status {@code 400 (Bad Request)} if the pessoaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pessoaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pessoas/{id}")
    public ResponseEntity<PessoaDTO> updatePessoa(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PessoaDTO pessoaDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Pessoa : {}, {}", id, pessoaDTO);
        if (pessoaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pessoaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PessoaDTO result = pessoaService.update(pessoaDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pessoaDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /pessoas/:id} : Partial updates given fields of an existing pessoa, field will ignore if it is null
     *
     * @param id        the id of the pessoaDTO to save.
     * @param pessoaDTO the pessoaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pessoaDTO,
     * or with status {@code 400 (Bad Request)} if the pessoaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pessoaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pessoaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pessoas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PessoaDTO> partialUpdatePessoa(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PessoaDTO pessoaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pessoa partially : {}, {}", id, pessoaDTO);
        if (pessoaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pessoaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PessoaDTO> result = pessoaService.partialUpdate(pessoaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pessoaDTO.getId().toString())
        );
    }

    //    /**
    //     * {@code GET  /pessoas} : get all the pessoas.
    //     *
    //     * @param pageable the pagination information.
    //     * @param criteria the criteria which the requested entities should match.
    //     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoas in body.
    //     */
    //    @GetMapping("/pessoas")
    //    public ResponseEntity<List<PessoaDTO>> getAllPessoasOriginal(PessoaCriteria criteria, @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
    //        log.debug("REST request to get Pessoas by criteria: {}", criteria);
    //        Page<PessoaDTO> page = pessoaQueryService.findByCriteria(criteria, pageable);
    //        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
    //        return ResponseEntity.ok().headers(headers).body(page.getContent());
    //    }

    /**
     * {@code GET  /pessoas} : get all the pessoas by name.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoas in body.
     */
    @GetMapping("/pessoas-search-name/")
    public ResponseEntity<List<Pessoa>> getAllPessoasByName(
        @RequestParam(required = false) String name,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Pessoas by criteria: {}");
        if (name != null) {
            Page<Pessoa> page = pessoaQueryService.findAllByName(name, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
        Page<Pessoa> page = pessoaQueryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pessoas} : get all the pessoas by cpf.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoas in body.
     */
    @GetMapping("/pessoas-search-cpf/")
    public ResponseEntity<List<Pessoa>> getAllPessoasByCpf(
        @RequestParam(required = false) String cpf,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Pessoas by criteria: {}");
        if (cpf != null) {
            Page<Pessoa> page = pessoaQueryService.findAllByCpf(cpf, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
        Page<Pessoa> page = pessoaQueryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pessoas} : get all the pessoas by email.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoas in body.
     */
    @GetMapping("/pessoas-search-email/")
    public ResponseEntity<List<Pessoa>> getAllPessoasByEmail(
        @RequestParam(required = false) String email,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Pessoas by criteria: {}");
        if (email != null) {
            Page<Pessoa> page = pessoaQueryService.findAllByEmail(email, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
        Page<Pessoa> page = pessoaQueryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pessoas} : get all the pessoas by birthDate.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoas in body.
     */
    @GetMapping("/pessoas-search-birthdate/")
    public ResponseEntity<List<Pessoa>> getAllPessoasByBirthDate(
        @RequestParam(required = false) String birthDate,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Pessoas by criteria: {}");
        if (birthDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            System.out.println("@@@@@@@@@@@@" + birthDate);
            LocalDate localDate = LocalDate.parse(birthDate);
            if (localDate != null) {
                System.out.println(localDate);
                Page<Pessoa> page = pessoaQueryService.findAllByBirthDate(localDate, pageable);
                HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
                return ResponseEntity.ok().headers(headers).body(page.getContent());
            }
        }
        System.out.println("@@@@@@@@@@@@@@@@");
        Page<Pessoa> page = pessoaQueryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pessoas} : get all the pessoas.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pessoas in body.
     */
    @GetMapping("/pessoas")
    public ResponseEntity<List<Pessoa>> getAllPessoas(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get Pessoas: {}");
        Page<Pessoa> page = pessoaQueryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /pessoas/count} : count all the pessoas.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/pessoas/count")
    public ResponseEntity<Long> countPessoas(PessoaCriteria criteria) {
        log.debug("REST request to count Pessoas by criteria: {}", criteria);
        return ResponseEntity.ok().body(pessoaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /pessoas/:id} : get the "id" pessoa.
     *
     * @param id the id of the pessoaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pessoaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoas/{id}")
    public ResponseEntity<PessoaDTO> getPessoa(@PathVariable Long id) {
        log.debug("REST request to get Pessoa : {}", id);
        Optional<PessoaDTO> pessoaDTO = pessoaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pessoaDTO);
    }

    /**
     * {@code DELETE  /pessoas/:id} : delete the "id" pessoa.
     *
     * @param id the id of the pessoaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoas/{id}")
    public ResponseEntity<Void> deletePessoa(@PathVariable Long id) {
        log.debug("REST request to delete Pessoa : {}", id);
        pessoaService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/check-exists/{cpf}")
    public ResponseEntity<Boolean> checkCpfExists(@PathVariable String cpf) {
        boolean exists = pessoaService.checkExistsCpf(cpf);
        return ResponseEntity.ok(exists);
    }
}
