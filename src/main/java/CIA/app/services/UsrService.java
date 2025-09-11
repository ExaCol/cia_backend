package CIA.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import CIA.app.model.Usr;
import CIA.app.repositories.UsrRepository;

@Service
public class UsrService {
    @Autowired
    private UsrRepository usrRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsrService(UsrRepository usrRepository, PasswordEncoder passwordEncoder) {
        this.usrRepository = usrRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usr registUser(Usr user) {
        Usr existingUser = usrRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
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
        }
        return user;
    }

    public Usr update(String currentEmail, Usr req) {
        Usr user = usrRepository.findByEmail(currentEmail);
        if(user != null){
            Usr comNemail = usrRepository.findByEmail(req.getEmail());
            if(comNemail == null){
                user.setEmail(req.getEmail());
                user.setName(req.getName());
                return usrRepository.save(user);
            }
            return null;
        }
        return null;
    }
}
