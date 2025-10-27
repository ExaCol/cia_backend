package CIA.app.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CIA.app.model.Usr;

public interface UsrRepository extends JpaRepository<Usr, Integer> {
    Usr findByEmail(String email);

    Usr findByIdentification(String identification);

    Optional<Usr> findById(Integer id);

    @Query("select u from Usr u join u.courses c where c.id = :courseId")
    List<Usr> findAllByCourseId(@Param("courseId") Integer courseId);
}
