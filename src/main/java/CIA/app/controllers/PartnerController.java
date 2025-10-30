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
import CIA.app.model.Partner;
import CIA.app.services.PartnerService;
import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping("/partners")
public class PartnerController {
    @Autowired
    private final PartnerService partnerService;
    @Autowired
    private final JwtUtil jwtUtil;

    public PartnerController(PartnerService partnerService, JwtUtil jwtUtil) {
        this.partnerService = partnerService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayments(@RequestHeader("Authorization") String authHeader,
            @RequestBody Partner partner) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Admin".equals(role)) {
                try {
                    Partner p = partnerService.createPartner(email, partner);
                    if (p == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuario o servicio no existe");
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body("Aliado guardado exitosamente");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al registrar aliado: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @DeleteMapping("/specificPartner/{id}")
    public ResponseEntity<?> deleteSpecificPayment(@RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Admin".equals(role)) {
                try {
                    Partner p = partnerService.deleteSpecificPartner(id);
                    if (p == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aliado no encontrado");
                    }
                    return ResponseEntity.ok("Aliado eliminado exitosamente");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener Aliado: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @PatchMapping("/partner")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authHeader,
            @RequestBody Partner partner) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Admin".equals(role)) {
                try {
                    Partner p = partnerService.update(email, partner);
                    if (p != null) {
                        return ResponseEntity.ok("Partner actualizado correctamente");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró el partner");
                    }
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al actualizar partner: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @GetMapping("/partners")
    public ResponseEntity<?> getPartners(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Admin".equals(role)) {
                try {
                    List<Partner> p = partnerService.getAllPartners();

                    if (p == null || p.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("No hay aliados registrados");
                    }
                    return ResponseEntity.ok(p);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener aliados: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @GetMapping("/partnerByServices/{serviceId}")
    public ResponseEntity<?> getByUser(@RequestHeader("Authorization") String authHeader,
            @PathVariable String service) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    List<Partner> p = partnerService.getPartnerByService(service);

                    if (p == null || p.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("No hay aliados con ese servicio registrados");
                    }
                    return ResponseEntity.ok(p);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener aliados: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }
}
