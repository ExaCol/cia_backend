package CIA.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import CIA.app.model.CoursesData;

public interface CoursesDataRepository extends JpaRepository<CoursesData, Integer>{

    @Query("""
            SELECT c
            FROM CoursesData c join c.usrs u
            WHERE u.id = :userId
            """)
    List<CoursesData> getCoursesByUser(@PathVariable("userId") Integer userId);

}
