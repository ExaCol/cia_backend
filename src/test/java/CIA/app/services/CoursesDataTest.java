package CIA.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import CIA.app.model.CoursesData;
import CIA.app.model.Usr;
import CIA.app.repositories.CoursesDataRepository;
import CIA.app.repositories.UsrRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CoursesDataTest {

    @Mock private CoursesDataRepository coursesDataRepository;
    @Mock private UsrService usrService;
    @Mock private UsrRepository usrRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private CoursesDataService coursesDataService;

    private Usr makeUser(Integer id, String name) {
        Usr u = new Usr();
        u.setId(id);
        u.setName(name);
        u.setCourses(new ArrayList<>());
        return u;
    }

    private CoursesData makeCourse(Integer id, String name, String type, int price, int capacity, int parcialCapacity, boolean onQueue, boolean full) {
        CoursesData c = new CoursesData();
        c.setId(id);
        c.setName(name);
        c.setType(type);
        c.setPrice(price);
        c.setCapacity(capacity);
        c.setParcialCapacity(parcialCapacity);
        c.setOnQueue(onQueue);
        c.setFull(full);
        c.setUsrs(new ArrayList<>());
        return c;
    }

    @Test
    void createCourse_ok() {
        String email = "usermail@exa.co";
        Usr admin = makeUser(1, "TestUser");

        CoursesData course = makeCourse(1, "Curso de Conducción", "A1", 100000, 25, 0, false, false);
        when(usrService.findByEmail(email)).thenReturn(admin);
        when(coursesDataRepository.save(course)).thenReturn(course);

        CoursesData createCourse = coursesDataService.createCourse(email, course);

        assertNotNull(createCourse);
        assertEquals(course.getId(), createCourse.getId());
        
    }

    @Test
    void getUsersByCourse_ok() {
        String email = "usermail@exa.co";
        Usr admin = makeUser(1, "TestUser");
        CoursesData course = makeCourse(1, "Curso de Conducción", "A1", 100000, 25, 0, false, false);
        Usr u1 = makeUser(2, "Test02");
        Usr u2 = makeUser(3, "Test03");
        course.getUsrs().addAll(List.of(u1, u2));

        when(usrService.findByEmail(email)).thenReturn(admin);
        when(coursesDataRepository.findById(1)).thenReturn(Optional.of(course));

        List<Usr> getUsersByCourse = coursesDataService.getUsersByCourse(email, 1);

        assertNotNull(getUsersByCourse);
        assertEquals(2, getUsersByCourse.size());
        assertEquals("Test02", getUsersByCourse.get(0).getName());
    }

    @Test
    void getSpecificCourse_ok() {
        CoursesData course = makeCourse(1, "Curso de Conducción", "A1", 100000, 25, 0, false, false);
        when(coursesDataRepository.findById(30)).thenReturn(Optional.of(course));

        CoursesData getSpeficifCourse = coursesDataService.getSpeficifCourse(30);

        assertNotNull(getSpeficifCourse);
        assertEquals(1, getSpeficifCourse.getId());
    }

    @Test
    void deleteCourse_ok() {
        CoursesData course = makeCourse(1, "Curso de Conducción", "A1", 100000, 25, 0, false, false);
        when(coursesDataRepository.findById(1)).thenReturn(Optional.of(course));

        CoursesData deleteCourse = coursesDataService.deleteCourse(course);

        assertEquals(1, deleteCourse.getId());
        verify(coursesDataRepository, times(1)).delete(course);
        verifyNoMoreInteractions(coursesDataRepository);
    }

    @Test
    void updateCourse_ok() {
        String email = "usermail@exa.co";
        Usr usr = makeUser(1, "TestUser");

        CoursesData course1 = makeCourse(1, "Curso de Conducción", "A1", 100000, 25, 0, false, false);
        CoursesData course2 = makeCourse(1, "Curso de Conducción", "A1", 110000, 30, 0, false, false);
        course2.setUsrs(List.of(makeUser(2, "Cliente")));

        when(usrService.findByEmail(email)).thenReturn(usr);
        when(coursesDataRepository.findById(1)).thenReturn(Optional.of(course1));
        when(coursesDataRepository.save(course1)).thenReturn(course2);

        CoursesData updateCourse = coursesDataService.updateCourse(email, course1);

        assertEquals(course2.getName(), updateCourse.getName());
        assertEquals(30, updateCourse.getCapacity());
        assertEquals(0, updateCourse.getParcialCapacity());
        assertEquals(1, updateCourse.getUsrs().size());
    }

    
    @Test
    void getAllCourses_ok() {
        String email = "usermail@exa.co";
        Usr usr = makeUser(1, "TestUser");

        CoursesData course1 = makeCourse(1, "Curso de Conducción", "A1", 100000, 25, 0, false, false);
        CoursesData course2 = makeCourse(1, "Curso de Conducción", "B1", 110000, 30, 0, false, false);

        when(usrService.findByEmail(email)).thenReturn(usr);
        when(coursesDataRepository.findAvailableCourses()).thenReturn(List.of(course1, course2));

        List<CoursesData> getAllCourses = coursesDataService.getAllCourses(email);

        assertNotNull(getAllCourses);
        assertEquals(2, getAllCourses.size());
    }

    //Este test es pa inscripción normal
    @Test
    void enroll_ok_CaseNormalCapacity() {
        Integer userId = 1;
        Integer courseId = 2;

        Usr usr = makeUser(userId, "TestUser");
        usr.setRole("Cliente");
        CoursesData course1 = makeCourse(courseId, "Curso de Conducción", "A1", 100000, 25, 0, false, false);

        when(coursesDataRepository.findById(courseId)).thenReturn(Optional.of(course1));
        when(usrRepository.findById(userId)).thenReturn(Optional.of(usr));
        when(usrRepository.save(any(Usr.class))).thenAnswer(inv -> inv.getArgument(0));
        when(coursesDataRepository.save(any(CoursesData.class))).thenAnswer(inv -> inv.getArgument(0));

        String enroll = coursesDataService.enroll(userId, courseId);

        assertEquals("Cliente inscrito a curso exitosamente", enroll);

        assertTrue(usr.getCourses().contains(course1));
        assertEquals(1, course1.getParcialCapacity());
        verify(usrRepository).save(usr);
        verify(coursesDataRepository).save(course1);
        verifyNoInteractions(emailService);
    }

    //Primera vez que se va a encolar
    @Test
    void enroll_ok_CaseFirstTimeQueue() {
        Integer userId = 1;
        Integer courseId = 1;

        CoursesData fullCourse = makeCourse(courseId, "Curso de Conducción", "A1", 100000, 25, 25, false, false);
        Usr usr = makeUser(userId, "TestUser");
        usr.setRole("Cliente");
        when(coursesDataRepository.findById(courseId)).thenReturn(Optional.of(fullCourse));
        when(usrRepository.findById(userId)).thenReturn(Optional.of(usr));

        when(coursesDataRepository.getCourseOnQueue("A1")).thenReturn(null);

        ArgumentCaptor<CoursesData> courseCaptor = ArgumentCaptor.forClass(CoursesData.class);
        when(coursesDataRepository.save(courseCaptor.capture())).thenAnswer(inv -> {
            CoursesData saved = inv.getArgument(0);
            if (saved.getId() == null) saved.setId(999); 
            return saved;
        });
        when(usrRepository.save(any(Usr.class))).thenAnswer(inv -> inv.getArgument(0));

        String enroll = coursesDataService.enroll(userId, courseId);

        assertNull(enroll); 

        CoursesData createdQueueCourse = courseCaptor.getAllValues().stream()
                .filter(c -> Boolean.TRUE.equals(c.isOnQueue()))
                .findFirst()
                .orElse(null);

        assertNotNull(createdQueueCourse);
        assertEquals("A1", createdQueueCourse.getType());
        assertTrue(createdQueueCourse.isOnQueue());
        assertFalse(createdQueueCourse.isFull());
        assertEquals(1, createdQueueCourse.getParcialCapacity());
        assertEquals(1, createdQueueCourse.getUsrs().size());
        assertEquals(userId, createdQueueCourse.getUsrs().get(0).getId());

        assertTrue(usr.getCourses().contains(createdQueueCourse));

        verify(usrRepository).save(usr);
        verifyNoInteractions(emailService);
    }

    //Se alcanzó el número de interesados >=5
    @Test
    void enroll_ok_CaseReachMinimum() {
        Integer userId = 1;
        Integer courseId = 1;

        CoursesData fullCourse = makeCourse(courseId, "Curso de Conducción", "A1", 100000, 25, 25, false, false);
        //Usr usr = makeUser(userId, "TestUser");

        CoursesData courseQueue = makeCourse(courseId, "Curso de Conducción", "A1", 100000, 25, 4, true, false);
        Usr u1 = makeUser(2, "TestUser02");
        u1.setRole("Cliente");
        Usr u2 = makeUser(3, "TestUser03");
        u2.setRole("Cliente");
        Usr u3 = makeUser(4, "TestUser04");
        u3.setRole("Cliente");
        Usr u4 = makeUser(5, "TestUser05");
        u4.setRole("Cliente");
        courseQueue.getUsrs().addAll(List.of(u1, u2, u3, u4));

        
        Usr user = makeUser(6, "FinalTestUser");
        user.setRole("Cliente");

        when(coursesDataRepository.findById(courseId)).thenReturn(Optional.of(fullCourse));
        when(usrRepository.findById(userId)).thenReturn(Optional.of(user));
        when(coursesDataRepository.getCourseOnQueue("A1")).thenReturn(courseQueue);

        when(usrRepository.save(any(Usr.class))).thenAnswer(inv -> inv.getArgument(0));
        when(coursesDataRepository.save(any(CoursesData.class))).thenAnswer(inv -> inv.getArgument(0));

        String enroll = coursesDataService.enroll(userId, courseId);

        assertNull(enroll);

        assertFalse(courseQueue.isOnQueue());
        assertTrue(fullCourse.isFull());
        assertEquals(5, courseQueue.getParcialCapacity());
        assertTrue(user.getCourses().contains(courseQueue));
        
    }

    @Test
    void unroll_ok() {
        Integer userId = 1;
        Integer courseId = 1;

        CoursesData course = makeCourse(courseId, "Curso de Conducción", "A1", 100000, 25, 25, false, false);
        Usr usr = makeUser(userId, "TestUser");
        usr.setRole("Cliente");
        usr.getCourses().add(course);

        when(coursesDataRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(usrRepository.findById(userId)).thenReturn(Optional.of(usr));
        when(usrRepository.save(any(Usr.class))).thenAnswer(inv -> inv.getArgument(0));
        when(coursesDataRepository.save(any(CoursesData.class))).thenAnswer(inv -> inv.getArgument(0));

        String unroll = coursesDataService.unroll(userId, courseId);

        assertEquals("Cliente desinscrito de curso exitosamente", unroll);
        assertFalse(usr.getCourses().contains(course));
        assertEquals(24, course.getParcialCapacity());
        verify(usrRepository).save(usr);
        verify(coursesDataRepository).save(course);
    }

    @Test
    void getUsersByCourseId_ok() {
        Integer courseId = 1;
        when(coursesDataRepository.existsById(courseId)).thenReturn(true);

        Usr u1 = makeUser(1, "TestUser01");
        Usr u2 = makeUser(2, "TestUser02");
        when(usrRepository.findAllByCourseId(courseId)).thenReturn(List.of(u1, u2));

        List<Usr> getUsersByCourseId = coursesDataService.getUsersByCourseId(courseId);

        assertNotNull(getUsersByCourseId);
        assertEquals(2, getUsersByCourseId.size());
        
    }
}
