package br.sc.provapesquisador.repository;

import br.sc.provapesquisador.domain.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Pessoa entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long>, JpaSpecificationExecutor<Pessoa> {
    @Query(value = "SELECT * FROM pessoa as p where p.excluded = false", nativeQuery = true)
    Page<Pessoa> findAll(Pageable pageable);
}
