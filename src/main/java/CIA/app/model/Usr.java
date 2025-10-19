package CIA.app.model;

import java.util.List;

//import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "usr")
public class Usr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String name;
    @Column(unique = true)
    private String identification;
    @Column(unique = true)
    private String email;
    @Column
    private String password;
    @Column
    private String role;
    @Column
    private Double lon;
    @Column
    private Double lat;

    @OneToMany(mappedBy = "usr", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value="usr-vehicle")
    private List<Vehicle> vehicles;

    //@JsonManagedReference(value="usr-courses")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "courses_usr",
        joinColumns = @JoinColumn(name = "usrId"),
        inverseJoinColumns = @JoinColumn(name = "courseId")
    )
    private List<CoursesData> courses;

    @OneToMany(mappedBy = "usr", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "usr-services") 
    private List<Services> services;

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
    public String getIdentification() {
        return identification;
    }
    public void setIdentification(String identification) {
        this.identification = identification;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public Double getLon() {
        return lon;
    }
    public void setLon(Double lon) {
        this.lon = lon;
    }
    public Double getLat() {
        return lat;
    }
    public void setLat(Double lat) {
        this.lat = lat;
    }
    public List<Vehicle> getVehicles() {
        return vehicles;
    }
    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
    public List<CoursesData> getCourses() {
        return courses;
    }
    public void setCourses(List<CoursesData> courses) {
        this.courses = courses;
    }
    public List<Services> getServices() {
        return services;
    }
    public void setServices(List<Services> services) {
        this.services = services;
    }
    
}
