package CIA.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CIA.app.model.Usr;
import java.util.List;
import java.util.Optional;


public interface UsrRepository extends JpaRepository<Usr, Integer> {
    Usr findByEmail(String email);
    Usr findByIdentification(String identification);
    Optional<Usr> findById(Integer id);
}
