package CIA.app.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "partner")
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String name;

    @Column
    private Double lat;

    @Column
    private Double lon;

    @Column
    private boolean soat;

    @Column
    private boolean techno;

    @OneToMany(mappedBy = "partner")
    @JsonIgnore
    private List<Services> service;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public boolean isSoat() {
        return soat;
    }

    public void setSoat(boolean soat) {
        this.soat = soat;
    }

    public boolean isTechno() {
        return techno;
    }

    public void setTechno(boolean techno) {
        this.techno = techno;
    }

    public List<Services> getService() {
        return service;
    }

    public void setService(List<Services> service) {
        this.service = service;
    }
}