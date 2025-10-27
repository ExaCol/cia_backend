package CIA.app.controllers;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import CIA.app.model.Usr;
import CIA.app.services.EmailService;
import CIA.app.services.UsrService;

@RestController
@RequestMapping("/token")
public class TokenController {
    private EmailService emailService;
    private UsrService usrService;

    public TokenController(EmailService emailService, UsrService usrService) {
        this.emailService = emailService;
        this.usrService = usrService;
    }

    @GetMapping("/obtener-token/{correo}")
    public ResponseEntity<Map<String, String>> generacionToken(@PathVariable String correo) {
        Usr usr = usrService.findByEmail(correo);
        if (usr != null) {
            String token = usrService.generarToken(correo);
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("token", token);
            String mensajeCorreo = "Hola, " + usr.getName() + ".\n\n"
                    + "Has solicitado un token de verificación. Por favor, utiliza el siguiente para continuar con el proceso:\n\n"
                    + token + "\n\n"
                    + "Este token es válido por 5 minutos. Si no has solicitado un token de verificación, ignora este mensaje.\n\n"
                    + "Saludos,\n"
                    + "El equipo de SmartTraffic.";
            emailService.enviarCorreo(correo, "Token de verificación", mensajeCorreo);
            return ResponseEntity.ok(respuesta);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/verify-token/{token}/{correo}")
    public ResponseEntity<Map<String, String>> verifyToken(@PathVariable String token, @PathVariable String correo) {
        boolean isValid = usrService.verificarToken(token, correo);
        if (!isValid) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("respuesta", "Token Validado Correctamente");
        return ResponseEntity.ok(respuesta);
    }
}
