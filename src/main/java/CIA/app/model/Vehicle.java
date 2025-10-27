package CIA.app.model;

import java.sql.Date;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column
    private String type;
    @Column(unique = true)
    private String plate;
    @Column
    private String soatRateType;
    @Column
    private String technoClassification;
    @Column
    @Temporal(TemporalType.DATE)
    private Date soatExpiration;
    @Column
    @Temporal(TemporalType.DATE)
    private Date technoExpiration;

    @ManyToOne
    @JoinColumn(name = "usr_id")
    @JsonBackReference(value = "usr-vehicle")
    private Usr usr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getSoatRateType() {
        return soatRateType;
    }

    public void setSoatRateType(String soatRateType) {
        this.soatRateType = soatRateType;
    }

    public String getTechnoClassification() {
        return technoClassification;
    }

    public void setTechnoClassification(String technoClassification) {
        this.technoClassification = technoClassification;
    }

    public Date getSoatExpiration() {
        return soatExpiration;
    }

    public void setSoatExpiration(Date soatExpiration) {
        this.soatExpiration = soatExpiration;
    }

    public Date getTechnoExpiration() {
        return technoExpiration;
    }

    public void setTechnoExpiration(Date technoExpiration) {
        this.technoExpiration = technoExpiration;
    }

    public Usr getUsr() {
        return usr;
    }

    public void setUsr(Usr usr) {
        this.usr = usr;
    }
}
