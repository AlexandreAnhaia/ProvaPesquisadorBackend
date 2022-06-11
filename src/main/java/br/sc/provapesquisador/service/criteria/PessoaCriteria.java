package br.sc.provapesquisador.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link br.sc.provapesquisador.domain.Pessoa} entity. This class is used
 * in {@link br.sc.provapesquisador.web.rest.PessoaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pessoas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class PessoaCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter cpf;

    private StringFilter email;

    private LocalDateFilter birthDate;

    private BooleanFilter excluded;

    private Boolean distinct;

    public PessoaCriteria() {}

    public PessoaCriteria(PessoaCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.cpf = other.cpf == null ? null : other.cpf.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.birthDate = other.birthDate == null ? null : other.birthDate.copy();
        this.excluded = other.excluded == null ? null : other.excluded.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PessoaCriteria copy() {
        return new PessoaCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getCpf() {
        return cpf;
    }

    public StringFilter cpf() {
        if (cpf == null) {
            cpf = new StringFilter();
        }
        return cpf;
    }

    public void setCpf(StringFilter cpf) {
        this.cpf = cpf;
    }

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public LocalDateFilter getBirthDate() {
        return birthDate;
    }

    public LocalDateFilter birthDate() {
        if (birthDate == null) {
            birthDate = new LocalDateFilter();
        }
        return birthDate;
    }

    public void setBirthDate(LocalDateFilter birthDate) {
        this.birthDate = birthDate;
    }

    public BooleanFilter getExcluded() {
        return excluded;
    }

    public BooleanFilter excluded() {
        if (excluded == null) {
            excluded = new BooleanFilter();
        }
        return excluded;
    }

    public void setExcluded(BooleanFilter excluded) {
        this.excluded = excluded;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PessoaCriteria that = (PessoaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(cpf, that.cpf) &&
            Objects.equals(email, that.email) &&
            Objects.equals(birthDate, that.birthDate) &&
            Objects.equals(excluded, that.excluded) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cpf, email, birthDate, excluded, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (cpf != null ? "cpf=" + cpf + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (birthDate != null ? "birthDate=" + birthDate + ", " : "") +
            (excluded != null ? "excluded=" + excluded + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
