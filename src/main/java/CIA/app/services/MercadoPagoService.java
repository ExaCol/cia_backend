package CIA.app.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;

import CIA.app.components.MercadoPagoProperties;
import CIA.app.dtos.CheckoutRequest;
import CIA.app.dtos.CheckoutResponse;
import CIA.app.model.Payments;
import CIA.app.model.Services;
import CIA.app.model.Usr;
import CIA.app.repositories.PaymentsRepository;
import CIA.app.repositories.ServicesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MercadoPagoService {
    private final MercadoPagoProperties props;
    private final ServicesRepository servicesRepo;
    private final PaymentsRepository paymentsRepo;

    @Transactional
    public CheckoutResponse createCheckout(CheckoutRequest req, Usr currentUser) throws MPException, MPApiException {
        Services service = servicesRepo.findById(req.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));

        if (service.isPaid()) {
            throw new IllegalStateException("Este servicio ya fue pagado");
        }

        String externalRef = "svc-" + service.getId() + "-" + UUID.randomUUID();

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .id(String.valueOf(service.getId()))
                .title(service.getName() != null ? service.getName() : "Servicio")
                .quantity(1)
                .unitPrice(new BigDecimal(service.getPrice()))
                .currencyId("COP")
                .build();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(props.getBackUrlBase() + "/exito")
                .pending(props.getBackUrlBase() + "/pendiente")
                .failure(props.getBackUrlBase() + "/fallo")
                .build();

        PreferencePayerRequest payer = PreferencePayerRequest.builder()
                .name(currentUser.getName())
                .email(currentUser.getEmail())
                .build();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("serviceId", service.getId());
        metadata.put("usrId", currentUser.getId());

        PreferenceRequest prefReq = PreferenceRequest.builder()
                .items(List.of(item))
                .externalReference(externalRef)
                .backUrls(backUrls)
                .notificationUrl(props.getNotificationUrl())
                .autoReturn("approved")
                .payer(payer)
                .metadata(metadata)
                .statementDescriptor("SmartTraffic")
                .build();

        var pref = new PreferenceClient().create(prefReq);

        // Pre-registrar pago pendiente con vínculo lógico
        Payments pay = new Payments();
        pay.setAmount(service.getPrice());
        pay.setState("pending");
        pay.setUsr(currentUser);
        pay.setServiceId(service.getId()); // <<--- AQUÍ guardas el serviceId
        pay.setExternalReference(externalRef);
        paymentsRepo.save(pay);

        return new CheckoutResponse(pref.getId(), pref.getInitPoint(), pref.getSandboxInitPoint());
    }

    @Transactional
    public boolean confirmFromWebhook(Long paymentId, String externalReference, String mpStatus, String statusDetail)
            throws MPException, MPApiException {

        PaymentClient paymentClient = new PaymentClient();
        Payment payment = paymentClient.get(paymentId); // <-- sin PaymentGetRequest

        String status = payment.getStatus(); // approved / pending / rejected
        String extRef = payment.getExternalReference();
        if (extRef == null)
            extRef = externalReference;

        Payments pay = paymentsRepo.findByExternalReference(extRef)
                .orElseThrow(() -> new IllegalStateException("No se encontró payment por external_reference"));

        pay.setMpPaymentId(paymentId);
        pay.setMpStatusDetail(statusDetail != null ? statusDetail : payment.getStatusDetail());
        pay.setState(status);
        paymentsRepo.save(pay);

        if ("approved".equalsIgnoreCase(status)) {
            servicesRepo.findById(pay.getServiceId()).ifPresent(svc -> {
                if (!svc.isPaid()) {
                    svc.setPaid(true);
                    servicesRepo.save(svc);
                    postPaymentSuccess(svc, pay.getUsr(), pay);
                }
            });
            return true;
        }
        return false;
    }

    private void postPaymentSuccess(Services service, Usr usr, Payments pay) {
        // TODO: tu lógica adicional post-pago (enviar correo, habilitar curso, etc.)
    }
}
