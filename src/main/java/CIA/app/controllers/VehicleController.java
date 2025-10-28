package CIA.app.controllers;

import java.sql.Date;
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
import CIA.app.model.Vehicle;
import CIA.app.services.VehicleService;
import io.jsonwebtoken.ExpiredJwtException;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private JwtUtil jwtUtil;

    public VehicleController(VehicleService vehicleService, JwtUtil jwtUtil) {
        this.vehicleService = vehicleService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveVehicle(@RequestHeader("Authorization") String authHeader,
            @RequestBody Vehicle vehicle) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    Vehicle v = vehicleService.saveVehicle(email, vehicle);
                    if (v == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("El vehículo ya está registrado");
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body("Vehículo guardado exitosamente");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al registrar vehículo: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @GetMapping("/byPlate/{plate}")
    public ResponseEntity<?> getByPlate(@RequestHeader("Authorization") String authHeader, @PathVariable String plate) {
        String token = authHeader.replace("Bearer ", "");

        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    Vehicle v = vehicleService.getByPlate(plate.toUpperCase());
                    if (v == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vehículo no encontrado");
                    }
                    return ResponseEntity.ok(v);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener vehículo: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @GetMapping("/vehicles")
    public ResponseEntity<?> getVehicles(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    List<Vehicle> v = vehicleService.getVehicles(email);
                    if (v == null || v.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay vehiculos registrados");
                    }
                    return ResponseEntity.ok(v);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener vehículo: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");

        }
    }

    @DeleteMapping("/byPlate/{plate}")
    public ResponseEntity<?> deleteByPlate(@RequestHeader("Authorization") String authHeader,
            @PathVariable String plate) {
        String token = authHeader.replace("Bearer ", "");
        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    Vehicle v = vehicleService.deleteByPlate(email, plate.toUpperCase());
                    if (v == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vehículo no encontrado");
                    }
                    return ResponseEntity.ok("Vehículo eliminado exitosamente");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al obtener vehículo: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }

    @PatchMapping("/update-vehicle/{id}/{soatExpiration}/{technoExpiration}")
    public ResponseEntity<?> updateVehicle(@RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id, @PathVariable Date soatExpiration, @PathVariable Date technoExpiration) {
        String token = authHeader.replace("Bearer ", "");

        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractUserRole(token);

            if (jwtUtil.isTokenValid(token, email) && "Cliente".equals(role)) {
                try {
                    Vehicle v = vehicleService.update(id, soatExpiration, technoExpiration);
                    if (v == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo actualizar el vehículo");
                    }
                    return ResponseEntity.ok(v);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al actualizar vehículo: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado: requiere rol válido");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }
}
