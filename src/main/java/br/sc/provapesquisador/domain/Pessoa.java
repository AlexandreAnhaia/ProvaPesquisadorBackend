package br.sc.provapesquisador.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Pessoa.
 */
@Entity
@Table(name = "pessoa")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 150)
    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @NotNull
    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @NotNull
    @Size(max = 400)
    @Column(name = "email", length = 400, nullable = false)
    private String email;

    @Lob
    @Column(name = "avatar")
    private byte[] avatar;

    @Column(name = "avatar_content_type")
    private String avatarContentType;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "excluded")
    private Boolean excluded;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pessoa id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Pessoa name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return this.cpf;
    }

    public Pessoa cpf(String cpf) {
        this.setCpf(cpf);
        return this;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return this.email;
    }

    public Pessoa email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getAvatar() {
        return this.avatar;
    }

    public Pessoa avatar(byte[] avatar) {
        this.setAvatar(avatar);
        return this;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getAvatarContentType() {
        return this.avatarContentType;
    }

    public Pessoa avatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
        return this;
    }

    public void setAvatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public Pessoa birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Boolean getExcluded() {
        return this.excluded;
    }

    public Pessoa excluded(Boolean excluded) {
        this.setExcluded(excluded);
        return this;
    }

    public void setExcluded(Boolean excluded) {
        this.excluded = excluded;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pessoa)) {
            return false;
        }
        return id != null && id.equals(((Pessoa) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pessoa{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", cpf='" + getCpf() + "'" +
            ", email='" + getEmail() + "'" +
            ", avatar='" + getAvatar() + "'" +
            ", avatarContentType='" + getAvatarContentType() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", excluded='" + getExcluded() + "'" +
            "}";
    }
}
