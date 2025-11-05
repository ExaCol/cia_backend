package CIA.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import CIA.app.model.CoursesData;
import CIA.app.model.Partner;
import CIA.app.model.Services;
import CIA.app.model.Usr;
import CIA.app.repositories.CoursesDataRepository;
import CIA.app.repositories.PartnerRepository;
import CIA.app.repositories.ServicesRepository;
import CIA.app.repositories.TokenRepository;
import CIA.app.repositories.UsrRepository;

@ExtendWith(MockitoExtension.class)
public class UsrServiceTest {
    @Mock
    private UsrRepository usrRepository;

    @Mock
    private CoursesDataRepository coursesDataRepository;

    @Mock
    private ServicesRepository servicesRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private UsrService usrService;

    @BeforeEach
    void setup() {
    }

    private static Usr makeUser(String email, String identification, String name) {
        Usr u = new Usr();
        u.setId(1);
        u.setEmail(email);
        u.setIdentification(identification);
        u.setName(name);
        u.setPassword("raw");
        u.setLat(4.5940736);
        u.setLon(-74.1081088);
        ;
        return u;
    }

    private static CoursesData makeCourse(int id, String name, int parcialCapacity, int capacity) {
        CoursesData c = new CoursesData();
        c.setId(id);
        c.setName(name);
        c.setParcialCapacity(parcialCapacity);
        c.setCapacity(capacity);
        return c;
    }

    // --- NUEVO: helper para crear Services apuntando a un Partner ---
    private static Services makeService(int id, String type, Partner partner) {
        Services s = new Services();
        s.setId(id);
        s.setServiceType(type);
        s.setPartner(partner);
        return s;
    }

    // --- Helper ya propuesto antes para Partner (si no lo añadiste, agrégalo) ---
    private static Partner makePartner(int id, String name, double lat, double lon, boolean soat, boolean techno) {
        Partner p = new Partner();
        p.setId(id);
        p.setName(name);
        p.setLat(lat);
        p.setLon(lon);
        p.setSoat(soat);
        p.setTechno(techno);
        return p;
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
        assertTrue(BCrypt.checkpw("newSecret", saved.getPassword()),
                "El password guardado debe ser el hash de 'newSecret'");
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

    @Test
    void getAllCourses_success() {
        String email = "exatest@gmail.com";

        Usr simulatedUsr = makeUser(email, "CC1021", "Exa");
        List<CoursesData> simulatedCourses = List.of(
                makeCourse(1, "Curso A", 0, 20),
                makeCourse(2, "Curso B", 0, 25));

        when(usrRepository.findByEmail(email)).thenReturn(simulatedUsr);
        when(coursesDataRepository.findAll()).thenReturn(simulatedCourses);

        List<CoursesData> outCourses = usrService.getAllCourses(email);

        assertNotNull(outCourses);
        assertEquals(simulatedCourses, outCourses);
        verify(usrRepository).findByEmail(email);
        verify(coursesDataRepository).findAll();

    }

    @Test
    void getCoursesByUser_success() {
        String email = "exatest@gmail.com";

        Usr simulatedUsr = makeUser(email, "CC1021", "Exa");
        List<CoursesData> simulatedCourses = List.of(
                makeCourse(1, "Curso A", 0, 20),
                makeCourse(2, "Curso B", 0, 25));

        when(usrRepository.findByEmail(email)).thenReturn(simulatedUsr);
        when(coursesDataRepository.getCoursesByUser(simulatedUsr.getId())).thenReturn(simulatedCourses);

        List<CoursesData> outCourses = usrService.getCoursesByUser(email);

        assertNotNull(outCourses);
        assertEquals(simulatedCourses, outCourses);
        verify(usrRepository).findByEmail(email);
        verify(coursesDataRepository).getCoursesByUser(simulatedUsr.getId());

    }

    @Test
    void registerUserToCourse_success() {

        String email = "exatest@gmail.com";

        Usr simulatedUsr = makeUser(email, "CC1021", "Exa");
        simulatedUsr.setCourses(new ArrayList<>());
        CoursesData simulatedCourse = makeCourse(1, "Curso A", 0, 20);
        simulatedCourse.setUsrs(new ArrayList<>());

        // simulatedUsr.getCourses().add(simulatedCourse);
        // simulatedCourse.setParcialCapacity(simulatedCourse.getParcialCapacity() + 1);
        // simulatedCourse.getUsrs().add(simulatedUsr);

        when(usrRepository.findByEmail(email)).thenReturn(simulatedUsr);
        when(coursesDataRepository.findById(simulatedCourse.getId())).thenReturn(Optional.of(simulatedCourse));

        CoursesData outCourse = usrService.registerUserToCourse(email, simulatedCourse);

        assertNotNull(outCourse);
        assertEquals(simulatedCourse, outCourse);
        assertTrue(simulatedUsr.getCourses().contains(outCourse));
        assertEquals(1, outCourse.getParcialCapacity());

        verify(usrRepository).findByEmail(email);
        verify(coursesDataRepository).findById(simulatedCourse.getId());
        // verify(usrRepository).save(simulatedUsr);
    }

    @Test
    void deleteUserFromCourse_success() {
        String email = "exatest@gmail.com";

        Usr simulatedUsr = makeUser(email, "CC1021", "Exa");
        CoursesData simulatedCourse = makeCourse(1, "Curso A", 1, 20);
        simulatedCourse.setUsrs(new ArrayList<>());
        simulatedCourse.getUsrs().add(simulatedUsr);
        simulatedUsr.setCourses(new ArrayList<>());
        simulatedUsr.getCourses().add(simulatedCourse);

        // simulatedUsr.getCourses().add(simulatedCourse);
        // simulatedCourse.setParcialCapacity(simulatedCourse.getParcialCapacity() + 1);
        // simulatedCourse.getUsrs().add(simulatedUsr);

        when(usrRepository.findByEmail(email)).thenReturn(simulatedUsr);
        when(coursesDataRepository.findById(simulatedCourse.getId())).thenReturn(Optional.of(simulatedCourse));

        CoursesData outCourse = usrService.deleteUserFromCourse(email, simulatedCourse);

        assertNotNull(outCourse);
        assertEquals(simulatedCourse, outCourse);
        assertTrue(!simulatedUsr.getCourses().contains(outCourse));
        assertEquals(0, outCourse.getParcialCapacity());

        verify(usrRepository).findByEmail(email);
        verify(coursesDataRepository).findById(simulatedCourse.getId());
    }

    @Test
    void getPartnerByService_SOAT_success() {
        Partner a = makePartner(1, "Partner A", 4.6838, -74.13, true, false);
        Partner b = makePartner(2, "Partner B", 4.6862, -74.119, true, false);

        when(partnerRepository.getPartnersBySoat()).thenReturn(List.of(a, b));

        List<Partner> out = usrService.getPartnerByService("SOAT");

        assertNotNull(out);
        assertEquals(2, out.size());
        assertEquals(1, out.get(0).getId());
        assertEquals(2, out.get(1).getId());
        verify(partnerRepository).getPartnersBySoat();
    }

    @Test
    void getPartnerByService_TECHNO_success() {
        Partner c = makePartner(3, "Partner C", 4.6850, -74.118, false, true);

        when(partnerRepository.getPartnersByTechno()).thenReturn(List.of(c));

        List<Partner> out = usrService.getPartnerByService("TECHNO");

        assertNotNull(out);
        assertEquals(1, out.size());
        assertEquals(3, out.get(0).getId());
        verify(partnerRepository).getPartnersByTechno();
    }

    @Test
    void getPartnerByService_default_success() {
        Partner x = makePartner(9, "Partner X", 4.60, -74.09, false, false);

        when(partnerRepository.getCIA()).thenReturn(List.of(x));

        List<Partner> out = usrService.getPartnerByService("OTRO");

        assertNotNull(out);
        assertEquals(1, out.size());
        assertEquals(9, out.get(0).getId());
        verify(partnerRepository).getCIA();
    }

    @Test
    void getNearestPartner_success() {
        String email = "exatest@gmail.com";
        String type = "SOAT";
        double maxDistanceSimulated = 10280.0; // ~10.28 km

        // Usuario (coords de tu modelo)
        Usr user = makeUser(email, "CC1021", "Exa");

        // Partners candidatos (todos "SOAT")
        Partner a = makePartner(1, "Partner A", 4.6838, -74.13, true, false);
        Partner b = makePartner(2, "Partner B", 4.6862, -74.119, true, false);
        Partner c = makePartner(3, "Partner C", 4.6850, -74.118, true, false);

        when(usrRepository.findByEmail(email)).thenReturn(user);
        when(partnerRepository.getPartnersBySoat()).thenReturn(List.of(a, b, c));

        List<Partner> outPartners = usrService.getNearestPartner(email, type, maxDistanceSimulated);

        assertNotNull(outPartners);
        // Según tu caso de ejemplo: { (3,"Partner C"), (1,"Partner A") }
        assertEquals(2, outPartners.size(), "Se esperan 2 partners dentro del radio");
        assertEquals(3, outPartners.get(0).getId(), "El más cercano esperado es Partner C");
        assertEquals(1, outPartners.get(1).getId(), "El segundo esperado es Partner A");

        verify(usrRepository).findByEmail(email);
        verify(partnerRepository).getPartnersBySoat();
    }

}
