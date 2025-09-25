package CIA.app.controllers;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CIA.app.components.JwtUtil;
import CIA.app.services.PaymentsService;
import CIA.app.services.ServicesService;
import CIA.app.services.UsrService;
import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.core.ipc.http.HttpSender.Response;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    public PaymentsService paymentsService;
    public ServicesService servicesService;
    public JwtUtil jwtUtil;
    public UsrService usrService;

    public StatisticsController(PaymentsService paymentsService, ServicesService servicesService, JwtUtil jwtUtil,
            UsrService usrService) {
        this.paymentsService = paymentsService;
        this.servicesService = servicesService;
        this.jwtUtil = jwtUtil;
        this.usrService = usrService;
    }

    // numero de pagos realizados en un intervalo de fechas
    @GetMapping("/paymentNumber")
    public ResponseEntity<?> PaymentNumber(@RequestHeader("Authorization") String authHeader,
            @RequestParam("strtDate") String startDate, @RequestParam("endDate") String endDate) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && ("Admin".equals(role))) {

                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                System.out.println(start);
                System.out.println(end);
                int num = paymentsService.getPaymentNumber(start, end);
                if (num >= 0) {
                    return ResponseEntity.ok("el numero de pagos hechos fue " + num);
                } else {
                    return ResponseEntity.badRequest()
                            .body("No se encontraron pagos en el intervalo de fechas proporcionado.");

                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol ADMIN");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado ");
        }

    }

    // ganancia neta
    @GetMapping("/netWorth")
    public ResponseEntity<?> netWorth(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && ("Admin".equals(role))) {
                Double num = paymentsService.getNetWorth();
                if (num != null) {
                    return ResponseEntity.ok("la ganancia neta es de: " + num);
                } else {
                    return ResponseEntity.badRequest().body("No se encontraron pagos.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol ADMIN");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado ");
        }
    }

    // ganancia negocio
    @GetMapping("/businessWorth")
    public ResponseEntity<?> businessWorth(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && ("Admin".equals(role))) {
                Double num = paymentsService.getNetWorth() * 0.2;
                if (num != null) {
                    return ResponseEntity.ok("la ganancia neta es de: " + num);
                } else {
                    return ResponseEntity.badRequest().body("No se encontraron pagos.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol ADMIN");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado ");
        }
    }

    // dinero ahorrado por usuarios que pagaron el curso caso simit
    @GetMapping("/savedMoney")
    public ResponseEntity<?> savedMoney(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && ("Admin".equals(role))) {
                Double num = paymentsService.savedUsrMoney();
                if (num != null) {
                    return ResponseEntity.ok("el dinero ahorrado por los usuarios es de: " + num);
                } else {
                    return ResponseEntity.badRequest().body("No se encontraron pagos.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol ADMIN");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado ");
        }
    }

    // ganancia por categoria de servicio
    @GetMapping("/earningsByCat")
    public ResponseEntity<?> earningsByCat(@RequestHeader("Authorization") String authHeader,
            @RequestParam("type") String type) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && ("Admin".equals(role))) {
                Double num = paymentsService.earningsByCat(type);
                if (num != null) {
                    return ResponseEntity.ok("ganancias por " + type + " son: " + num);
                } else {
                    return ResponseEntity.badRequest().body("No se encontraron pagos.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol ADMIN");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado ");
        }

    }

    // conductores formados en un intervalo de fechas
    @GetMapping("/graduatedUsr")
    public ResponseEntity<?> GraduatedUsr(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && ("Admin".equals(role))) {
                Integer num = paymentsService.graduatedNum();
                if (num != null) {
                    return ResponseEntity.ok("el numero de nuevos conductores es: " + num);
                } else {
                    return ResponseEntity.badRequest().body("No se encontraron nuevos conductores");
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol ADMIN");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado ");
        }
    }
}
