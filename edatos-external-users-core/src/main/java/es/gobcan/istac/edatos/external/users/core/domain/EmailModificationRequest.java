package es.gobcan.istac.edatos.external.users.core.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "tb_email_modification_request")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EmailModificationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tb_email_modification_request")
    @SequenceGenerator(name = "seq_tb_email_modification_request", sequenceName = "seq_tb_email_modification_request", allocationSize = 50, initialValue = 1)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String key;

    @Email
    @NotNull
    @Column(nullable = false, name = "new_email")
    private String newEmail;

    @OneToOne(mappedBy = "emailModificationRequest")
    private ExternalUserEntity user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public ExternalUserEntity getUser() {
        return user;
    }

    public void setUser(ExternalUserEntity user) {
        this.user = user;
    }
}
