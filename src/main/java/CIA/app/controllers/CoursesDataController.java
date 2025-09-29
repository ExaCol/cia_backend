package CIA.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CIA.app.components.JwtUtil;
import CIA.app.model.CoursesData;
import CIA.app.model.Usr;
import CIA.app.services.CoursesDataService;
import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping("/coursesData")
public class CoursesDataController {

    @Autowired
    private CoursesDataService coursesDataService;

    @Autowired
    private JwtUtil jwtUtil;

    public CoursesDataController(CoursesDataService coursesDataService, JwtUtil jwtUtil) {
        this.coursesDataService = coursesDataService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/createCourse")
    public ResponseEntity<?> createCourse(@RequestHeader("Authorization") String authHeader, @RequestBody CoursesData coursesData) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Admin".equals(role)) {
                try {
                    CoursesData c = coursesDataService.createCourse(email, coursesData);
                    if (c == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se ha podido crear el curso");
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body("Curso guardado exitosamente");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar curso: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }

    }

    @GetMapping("/getUsersByCourse/{courseId}")
    public ResponseEntity<?> getUsersByCourse(@RequestHeader("Authorization") String authHeader, @PathVariable Integer courseId) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && ("Admin".equals(role) || "Cliente".equals(role))) {
                try {
                    List<Usr> users = coursesDataService.getUsersByCourse(email, courseId);
                    if (users == null || users.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se ha podido traer los usuarios del curso");
                    }
                    return ResponseEntity.ok(users);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar el curso: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @GetMapping("/getSpecificCourse/{courseId}")
    public ResponseEntity<?> getSpecificCourse(@RequestHeader("Authorization") String authHeader, @PathVariable Integer courseId) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && ("Admin".equals(role) || "Cliente".equals(role))) {
                try {
                    CoursesData course = coursesDataService.getSpeficifCourse(courseId);
                    if (course == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se ha podido encontrar el curso");
                    }
                    return ResponseEntity.ok(course);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar curso: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @DeleteMapping("/deleteCourse")
    public ResponseEntity<?> deleteCourse(@RequestHeader("Authorization") String authHeader, @RequestBody CoursesData coursesData) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if(jwtUtil.isTokenValid(token, email) && "Admin".equals(role)) {
                try {
                    CoursesData course = coursesDataService.deleteCourse(coursesData);
                    if (course == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Curso no encontrado");
                    }
                    return ResponseEntity.ok("Curso eliminado exitosamente");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener Curso: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        }catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @PatchMapping("/updateCourse")
    public ResponseEntity<?> updateCourse(@RequestHeader("Authorization") String authHeader, @RequestBody CoursesData coursesData) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email)
                    && ("Admin".equals(role) || "Empleado".equals(role))) {
                try {
                    CoursesData course = coursesDataService.updateCourse(email, coursesData);
                    if (course != null) {
                        return ResponseEntity.ok("Curso actualizado correctamente");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el curso");
                    }
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al actualizar curso: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        }catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @GetMapping("/getAllCourses")
    public ResponseEntity<?> getUsersByCourse(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && ("Admin".equals(role) || "Cliente".equals(role))) {
                try {
                    List<CoursesData> courses = coursesDataService.getAllCourses(email);
                    if (courses == null || courses.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se ha podido traer los cursos");
                    }
                    return ResponseEntity.ok(courses);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar los cursos: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }
}
