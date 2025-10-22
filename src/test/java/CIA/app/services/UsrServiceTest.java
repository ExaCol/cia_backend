package CIA.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.checkerframework.checker.units.qual.s;
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
import CIA.app.repositories.ServicesRepository;
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
        u.setLon(-74.1081088);;
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

    private static Partner makePartner(int id, String name, double lat, double lon){
        Partner p = new Partner();
        p.setId(id);
        p.setName(name);
        p.setLat(lat);
        p.setLon(lon);
        return p;
    }

    private static Services makeService(String type, Partner partners){
        Services s = new Services();
        s.setServiceType(type);
        s.setPartner(partners);
        return s;
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

    @Test
    void getAllCourses_success() {
        String email = "exatest@gmail.com";

        Usr simulatedUsr = makeUser(email, "CC1021", "Exa");
        List<CoursesData> simulatedCourses = List.of(
            makeCourse(1, "Curso A", 0, 20),
            makeCourse(2, "Curso B", 0, 25)
        );
        
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
            makeCourse(2, "Curso B", 0, 25)
        );

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
        
        //simulatedUsr.getCourses().add(simulatedCourse);
        //simulatedCourse.setParcialCapacity(simulatedCourse.getParcialCapacity() + 1);
        //simulatedCourse.getUsrs().add(simulatedUsr);

        when(usrRepository.findByEmail(email)).thenReturn(simulatedUsr);
        when(coursesDataRepository.findById(simulatedCourse.getId())).thenReturn(Optional.of(simulatedCourse));

        CoursesData outCourse = usrService.registerUserToCourse(email, simulatedCourse);

        assertNotNull(outCourse);
        assertEquals(simulatedCourse, outCourse);
        assertTrue(simulatedUsr.getCourses().contains(outCourse));
        assertEquals(1, outCourse.getParcialCapacity());

        verify(usrRepository).findByEmail(email);
        verify(coursesDataRepository).findById(simulatedCourse.getId());
        //verify(usrRepository).save(simulatedUsr);
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
        
        //simulatedUsr.getCourses().add(simulatedCourse);
        //simulatedCourse.setParcialCapacity(simulatedCourse.getParcialCapacity() + 1);
        //simulatedCourse.getUsrs().add(simulatedUsr);

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
    void getNearestPartner_success(){
        String email = "exatest@gmail.com";
        Usr simulatedUsr = makeUser(email, "CC1021", "Exa");
        String type = "SOAT";
        Double maxDistanceSimulated = 10280.0;
        
        Partner p1 = makePartner(1, "Partner A", 4.6838, -74.13); 
        Partner p2 = makePartner(2, "Partner B", 4.6862, -74.119); //Más lejano
        Partner p3 = makePartner(3, "Partner C", 4.685, -74.118); //Más cercano
        
        List<Partner> servicePartnersA = new ArrayList<>();
        servicePartnersA.add(p1);
        servicePartnersA.add(p2);

        List<Partner> servicePartnersB = new ArrayList<>();
        servicePartnersB.add(p3);

        Services s1 = makeService(type, p1);
        Services s2 = makeService(type, p2);

        when(usrRepository.findByEmail(email)).thenReturn(simulatedUsr);
        when(servicesRepository.getServicesByType(type)).thenReturn(List.of(s1, s2));

        List<Partner> outPartners = usrService.getNearestPartner(email, type, maxDistanceSimulated);

        assertNotNull(outPartners);
        assertEquals(2, outPartners.size());
        assertEquals(List.of(p3, p1), outPartners);
        verify(usrRepository).findByEmail(email);
        verify(servicesRepository).getServicesByType(type);

    }

    @Test
    void getPartnersByTypeServicesNR_success(){
        String type = "SOAT";
        
        Partner p1 = makePartner(1, "Partner A", 4.6838, -74.13); 
        Partner p2 = makePartner(2, "Partner B", 4.6862, -74.119); //Más lejano
        Partner p3 = makePartner(3, "Partner C", 4.685, -74.118);  //Más cercano
        
        List<Partner> servicePartnersA = new ArrayList<>();
        servicePartnersA.add(p1);
        servicePartnersA.add(p2);

        List<Partner> servicePartnersB = new ArrayList<>();
        servicePartnersA.add(p3);
        servicePartnersA.add(p1);

        List<Partner> servicePartnersC = new ArrayList<>();
        servicePartnersA.add(null);
        servicePartnersA.add(p2);

        Services s1 = makeService(type, p1);
        Services s2 = makeService(type, p2);
        Services s3 = makeService(type, p3);

        when(servicesRepository.getServicesByType(type)).thenReturn(List.of(s1, s2, s3));

        List<Partner> outMap = usrService.getPartnerByService(type);

        assertNotNull(outMap);
        assertEquals(3, outMap.size());
        //assertTrue(outMap.containsKey(p1.getId()));
        //assertTrue(outMap.containsKey(p2.getId()));
        //assertTrue(outMap.containsKey(p3.getId()));
        verify(servicesRepository).getServicesByType(type);
    }

    
}
