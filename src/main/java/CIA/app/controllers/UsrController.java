package CIA.app.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CIA.app.components.JwtUtil;
import CIA.app.components.LoginAttemptService;
import CIA.app.model.Partner;
//import CIA.app.model.Payments;
import CIA.app.model.Usr;
import CIA.app.services.UsrService;
import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping("/usr")
public class UsrController {
    @Autowired
    private UsrService usrService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private LoginAttemptService loginAttemptService;

    public UsrController(UsrService usrService, JwtUtil jwtUtil, LoginAttemptService loginAttemptService) {
        this.usrService = usrService;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usr user) {
        Usr registeredUser = usrService.registUser(user);
        if (registeredUser != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuario ya existe");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> userEndpoint(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractUserRole(token);

        if (jwtUtil.isTokenValid(token, email)
                && ("Cliente".equals(role) || "Admin".equals(role) || "Empleado".equals(role))) {
            return ResponseEntity.ok(usrService.findByEmail(email));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol USER");
        }
    }

    @PatchMapping("/user")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authHeader, @RequestBody Usr user) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractUserRole(token);

        if (jwtUtil.isTokenValid(token, email)
                && ("Cliente".equals(role) || "Admin".equals(role) || "Empleado".equals(role))) {
            try {
                Usr u = usrService.update(email, user);
                if (u != null) {
                    return ResponseEntity.ok("Usuario actualizado correctamente");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nuevo email ya está en uso");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al actualizar usuario: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractUserRole(token);

        if (jwtUtil.isTokenValid(token, email)
                && ("Cliente".equals(role) || "Admin".equals(role) || "Empleado".equals(role))) {
            try {
                Usr u =usrService.deleteByEmail(email);
                if (u == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
                }
                return ResponseEntity.ok("Usuario eliminado correctamente");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al eliminar usuario: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usr user) {
        Usr existingUser = usrService.findByEmail(user.getEmail());
        if (existingUser != null) {
            Usr u = usrService.login(user.getEmail(), user.getPassword());
            if (u != null) {
                if (loginAttemptService.isBlocked(u.getEmail())) {
                    return ResponseEntity.status(HttpStatus.LOCKED)
                            .body("Usuario bloqueado por demasiados intentos fallidos");
                }
                loginAttemptService.loginSecceeded(u.getEmail());
                String token = jwtUtil.generateToken(u.getEmail(), u.getRole(), u.getId());
                HashMap<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                loginAttemptService.loginFailed(user.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }
    }

    @GetMapping("/nearestPartner")
    public ResponseEntity<?> getPayment(@RequestHeader("Authorization") String authHeader, @RequestParam String type, @RequestParam double maxDistance) {
        String token = authHeader.replace("Bearer ", "");
        try{
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role) ) {
                try {
                    List<Partner> p = usrService.getNearestPartner(email, type, maxDistance * 1000);
                    if (p == null || p.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se han encontrado aliados con la distancia ingresada");
                    }
                    return ResponseEntity.ok(p);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener lista de aliados: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        }catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

}
