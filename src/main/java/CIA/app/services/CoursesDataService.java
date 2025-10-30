package CIA.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CIA.app.model.CoursesData;
import CIA.app.model.Usr;
import CIA.app.repositories.CoursesDataRepository;
import CIA.app.repositories.UsrRepository;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;

@Service
public class CoursesDataService {

    @Autowired
    private CoursesDataRepository coursesDataRepository;
    @Autowired
    private UsrService usrService;
    @Autowired
    private UsrRepository usrRepository;
    private final EmailService emailService;

    public CoursesDataService(CoursesDataRepository coursesDataRepository, UsrService usrService,
            UsrRepository usrRepository, EmailService emailService) {
        this.coursesDataRepository = coursesDataRepository;
        this.usrService = usrService;
        this.usrRepository = usrRepository;
        this.emailService = emailService;
    }

    public CoursesData createCourse(String email, CoursesData coursesData) {
        Usr user = usrService.findByEmail(email);
        if (user != null) {
            if (coursesData.getCapacity() > 0)
                return coursesDataRepository.save(coursesData);
        }
        return null;
    }

    public List<Usr> getUsersByCourse(String email, Integer courseId) {
        Usr user = usrService.findByEmail(email);
        if (user != null) {
            Optional<CoursesData> course = coursesDataRepository.findById(courseId);
            if (!course.isEmpty()) {
                List<Usr> courseUsers = course.get().getUsrs();
                return courseUsers;
            }
            return null;
        }
        return null;
    }

    public CoursesData getSpeficifCourse(Integer courseId) {
        Optional<CoursesData> course = coursesDataRepository.findById(courseId);
        return course.orElse(null);
    }

    public CoursesData deleteCourse(CoursesData coursesData) {
        CoursesData course = getSpeficifCourse(coursesData.getId());
        if (course != null) {
            coursesDataRepository.delete(course);
            return course;
        }
        return null;
    }

    public CoursesData updateCourse(String email, CoursesData coursesData) {
        Usr usr = usrService.findByEmail(email);
        if (usr != null) {
            CoursesData sCourse = getSpeficifCourse(coursesData.getId());
            if (sCourse.getId().equals(coursesData.getId())) {
                if (sCourse.getCapacity() > 0 && sCourse.getName() != null) {
                    sCourse.setName(coursesData.getName());
                    sCourse.setParcialCapacity(coursesData.getParcialCapacity());
                    sCourse.setCapacity(coursesData.getCapacity());
                    sCourse.setUsrs(coursesData.getUsrs());
                    return coursesDataRepository.save(sCourse);
                }
            }
            return null;
        }

        return null;
    }

    public List<CoursesData> getAllCourses(String email) {
        Usr usr = usrService.findByEmail(email);
        if (usr != null) {
            return coursesDataRepository.findAvailableCourses();
        }
        return null;
    }

    public String enroll(Integer userId, Integer courseId) {
        CoursesData courseOpt = coursesDataRepository.findById(courseId).get();
        Usr userOpt = usrRepository.findById(userId).get();
        if (courseOpt != null && userOpt != null && userOpt.getRole().equals("Cliente")) {
            boolean alreadyEnrolled = userOpt.getCourses().stream().anyMatch(c -> c.getId().equals(courseOpt.getId()));
            if (alreadyEnrolled) {
                return null;
            }
            if ((courseOpt.getParcialCapacity() + 1) <= courseOpt.getCapacity()) {
                userOpt.getCourses().add(courseOpt);
                usrRepository.save(userOpt);
                courseOpt.setParcialCapacity(courseOpt.getParcialCapacity() + 1);
                coursesDataRepository.save(courseOpt);
                return "Cliente inscrito a curso exitosamente";
            } else{
                courseOnQueue(courseOpt, userOpt);
                return "Curso lleno, el cliente ha sido puesto en la cola de espera";
            }
        }
        return null;
    }

    private void courseOnQueue(CoursesData fullCourse, Usr userOpt) {
        CoursesData searchCourseOnQueue = coursesDataRepository.getCourseOnQueue(fullCourse.getType());
        CoursesData courseOnQueue = new CoursesData();
        if (searchCourseOnQueue != null) {
            //Ya se hay curso creado, se iguala el curso encontrado uno nuevo
            courseOnQueue = searchCourseOnQueue;
            if(searchCourseOnQueue.getParcialCapacity() + 1 > 5){
                //Ya hay más de 5 interesados, poner en público este curso
                courseOnQueue.setOnQueue(false);
                fullCourse.setFull(true);
                coursesDataRepository.save(fullCourse);
                coursesDataRepository.save(courseOnQueue);
                notifyUsersCourseAvailable(courseOnQueue);
                //Acá hacer lógica de envio de correo a los usuarios del curso
                return;

            }
            //No entró al if porque no ha llegado al mínimo de interesados
            courseOnQueue.setParcialCapacity(courseOnQueue.getParcialCapacity() + 1);
            courseOnQueue.getUsrs().add(userOpt);
            coursesDataRepository.save(courseOnQueue);
        } else{
            CoursesData newCourse = fullCourse;
            newCourse.setOnQueue(true);
            newCourse.setFull(false);
            newCourse.setParcialCapacity(1);
            newCourse.getUsrs().clear();
            newCourse.getUsrs().add(userOpt);
            coursesDataRepository.save(newCourse);
        }
    }

    private void notifyUsersCourseAvailable(CoursesData course) {
        String asunto = "";
        String mensaje = "";

        for(Usr user : course.getUsrs()){
            asunto = "Curso Disponible: " + course.getName();
            mensaje = "Hola " + user.getName() + ",\n" +
                      "Nos complace informarte que el curso '" + course.getName() + "' obtuvo el número minimo de cupos y fue abierto🎉.\n" +
                      "👉Acércate a nuestra sucursal.\n" +
                      "Si no reconoces este pago, contáctanos de inmediato respondiendo a este correo.\n" +
                      "Saludos,\n" +
                      "El equipo de SmartTraffic";
            emailService.enviarCorreo(user.getEmail(), asunto, mensaje);
        }

    }

    @Transactional
    public String unroll(Integer userId, Integer courseId) {
        CoursesData courseOpt = coursesDataRepository.findById(courseId).get();
        Usr userOpt = usrRepository.findById(userId).get();
        if (courseOpt != null && userOpt != null && userOpt.getRole().equals("Cliente")) {
            boolean alreadyEnrolled = userOpt.getCourses().stream().anyMatch(c -> c.getId().equals(courseOpt.getId()));
            if (!alreadyEnrolled) {
                return null;
            }
            userOpt.getCourses().remove(courseOpt);
            usrRepository.save(userOpt);
            courseOpt.setParcialCapacity(courseOpt.getParcialCapacity() - 1);
            coursesDataRepository.save(courseOpt);
            return "Cliente desinscrito de curso exitosamente";
        }
        return null;
    }

    public List<Usr> getUsersByCourseId(Integer courseId) {
        boolean courseExists = coursesDataRepository.existsById(courseId);
        if (!courseExists) {
            return null;
        }
        return usrRepository.findAllByCourseId(courseId);
    }
}
