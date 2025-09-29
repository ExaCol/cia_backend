package CIA.app.model;

import java.util.List;

//import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonIdentityInfo;
//import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "courses_data")
public class CoursesData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String name;

    @Column(name = "parcial_capacity")
    private int parcialCapacity;

    @Column
    private int capacity;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    //@JsonBackReference(value="usr-courses")
    private List<Usr> usrs;

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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Usr> getUsrs() {
        return usrs;
    }

    public void setUsrs(List<Usr> usrs) {
        this.usrs = usrs;
    }

    public int getParcialCapacity() {
        return parcialCapacity;
    }

    public void setParcialCapacity(int parcialCapacity) {
        this.parcialCapacity = parcialCapacity;
    }
    
    
}
