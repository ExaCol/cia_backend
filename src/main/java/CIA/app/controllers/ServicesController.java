package CIA.app.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import CIA.app.components.JwtUtil;
import CIA.app.model.Services;
import CIA.app.services.ServicesService;
import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping("/services")
public class ServicesController {
    @Autowired
    private ServicesService servicesService;
    @Autowired
    private JwtUtil jwtUtil;

    public ServicesController(ServicesService servicesService, JwtUtil jwtUtil) {
        this.servicesService = servicesService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createServices(@RequestHeader("Authorization") String authHeader,
            @RequestBody Services services) {
        String token = authHeader.replace("Bearer ", "");

        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    Services s = servicesService.createServices(email, services);
                    if (s == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuario no existe");
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body("Servicio guardado exitosamente");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al registrar servicio: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }

    }

    @GetMapping("/byUser")
    public ResponseEntity<?> getByUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    List<Services> s = servicesService.getServicesByUser(email);
                    if (s == null || s.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay servicios registrados");
                    }
                    return ResponseEntity.ok(s);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener servicios: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @GetMapping("/specificServices/{serviceId}")
    public ResponseEntity<?> getService(@RequestHeader("Authorization") String authHeader,
            @PathVariable Integer serviceId) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    Services s = servicesService.getSpecificServices(serviceId);
                    if (s == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se ha encontrado el servicio");
                    }
                    return ResponseEntity.ok(s);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener el servicio: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");

        }
    }

    @DeleteMapping("/specificService")
    public ResponseEntity<?> deleteSpecificService(@RequestHeader("Authorization") String authHeader,
            @RequestBody Services services) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    Services s = servicesService.deleteEspecificServices(services);
                    if (s == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Servicio no encontrado");
                    }
                    return ResponseEntity.ok("Servicio eliminado exitosamente");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener servicio: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }
}
