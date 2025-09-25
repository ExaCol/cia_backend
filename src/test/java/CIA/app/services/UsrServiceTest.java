package CIA.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import CIA.app.model.Usr;
import CIA.app.repositories.UsrRepository;

@ExtendWith(MockitoExtension.class)
public class UsrServiceTest {
    @Mock
    private UsrRepository usrRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsrService usrService;

    @BeforeEach
    void setup() {
    }

    private Usr makeUser(String email, String identification, String name) {
        Usr u = new Usr();
        u.setId(1);
        u.setEmail(email);
        u.setIdentification(identification);
        u.setName(name);
        u.setPassword("raw");
        return u;
    }

    @Test
    void registUser_success() {
        Usr newUser = makeUser("new@exa.co", "CC123", "New User");
        newUser.setPassword("plain");

        when(usrRepository.findByEmail("new@exa.co")).thenReturn(null);
        when(usrRepository.findByIdentification("CC123")).thenReturn(null);
        when(passwordEncoder.encode("plain")).thenReturn("encoded-pass");
        when(usrRepository.save(any(Usr.class))).thenAnswer(inv -> inv.getArgument(0));

        Usr out = usrService.registUser(newUser);

        assertNotNull(out);
        assertEquals("encoded-pass", out.getPassword());
        verify(usrRepository).findByEmail("new@exa.co");
        verify(usrRepository).findByIdentification("CC123");
        verify(passwordEncoder).encode("plain");
        verify(usrRepository).save(any(Usr.class));
    }

    @Test
    void login_success() {
        String email = "login@exa.co";
        String plain = "secret";
        String hashed = new BCryptPasswordEncoder().encode(plain);

        Usr dbUser = makeUser(email, "ID1", "Login User");
        dbUser.setPassword(hashed);

        when(usrRepository.findByEmail(email)).thenReturn(dbUser);

        Usr out = usrService.login(email, plain);

        assertNotNull(out);
        assertEquals(email, out.getEmail());
        verify(usrRepository).findByEmail(email);
    }

    @Test
    void findByEmail_success() {

        Usr dbUser = makeUser("find@exa.co", "ID2", "Find User");
        when(usrRepository.findByEmail("find@exa.co")).thenReturn(dbUser);

        Usr out = usrService.findByEmail("find@exa.co");

        assertNotNull(out);
        assertEquals("find@exa.co", out.getEmail());
        verify(usrRepository).findByEmail("find@exa.co");
    }

    @Test
    void deleteByEmail_success() {
        String email = "del@exa.co";
        Usr dbUser = makeUser(email, "ID3", "Del User");
        when(usrRepository.findByEmail(email)).thenReturn(dbUser);

        Usr out = usrService.deleteByEmail(email);

        assertNotNull(out);
        assertEquals(email, out.getEmail());
        verify(usrRepository).findByEmail(email);
        verify(usrRepository).delete(dbUser);
    }

    @Test
    void update_success_changeNameAndEmailNotTaken() {
        String currentEmail = "old@exa.co";
        String newEmail = "new@exa.co";

        Usr existing = makeUser(currentEmail, "ID4", "Old Name");
        when(usrRepository.findByEmail(currentEmail)).thenReturn(existing);
        when(usrRepository.findByEmail(newEmail)).thenReturn(null); // email disponible

        Usr req = new Usr();
        req.setEmail(newEmail);
        req.setName("New Name");

        when(usrRepository.save(any(Usr.class))).thenAnswer(inv -> inv.getArgument(0));

        Usr out = usrService.update(currentEmail, req);

        assertNotNull(out);
        assertEquals("New Name", out.getName());
        assertEquals(newEmail, out.getEmail());
        verify(usrRepository).findByEmail(currentEmail);
        verify(usrRepository).findByEmail(newEmail);
        verify(usrRepository).save(existing);
    }

    @Test
    void changePassword_success() {
        String email = "cp@exa.co";
        Usr dbUser = makeUser(email, "ID5", "Cp User");
        dbUser.setPassword(new BCryptPasswordEncoder().encode("old"));

        when(usrRepository.findByEmail(email)).thenReturn(dbUser);
        ArgumentCaptor<Usr> captor = ArgumentCaptor.forClass(Usr.class);
        when(usrRepository.save(any(Usr.class))).thenAnswer(inv -> inv.getArgument(0));

        Usr req = new Usr();
        req.setEmail(email);
        req.setPassword("newSecret");

        Usr out = usrService.changePassword(req);

        assertNotNull(out);
        verify(usrRepository).save(captor.capture());
        Usr saved = captor.getValue();
        assertTrue(BCrypt.checkpw("newSecret", saved.getPassword()), "El password guardado debe ser el hash de 'newSecret'");
    }

    @Test
    void updatePassword_success() {
        String email = "up@exa.co";
        String current = "currPwd";
        String next = "nextPwd";

        Usr dbUser = makeUser(email, "ID6", "Up User");
        dbUser.setPassword(BCrypt.hashpw(current, BCrypt.gensalt(12))); // hash almacenado del password actual

        when(usrRepository.findByEmail(email)).thenReturn(dbUser);
        when(usrRepository.save(any(Usr.class))).thenAnswer(inv -> inv.getArgument(0));
        ArgumentCaptor<Usr> captor = ArgumentCaptor.forClass(Usr.class);

        Usr out = usrService.updatePassword(email, current, next);

        assertNotNull(out);
        verify(usrRepository).save(captor.capture());
        Usr saved = captor.getValue();
        assertTrue(BCrypt.checkpw(next, saved.getPassword()), "El password guardado debe ser el hash de 'next'");
    }
}
