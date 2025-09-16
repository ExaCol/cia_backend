package CIA.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SOAT_FARE {

    @Id
    public Integer id;
    public int price;

}
