package CIA.app.controllers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CIA.app.components.JwtUtil;
import CIA.app.components.LoginAttemptService;
import CIA.app.dtos.ChangePassword;
import CIA.app.model.Usr;
import CIA.app.services.EmailService;
import CIA.app.services.UsrService;

@RestController
@RequestMapping("/usr")
public class UsrController {
    @Autowired
    private UsrService usrService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private EmailService emailService;

    public UsrController(UsrService usrService, JwtUtil jwtUtil, LoginAttemptService loginAttemptService,
            EmailService emailService) {
        this.usrService = usrService;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
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
                    String tokenG = jwtUtil.generateToken(u.getEmail(), u.getRole(), u.getId());
                    HashMap<String, String> response = new HashMap<>();
                    response.put("token", tokenG);
                    return ResponseEntity.ok(response);
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
                Usr u = usrService.deleteByEmail(email);
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
            if (loginAttemptService.isBlocked(user.getEmail())) {
                String name = usrService.findByEmail(user.getEmail()).getName();
                String mensajeCorreo = "Hola, " + name + ".\n\n"
                        + "Tu cuenta ha sido temporalmente bloqueada por 15 minutos debido a múltiples intentos fallidos de inicio de sesión.\n\n"
                        + "Por tu seguridad, no podrás acceder durante este período. Pasado el tiempo de bloqueo, podrás intentar nuevamente. "
                        + "Si olvidaste tu contraseña, utiliza la opción \"Olvidé mi contraseña\" para restablecerla.\n\n"
                        + "Si no reconoces esta actividad, te recomendamos cambiar tu contraseña.\n\n"
                        + "Saludos,\n"
                        + "El equipo de SmartTraffic.";
                emailService.enviarCorreo(user.getEmail(), "Cuenta bloqueada", mensajeCorreo);
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body("Usuario bloqueado por demasiados intentos fallidos");
            }
            if (u != null) {
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

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Usr usr) {
        Usr u = usrService.changePassword(usr);
        if (u != null) {
            return ResponseEntity.ok("Contraseña actualizada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar la contraseña");
        }
    }

     @PatchMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String authHeader, @RequestBody ChangePassword changePassword) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractUserRole(token);

        if (jwtUtil.isTokenValid(token, email)
                && ("Cliente".equals(role) || "Admin".equals(role) || "Empleado".equals(role))) {
            try {
                Usr existingUser = usrService.login(email, changePassword.getPasswordCurrent());
                if (existingUser != null) {
                    Usr updatedUser = usrService.updatePassword(email, changePassword.getPasswordCurrent(), changePassword.getPasswordNew());
                    if (updatedUser != null) {
                        return ResponseEntity.ok("Contraseña cambiada correctamente");
                    }
                }
                return ResponseEntity.badRequest().body("La contraseña actual es incorrecta");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al cambiar contraseña de usuario: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
        }
    }
}
