package CIA.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CIA.app.repositories.UsrRepository;

@Service
public class UsrService {
    @Autowired
    private UsrRepository usrRepository;

    public UsrService(UsrRepository usrRepository) {
        this.usrRepository = usrRepository;
    }
}
