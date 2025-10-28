package CIA.app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import CIA.app.services.MercadoPagoService;

import java.util.Map;

/**
 * Debe quedar público (sin JWT) para recibir notificaciones de Mercado Pago.
 * Recibe: POST /api/payments/webhook?type=payment&id=<payment_id>
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class MercadoPagoWebhookController {

    private final MercadoPagoService mpService;

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestParam Map<String, String> queryParams,
            @RequestBody(required = false) Map<String, Object> body) {
        // MP suele enviar: type=payment & id=<payment_id>
        String type = queryParams.getOrDefault("type", queryParams.get("topic"));
        String idStr = queryParams.get("id");

        // Algunas veces viene en el body (según configuración)
        if (idStr == null && body != null && body.get("data") instanceof Map<?, ?> data) {
            Object oid = data.get("id");
            if (oid != null)
                idStr = String.valueOf(oid);
        }

        if (!"payment".equalsIgnoreCase(type) || idStr == null) {
            return ResponseEntity.ok("ignored");
        }

        Long paymentId = Long.valueOf(idStr);

        // Datos opcionales que a veces MP incluye en body
        String extRef = null, status = null, statusDetail = null;
        if (body != null) {
            Object er = body.get("external_reference");
            Object st = body.get("status");
            Object sd = body.get("status_detail");
            extRef = er != null ? er.toString() : null;
            status = st != null ? st.toString() : null;
            statusDetail = sd != null ? sd.toString() : null;
        }

        boolean approved = false;
        try {
            approved = mpService.confirmFromWebhook(paymentId, extRef, status, statusDetail);
            // Aquí, si quieres, también puedes disparar lógica adicional a nivel
            // controller.
        } catch (Exception e) {
            // No devuelvas 500; MP reintentará. Regresa 200 para cortar reintentos si tu
            // servicio ya maneja idempotencia.
            return ResponseEntity.ok("processed-with-error:" + e.getMessage());
        }
        return ResponseEntity.ok(approved ? "approved" : "ok");
    }
}
