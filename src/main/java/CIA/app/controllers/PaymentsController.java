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
import CIA.app.model.Payments;
import CIA.app.services.PaymentsService;
import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping("/payments")
public class PaymentsController {
    @Autowired
    private PaymentsService paymentsService;
    @Autowired
    private JwtUtil jwtUtil;

    public PaymentsController(PaymentsService paymentsService, JwtUtil jwtUtil) {
        this.paymentsService = paymentsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayments(@RequestHeader("Authorization") String authHeader,
            @RequestBody Payments payments) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {

                    Payments p = paymentsService.createPayments(email, payments);
                    if (p == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuario o servicio no existe");
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body("Pago guardado exitosamente");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al registrar pago: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado ");
        }
    }

    @GetMapping("/paymentHistory")
    public ResponseEntity<?> getPaymentHistory(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    paymentsService.getPaymentHistoryByUserId(email);
                    List<Payments> p = paymentsService.getPaymentHistoryByUserId(email);
                    if (p == null || p.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se han realizado pagos");
                    }
                    return ResponseEntity.ok(p);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener el pago: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado ");
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
                    List<Payments> p = paymentsService.getPaymentsByUser(email);
                    if (p == null || p.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay pagos registrados");
                    }
                    return ResponseEntity.ok(p);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener pagos: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");

            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado ");
        }
    }

    @GetMapping("/specificPayments/{paymentId}")
    public ResponseEntity<?> getPayment(@RequestHeader("Authorization") String authHeader,
            @PathVariable Integer paymentId) {
        String token = authHeader.replace("Bearer ", "");
        try {

            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {

                    Payments p = paymentsService.getSpecificPayments(paymentId);

                    if (p == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se ha encontrado el pago");
                    }
                    return ResponseEntity.ok(p);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener el pago: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");

            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado ");
        }
    }

    @DeleteMapping("/specificPayment")
    public ResponseEntity<?> deleteSpecificPayment(@RequestHeader("Authorization") String authHeader,
            @RequestBody Payments payments) {
        String token = authHeader.replace("Bearer ", "");
        try {

            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    Payments p = paymentsService.deleteEspecificPayments(payments);
                    if (p == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pago no encontrado");
                    }
                    return ResponseEntity.ok("Pago eliminado exitosamente");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener pago: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");

            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado ");
        }
    }
}
