package CIA.app.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CIA.app.components.JwtUtil;
import CIA.app.model.Partner;
import CIA.app.model.Services;
import CIA.app.services.PartnerService;

@RestController
@RequestMapping("/partners")
public class PartnerController {
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private JwtUtil jwtUtil;

    public PartnerController(PartnerService partnerService, JwtUtil jwtUtil) {
        this.partnerService = partnerService;
        this.jwtUtil = jwtUtil;
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createPayments(@RequestHeader("Authorization") String authHeader, @RequestBody Partner partner, @RequestBody Services services) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractUserRole(token);

        if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role) ) {
            try {
                Partner p = partnerService.createPartner(email, partner, services);
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
    }

    @GetMapping("/partnerByServices")
    public ResponseEntity<?> getByUser(@RequestHeader("Authorization") String authHeader, @RequestBody Services services) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractUserRole(token);

        if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role) ) {
            try {
                List<Partner> p = partnerService.getPartnersByServices(services);
                if (p == null || p.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay aliados con ese servicio registrados");
                }
                return ResponseEntity.ok(p);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al obtener aliados: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
        }
    }

    @GetMapping("/specificPartner")
    public ResponseEntity<?> getPayment(@RequestHeader("Authorization") String authHeader, @RequestBody Partner partner) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractUserRole(token);

        if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role) ) {
            try {
                Partner p = partnerService.getSpecificPartner(partner);
                if (p == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se ha encontrado el aliado");
                }
                return ResponseEntity.ok(p);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al obtener el pago: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
        }
    }

    @DeleteMapping("/specificPartner")
    public ResponseEntity<?> deleteSpecificPayment(@RequestHeader("Authorization") String authHeader, @RequestBody Partner partner) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractUserRole(token);

        if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role) ) {
            try {
                Partner p = partnerService.deleteSpecificPartner(partner);
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
    }
<<<<<<< Updated upstream
=======

    @PatchMapping("/partner")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authHeader, @RequestBody Partner partner) {
        String token = authHeader.replace("Bearer ", "");
        try{
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email)
                    && ("Admin".equals(role) || "Empleado".equals(role))) {
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
        }catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
            
    }
>>>>>>> Stashed changes
}


