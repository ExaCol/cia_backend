package CIA.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CIA.app.model.Payments;

public interface PaymentsRepository extends JpaRepository<Payments, Integer>{

    @Query("""
        select p
        from Payments p
        join p.services s
        where s.usr.id = :id
        """)
    List<Payments> getPaymentsByUser(@Param("id") Integer id);

}
