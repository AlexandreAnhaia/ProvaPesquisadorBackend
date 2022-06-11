package br.sc.provapesquisador.service.mapper;

import br.sc.provapesquisador.domain.Pessoa;
import br.sc.provapesquisador.service.dto.PessoaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pessoa} and its DTO {@link PessoaDTO}.
 */
@Mapper(componentModel = "spring")
public interface PessoaMapper extends EntityMapper<PessoaDTO, Pessoa> {}
