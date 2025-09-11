package CIA.app.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TECNO_FARE {

    @Id
    private Integer id;
    private Date start_date;
    private Date end_date;
    private int price;
}
