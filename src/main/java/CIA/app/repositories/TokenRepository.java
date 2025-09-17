package CIA.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import CIA.app.model.Token;
import jakarta.transaction.Transactional;

public interface TokenRepository extends JpaRepository<Token, Integer>{
    @Query("SELECT vt FROM Token vt WHERE vt.token = :token AND vt.email = :correo")
    Token buscarPorToken(@Param("token") String token, @Param("correo") String correo);

    @Modifying
    @Transactional
    @Query("DELETE FROM Token WHERE token = :token")
    void eliminarToken(@Param("token") String token);
}