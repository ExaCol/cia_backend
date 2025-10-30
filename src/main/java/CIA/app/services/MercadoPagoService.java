package CIA.app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import CIA.app.model.Partner;
import CIA.app.model.Payments;
import CIA.app.model.Services;
import CIA.app.model.Usr;
import CIA.app.repositories.CoursesDataRepository;
import CIA.app.repositories.PartnerRepository;
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
    private final EmailService emailService;
    private final PartnerRepository partnerRepository;
    private final CoursesDataRepository coursesDataRepository;
    private final CoursesDataService coursesDataService;

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

        try {
            PreferenceRequest prefReq = PreferenceRequest.builder()
                    .items(List.of(item))
                    .externalReference(externalRef)
                    .backUrls(backUrls)
                    .notificationUrl(props.getNotificationUrl())
                    // .autoReturn("approved")
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
            pay.setServiceId(service.getId());
            pay.setExternalReference(externalRef);
            pay.setReleaseDate(LocalDate.now());
            paymentsRepo.save(pay);

            return new CheckoutResponse(pref.getId(), pref.getInitPoint(), pref.getSandboxInitPoint());
        } catch (MPApiException e) {
            int status = e.getApiResponse().getStatusCode();
            String body = e.getApiResponse().getContent();
            System.err.println("[MP ERROR] status=" + status + " body=" + body);
            throw new RuntimeException("MercadoPago API error: " + body);
        } catch (MPException e) {
            System.err.println("[MP ERROR] " + e.getMessage());
            throw new RuntimeException("MercadoPago SDK error: " + e.getMessage());
        }
    }

    @Transactional
    public boolean confirmFromWebhook(Long paymentId, String externalReference, String mpStatus, String statusDetail)
            throws MPException, MPApiException {

        PaymentClient paymentClient = new PaymentClient();
        Payment payment = paymentClient.get(paymentId);

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
        String mensaje = "";
        String asunto = "";
        Partner partner = partnerRepository.findById(service.getPartner().getId()).get();
        String urlMapa = "https://www.google.com/maps?q=" + partner.getLat() + "," + partner.getLon();
        String serviceType = service.getServiceType().toUpperCase();

        if (serviceType.equals("COURSE")) {
            int courseId = coursesDataRepository.idByCourseType(service.getCourseType());
            String enroll = coursesDataService.enroll(usr.getId(), courseId);
            asunto = "Confirmación de pago - Curso";

            if(enroll != null){
                mensaje = "Hola, " + usr.getName() + ".\n"
                    + "¡Tu pago fue confirmado! 🎉\n"
                    + "Detalles del pago:\n"
                    + "• Servicio: Curso" + "\n"
                    + "• Categoría del curso:" + service.getCourseType() + "\n"
                    + "• Monto: " + service.getPrice() + "\n"
                    + "• Fecha: " + pay.getReleaseDate() + "\n"
                    + "• Partner: " + partner.getName() + " ubicado en " + urlMapa + "\n"
                    + "• Ref. externa: " + pay.getExternalReference() + "\n"
                    + "• Pago MP ID: " + pay.getMpPaymentId() + "\n"
                    + "Ya fuiste inscrito al curso exitosamente.\n"
                    + "👉Acércate a nuestra sucursal.\n"
                    + "Si no reconoces este pago, contáctanos de inmediato respondiendo a este correo.\n"
                    + "Saludos,\n"
                    + "El equipo de SmartTraffic.";
            }else{
                mensaje = "Hola, " + usr.getName() + ".\n"
                    + "¡Tu pago fue confirmado! 🎉\n"
                    + "Detalles del pago:\n"
                    + "• Servicio: Curso" + "\n"
                    + "• Categoría del curso:" + service.getCourseType() + "\n"
                    + "• Monto: " + service.getPrice() + "\n"
                    + "• Fecha: " + pay.getReleaseDate() + "\n"
                    + "• Partner: " + partner.getName() + " ubicado en " + urlMapa + "\n"
                    + "• Ref. externa: " + pay.getExternalReference() + "\n"
                    + "• Pago MP ID: " + pay.getMpPaymentId() + "\n"
                    + "No pudimos inscribirte al curso. De haber más interesados en este curso, te notificaremos y abriremos nuevos cupos.\n"
                    + "👉Acércate a nuestra sucursal.\n"
                    + "Si no reconoces este pago, contáctanos de inmediato respondiendo a este correo.\n"
                    + "Saludos,\n"
                    + "El equipo de SmartTraffic.";
            }
            
        } else if (serviceType.equals("SOAT")) {
            asunto = "Confirmación de pago - Soat";
            mensaje = "Hola, " + usr.getName() + ".\n"
                    + "¡Tu pago fue confirmado! 🎉\n"
                    + "Detalles del pago:\n"
                    + "• Servicio: Soat" + "\n"
                    + "• Placa: " + service.getPlate() + "\n"
                    + "• Monto: " + service.getPrice() + "\n"
                    + "• Fecha: " + pay.getReleaseDate() + "\n"
                    + "• Partner: " + partner.getName() + " ubicado en " + urlMapa + "\n"
                    + "• Ref. externa: " + pay.getExternalReference() + "\n"
                    + "• Pago MP ID: " + pay.getMpPaymentId() + "\n"
                    + "👉Acércate a nuestra sucursal.\n"
                    + "Si no reconoces este pago, contáctanos de inmediato respondiendo a este correo.\n"
                    + "Saludos,\n"
                    + "El equipo de SmartTraffic.";

        } else if (serviceType.equals("TECNO")) {
            asunto = "Confirmación de pago - Tecnomecánica";
            mensaje = "Hola, " + usr.getName() + ".\n"
                    + "¡Tu pago fue confirmado! 🎉\n"
                    + "Detalles del pago:\n"
                    + "• Servicio: Tecnomecánica" + "\n"
                    + "• Placa: " + service.getPlate() + "\n"
                    + "• Monto: " + service.getPrice() + "\n"
                    + "• Fecha: " + pay.getReleaseDate() + "\n"
                    + "• Partner: " + partner.getName() + " ubicado en " + urlMapa + "\n"
                    + "• Ref. externa: " + pay.getExternalReference() + "\n"
                    + "• Pago MP ID: " + pay.getMpPaymentId() + "\n"
                    + "👉Acércate a nuestra sucursal.\n"
                    + "Si no reconoces este pago, contáctanos de inmediato respondiendo a este correo.\n"
                    + "Saludos,\n"
                    + "El equipo de SmartTraffic.";
        }
        emailService.enviarCorreo(usr.getEmail(), asunto, mensaje);
    }
}
