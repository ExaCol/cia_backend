package CIA.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

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

    @Query("""
        SELECT DISTINCT p
        FROM Payments p
        JOIN p.services s
        WHERE s.usr.id = :id
        ORDER BY p.releaseDate ASC
        """)
List<Payments> findAllByUserServicesOrderByReleaseDateDesc(@Param("id") Integer id);


@Query("""
        SELECT COUNT(*)
        FROM Payments p
        WHERE p.releaseDate 
        BETWEEN :strtDate AND :endDate
        """)
Integer findPaymentsNumBetweenDates(@Param("strtDate") LocalDate strtDate, @Param("endDate") LocalDate endDate);


@Query("""
        SELECT SUM(p.amount)
        FROM Payments p
        """)
Double findTotalPaymentsAmount();

@Query("""
  SELECT COALESCE(SUM(s.price), 0) * 0.5
  FROM Services s
  WHERE s.serviceType = :type
""")
Double savedUsrMoney(@Param("type") String type);

@Query("""
    SELECT COALESCE(SUM(s.price), 0)
    FROM Services s
    WHERE s.serviceType = :type
        """)
Double earningsByCat(@Param("type") String type);



@Query("""
    SELECT COUNT(*)
    FROM Services s
    WHERE s.serviceType = "COURSE"
    AND s.graduated = true
        """)
Integer graduatedUsr();

}
