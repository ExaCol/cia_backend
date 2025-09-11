package CIA.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Courses {

    @Id
    private Integer id;
    private String name;
    private int capacity;

    
}
