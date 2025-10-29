package CIA.app.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CIA.app.model.Services;

public interface ServicesRepository extends JpaRepository<Services, Integer>{
    @Query("""
           select s
           from Services s
           where s.usr.id = :id
           """)
    List<Services> getServicesByUser(@Param("id") Integer id);

    @Query("""
           select s
           from Services s
           where s.serviceType = :type
           """)
    List<Services> getServicesByType(@Param("type") String type);

}
