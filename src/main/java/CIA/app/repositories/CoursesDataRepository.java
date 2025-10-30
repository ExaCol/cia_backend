package CIA.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import CIA.app.model.CoursesData;

public interface CoursesDataRepository extends JpaRepository<CoursesData, Integer> {

    @Query("""
            SELECT c
            FROM CoursesData c join c.usrs u
            WHERE u.id = :userId
            """)
    List<CoursesData> getCoursesByUser(@PathVariable("userId") Integer userId);

    Optional<CoursesData> findById(Integer id);

    @Query("""
        SELECT c.price
        FROM CoursesData c
        WHERE c.type = :courseType AND c.onQueue = false AND c.isFull = false
    """)
    int priceByCourseType(@PathVariable("courseType") String courseType);

    @Query("""
        SELECT c.id
        FROM CoursesData c
        WHERE c.type = :courseType AND c.onQueue = false AND c.isFull = false
    """)
    int idByCourseType(@PathVariable("courseType") String courseType);

    @Query("""
        SELECT c
        FROM CoursesData c
        WHERE c.onQueue = false AND c.isFull = false
    """)
    List<CoursesData> findAvailableCourses();

    @Query("""
        SELECT c
        FROM CoursesData c
        WHERE c.onQueue = true AND c.type = :courseType
    """)
    CoursesData getCourseOnQueue(@PathVariable("courseType") String courseType);
}
