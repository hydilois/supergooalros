package cm.elsha.supergooalros.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Work.
 */
@Entity
@Table(name = "work")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "work")
public class Work implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @NotNull
    @Column(name = "heuredebut", nullable = false)
    private String heuredebut;

    @NotNull
    @Column(name = "heure_fin", nullable = false)
    private String heureFin;

    @NotNull
    @Column(name = "nombre_tickets", nullable = false)
    private Integer nombreTickets;

    @NotNull
    @Column(name = "somme_encaissee", nullable = false)
    private Double sommeEncaissee;

    @ManyToOne
    private Employe employe;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getHeuredebut() {
        return heuredebut;
    }

    public void setHeuredebut(String heuredebut) {
        this.heuredebut = heuredebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }

    public Integer getNombreTickets() {
        return nombreTickets;
    }

    public void setNombreTickets(Integer nombreTickets) {
        this.nombreTickets = nombreTickets;
    }

    public Double getSommeEncaissee() {
        return sommeEncaissee;
    }

    public void setSommeEncaissee(Double sommeEncaissee) {
        this.sommeEncaissee = sommeEncaissee;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Work work = (Work) o;
        if(work.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, work.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Work{" +
            "id=" + id +
            ", date='" + date + "'" +
            ", heuredebut='" + heuredebut + "'" +
            ", heureFin='" + heureFin + "'" +
            ", nombreTickets='" + nombreTickets + "'" +
            ", sommeEncaissee='" + sommeEncaissee + "'" +
            '}';
    }
}
