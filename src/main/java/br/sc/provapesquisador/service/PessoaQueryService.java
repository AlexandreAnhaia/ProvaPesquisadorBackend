package br.sc.provapesquisador.service;

import br.sc.provapesquisador.domain.*; // for static metamodels
import br.sc.provapesquisador.domain.Pessoa;
import br.sc.provapesquisador.repository.PessoaRepository;
import br.sc.provapesquisador.service.criteria.PessoaCriteria;
import br.sc.provapesquisador.service.dto.PessoaDTO;
import br.sc.provapesquisador.service.mapper.PessoaMapper;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Pessoa} entities in the database.
 * The main input is a {@link PessoaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PessoaDTO} or a {@link Page} of {@link PessoaDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PessoaQueryService extends QueryService<Pessoa> {

    private final Logger log = LoggerFactory.getLogger(PessoaQueryService.class);

    private final PessoaRepository pessoaRepository;

    private final PessoaMapper pessoaMapper;

    public PessoaQueryService(PessoaRepository pessoaRepository, PessoaMapper pessoaMapper) {
        this.pessoaRepository = pessoaRepository;
        this.pessoaMapper = pessoaMapper;
    }

    /**
     * Return a {@link List} of {@link PessoaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PessoaDTO> findByCriteria(PessoaCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Pessoa> specification = createSpecification(criteria);
        return pessoaMapper.toDto(pessoaRepository.findAll(specification));
    }

    /**
     * Return a {@link List} of {@link PessoaDTO} which matches the criteria from the database.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Pessoa> findAll(Pageable pageable) {
        log.debug("find by criteria : {}");
        return pessoaRepository.findAll(pageable);
    }

    /**
     * Return a {@link List} of {@link PessoaDTO} which matches the name from the database.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Pessoa> findAllByName(String name, Pageable pageable) {
        log.debug("find by criteria : {}");
        return pessoaRepository.findAllByName(name, pageable);
    }

    /**
     * Return a {@link List} of {@link PessoaDTO} which matches the cpf from the database.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Pessoa> findAllByCpf(String cpf, Pageable pageable) {
        log.debug("find by criteria : {}");
        return pessoaRepository.findAllByCpf(cpf, pageable);
    }

    /**
     * Return a {@link List} of {@link PessoaDTO} which matches the email from the database.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Pessoa> findAllByEmail(String email, Pageable pageable) {
        log.debug("find by criteria : {}");
        return pessoaRepository.findAllByEmail(email, pageable);
    }

    /**
     * Return a {@link List} of {@link PessoaDTO} which matches the birthDate from the database.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Pessoa> findAllByBirthDate(LocalDate birthDate, Pageable pageable) {
        log.debug("find by criteria : {}");
        System.out.println(birthDate);
        return pessoaRepository.findAllByBirthDate(birthDate, pageable);
    }

    /**
     * Return a {@link Page} of {@link PessoaDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PessoaDTO> findByCriteria(PessoaCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Pessoa> specification = createSpecification(criteria);
        return pessoaRepository.findAll(specification, page).map(pessoaMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PessoaCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Pessoa> specification = createSpecification(criteria);
        return pessoaRepository.count(specification);
    }

    /**
     * Function to convert {@link PessoaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Pessoa> createSpecification(PessoaCriteria criteria) {
        Specification<Pessoa> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Pessoa_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Pessoa_.name));
            }
            if (criteria.getCpf() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCpf(), Pessoa_.cpf));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Pessoa_.email));
            }
            if (criteria.getBirthDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBirthDate(), Pessoa_.birthDate));
            }
            if (criteria.getExcluded() != null) {
                specification = specification.and(buildSpecification(criteria.getExcluded(), Pessoa_.excluded));
            }
        }
        return specification;
    }
}
