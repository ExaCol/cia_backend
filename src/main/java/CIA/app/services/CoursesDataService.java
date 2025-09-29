package CIA.app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CIA.app.model.CoursesData;
import CIA.app.model.Usr;
import CIA.app.repositories.CoursesDataRepository;

@Service
public class CoursesDataService {

    @Autowired
    private final CoursesDataRepository coursesDataRepository;

    @Autowired
    private final UsrService usrService;

    public CoursesDataService(CoursesDataRepository coursesDataRepository, UsrService usrService) {
        this.coursesDataRepository = coursesDataRepository;
        this.usrService = usrService;
    }

    public CoursesData createCourse(String email, CoursesData coursesData){
        Usr user = usrService.findByEmail(email);
        if(user != null){
            if(coursesData.getCapacity() > 0 )
            return coursesDataRepository.save(coursesData);
        }
        return null;
    }

    public List<Usr> getUsersByCourse(String email, Integer courseId){
        Usr user = usrService.findByEmail(email);
        if(user != null){
            Optional<CoursesData> course = coursesDataRepository.findById(courseId);
            if (!course.isEmpty()) {
                List<Usr> courseUsers = course.get().getUsrs();
                return courseUsers;
            }
            return null;
        }
        return null;
    }

    public CoursesData getSpeficifCourse(Integer courseId){
        Optional<CoursesData> course = coursesDataRepository.findById(courseId);
        return course.orElse(null);
    }

    public CoursesData deleteCourse(CoursesData coursesData){
        CoursesData course = getSpeficifCourse(coursesData.getId());
        if (course != null) {
            coursesDataRepository.delete(course);
            return course;
        }
        return null;
    }

    public CoursesData updateCourse(String email, CoursesData coursesData){
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


}
