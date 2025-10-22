package CIA.app.repositories;

import java.util.List;

//import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CIA.app.model.Partner;

public interface PartnerRepository extends JpaRepository<Partner, Integer>{

    /*@Query("""
        select p
        from Partner p
        where p.service.id = :id
        """)
    List<Partner> getPartnersByServices(@Param("id") Integer id);*/

    @Query(
        """
          SELECT p
          FROM Partner p
          WHERE p.soat = true      
        """)
    List<Partner> getPartnersBySoat();

    @Query(
        """
          SELECT p
          FROM Partner p
          WHERE p.techno = true      
        """)
    List <Partner> getPartnersByTechno();

    @Query(
      """
          SELECT p
          FROM Partner p
          WHERE p.name = 'CIA'     
        """)
        List <Partner> getCIA();

}
