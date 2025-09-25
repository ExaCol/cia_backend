package CIA.app.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import CIA.app.model.Token;
import CIA.app.model.Usr;
import CIA.app.repositories.TokenRepository;
import CIA.app.repositories.UsrRepository;

@Service
public class UsrService {
    @Autowired
    private UsrRepository usrRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenRepository tokenRepository;

    public UsrService(UsrRepository usrRepository, PasswordEncoder passwordEncoder, TokenRepository tokenRepository) {
        this.usrRepository = usrRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
    }

    public Usr registUser(Usr user) {
        Usr existingUser = usrRepository.findByEmail(user.getEmail());
        Usr existingIdentification = usrRepository.findByIdentification(user.getIdentification());
        if (existingUser != null || existingIdentification != null) {
            return null;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usrRepository.save(user);
    }

    public Usr login(String email, String password) {
        Usr user = usrRepository.findByEmail(email);
        if (user != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public Usr findByEmail(String email) {
        return usrRepository.findByEmail(email);
    }

    public Usr deleteByEmail(String email) {
        Usr user = usrRepository.findByEmail(email);
        if (user != null) {
            usrRepository.delete(user);
            return user;
        }
        return null;
    }

    public Usr update(String currentEmail, Usr req) {
        Usr user = usrRepository.findByEmail(currentEmail);
        if (user != null) {
            if (!currentEmail.equals(req.getEmail())) {
                Usr comNemail = usrRepository.findByEmail(req.getEmail());
                if (comNemail == null) {
                    user.setEmail(req.getEmail());
                }
            }
            user.setName(req.getName());
            return usrRepository.save(user);
        }
        return null;
    }

    public String generarToken(String email) {
        int tokenInt = (int) (Math.random() * 900000) + 100000;
        String token = String.valueOf(tokenInt);

        Token verificarToken = new Token();
        verificarToken.setToken(token);
        verificarToken.setEmail(email);
        verificarToken.setExpiracionToken(LocalDateTime.now().plusMinutes(5));
        tokenRepository.save(verificarToken);
        return token;
    }

    public boolean verificarToken(String token, String correo) {
        Token verificarToken = tokenRepository.buscarPorToken(token, correo);
        if (verificarToken == null) {
            return false;
        }
        if (verificarToken.getExpiracionToken().isBefore(LocalDateTime.now())) {
            return false;
        }
        tokenRepository.eliminarToken(token);
        return true;
    }

    public Usr changePassword(Usr usr) {
        Usr user = usrRepository.findByEmail(usr.getEmail());
        if (user != null) {
            String hashedPassword = BCrypt.hashpw(usr.getPassword(), BCrypt.gensalt(12));
            user.setPassword(hashedPassword);
            return usrRepository.save(user);
        }
        return null;
    }

    public Usr updatePassword(String email, String currentPassword, String newPassword) {
        Usr user = usrRepository.findByEmail(email);
        if (user != null) {
            if (BCrypt.checkpw(currentPassword, user.getPassword())) {
                String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
                user.setPassword(hashedNewPassword);
                return usrRepository.save(user);
            }
        }
        return null;
    }
}
