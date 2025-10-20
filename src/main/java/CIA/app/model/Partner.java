package CIA.app.model;

import java.util.List;

//import java.time.LocalDate;
//import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonManagedReference;

//import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
//import jakarta.persistence.OneToMany;
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
    private Long lat;

    @Column
    private Long lon;

    @Column
    private boolean soat;

    @Column
    private boolean techno;

    //@ManyToOne
    //@JoinColumn(name = "service_id")
    //@JsonBackReference(value="service-partner")
    //private Services service;

    @OneToMany(mappedBy = "partner")
    @JsonBackReference(value="service-partner")
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

    public Long getLat() {
        return lat;
    }

    public void setLat(Long lat) {
        this.lat = lat;
    }

    public Long getLon() {
        return lon;
    }

    public void setLon(Long lon) {
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
