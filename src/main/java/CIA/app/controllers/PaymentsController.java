package CIA.app.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import CIA.app.components.JwtUtil;
import CIA.app.dtos.CheckoutRequest;
import CIA.app.dtos.CheckoutResponse;
import CIA.app.model.Payments;
import CIA.app.model.Usr;
import CIA.app.services.MercadoPagoService;
import CIA.app.services.PaymentsService;
import CIA.app.services.UsrService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentsController {
    private final JwtUtil jwtUtil;
    private final MercadoPagoService mpService;
    private final UsrService usrService;
    private final PaymentsService paymentsService;

    @PostMapping("/checkout")
    public ResponseEntity<?> createCheckout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CheckoutRequest request) {

        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                // Obtén el usuario por email (ajusta al método real)
                Usr currentUser = usrService.findByEmail(email);

                CheckoutResponse resp = mpService.createCheckout(request, currentUser);
                return ResponseEntity.ok(resp);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creando preferencia: " + e.getMessage());
        }
    }
  
    @GetMapping("/getPayments")
    public ResponseEntity<?> getPayments(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);
            Integer id = jwtUtil.extractUserId(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    List<Payments> payments = paymentsService.paymentHistoryByUsr(id);
                    if (payments == null || payments.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se ha podido traer los pagos");
                    }
                    return ResponseEntity.ok(payments);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al buscar los pagos: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }
}
