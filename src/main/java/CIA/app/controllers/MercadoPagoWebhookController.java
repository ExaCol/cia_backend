package CIA.app.controllers;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import CIA.app.services.MercadoPagoService;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class MercadoPagoWebhookController {

  private static final Logger log = LoggerFactory.getLogger(MercadoPagoWebhookController.class);
  private final MercadoPagoService mpService;

  @PostMapping("/webhook")
  public ResponseEntity<String> webhookPost(@RequestParam Map<String, String> qp,
      @RequestBody(required = false) Map<String, Object> body) {
    log.info("MP Webhook POST qp={}, body={}", qp, body);
    return process(qp, body);
  }

  @GetMapping("/webhook")
  public ResponseEntity<String> webhookGet(@RequestParam Map<String, String> qp) {
    log.info("MP Webhook GET qp={}", qp);
    return process(qp, null);
  }

  private ResponseEntity<String> process(Map<String, String> qp, Map<String, Object> body) {
    String type = qp.getOrDefault("type", qp.get("topic"));
    String idStr = qp.get("id");
    if (idStr == null && body != null && body.get("data") instanceof Map<?, ?> data) {
      Object oid = data.get("id");
      if (oid != null)
        idStr = String.valueOf(oid);
    }

    if (!"payment".equalsIgnoreCase(type) || idStr == null) {
      log.warn("Ignorando notificación: type={}, id={}", type, idStr);
      return ResponseEntity.ok("ignored");
    }

    Long paymentId = Long.valueOf(idStr);

    String extRef = null, status = null, statusDetail = null;
    if (body != null) {
      Object er = body.get("external_reference");
      Object st = body.get("status");
      Object sd = body.get("status_detail");
      extRef = er != null ? er.toString() : null;
      status = st != null ? st.toString() : null;
      statusDetail = sd != null ? sd.toString() : null;
    }

    try {
      boolean approved = mpService.confirmFromWebhook(paymentId, extRef, status, statusDetail);
      return ResponseEntity.ok(approved ? "approved" : "ok");
    } catch (Exception e) {
      // Responder 200 evita reintentos infinitos, tu lógica ya es idempotente
      return ResponseEntity.ok("processed-with-error:" + e.getMessage());
    }
  }
}
