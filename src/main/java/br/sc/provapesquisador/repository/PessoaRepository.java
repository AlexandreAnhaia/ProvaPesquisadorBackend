package br.sc.provapesquisador.repository;

import br.sc.provapesquisador.domain.Pessoa;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Pessoa entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long>, JpaSpecificationExecutor<Pessoa> {
    @Query(value = "SELECT * FROM pessoa as p where p.excluded = false", nativeQuery = true)
    Page<Pessoa> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM pessoa as p where p.name LIKE %:name% and p.excluded = false", nativeQuery = true)
    Page<Pessoa> findAllByName(@Param("name") String name, Pageable pageable);

    @Query(value = "SELECT * FROM pessoa as p where p.cpf LIKE %:cpf% and p.excluded = false", nativeQuery = true)
    Page<Pessoa> findAllByCpf(@Param("cpf") String cpf, Pageable pageable);

    @Query(value = "SELECT * FROM pessoa as p where p.email LIKE %:email% and p.excluded = false", nativeQuery = true)
    Page<Pessoa> findAllByEmail(@Param("email") String email, Pageable pageable);

    @Query(value = "SELECT * FROM pessoa as p where p.birth_date = :birthdate and p.excluded = false", nativeQuery = true)
    Page<Pessoa> findAllByBirthDate(@Param("birthdate") LocalDate birthdate, Pageable pageable);
}
