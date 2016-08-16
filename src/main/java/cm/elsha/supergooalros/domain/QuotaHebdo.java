package cm.elsha.supergooalros.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A QuotaHebdo.
 */
@Entity
@Table(name = "quota_hebdo")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "quotahebdo")
public class QuotaHebdo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "montant", nullable = false)
    private Double montant;

    @Column(name = "prime_hebdo")
    private Double primeHebdo;

    @ManyToOne
    private Shop shop;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public Double getPrimeHebdo() {
        return primeHebdo;
    }

    public void setPrimeHebdo(Double primeHebdo) {
        this.primeHebdo = primeHebdo;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuotaHebdo quotaHebdo = (QuotaHebdo) o;
        if(quotaHebdo.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, quotaHebdo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QuotaHebdo{" +
            "id=" + id +
            ", montant='" + montant + "'" +
            ", primeHebdo='" + primeHebdo + "'" +
            '}';
    }
}
