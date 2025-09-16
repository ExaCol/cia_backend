package CIA.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import CIA.app.model.LoginAttempt;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, String>{
    
}
