package CIA.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import CIA.app.model.Usr;

public interface UsrRepository extends JpaRepository<Usr, Integer> {
    Usr findByEmail(String email);
    Usr findByIdentification(String identification);
    Optional<Usr> findById(Integer id);
}
