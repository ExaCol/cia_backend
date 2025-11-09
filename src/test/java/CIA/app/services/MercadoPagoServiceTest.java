package CIA.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;

import CIA.app.components.MercadoPagoProperties;
import CIA.app.dtos.CheckoutRequest;
import CIA.app.dtos.CheckoutResponse;
import CIA.app.interfaces.MPPaymentGateway;
import CIA.app.interfaces.MPPreferenceGateway;
import CIA.app.model.CoursesData;
import CIA.app.model.Partner;
import CIA.app.model.Payments;
import CIA.app.model.Services;
import CIA.app.model.Usr;
import CIA.app.repositories.CoursesDataRepository;
import CIA.app.repositories.PartnerRepository;
import CIA.app.repositories.PaymentsRepository;
import CIA.app.repositories.ServicesRepository;

@ExtendWith(MockitoExtension.class)
class MercadoPagoServiceTest {

  @Mock MercadoPagoProperties mercadoPagoProps;
  @Mock ServicesRepository servicesRepository;
  @Mock PaymentsRepository paymentsRepository;
  @Mock EmailService emailService;
  @Mock PartnerRepository partnerRepository;
  @Mock CoursesDataRepository coursesDataRepository;
  @Mock CoursesDataService coursesDataService;

  @Mock MPPreferenceGateway mpPreferenceGateway;
  @Mock MPPaymentGateway mpPaymentGateway;

  @InjectMocks MercadoPagoService mercadoPagoService;   

  @Test
  void createCheckout_ok() throws Exception {
    Integer serviceId = 10;

    Services svc = new Services();
    svc.setId(serviceId);
    svc.setName("Curso");
    svc.setPrice(123_000);
    svc.setPaid(false);

    Usr usr = new Usr(); usr.setId(7); usr.setName("Nico"); usr.setEmail("nico@example.com");

    when(servicesRepository.findById(serviceId)).thenReturn(Optional.of(svc));
    when(mercadoPagoProps.getBackUrlBase()).thenReturn("http://localhost:3000/pagos");
    when(mercadoPagoProps.getNotificationUrl()).thenReturn("https://ngrok/webhook");

    // mock Preference de solo getters
    var pref = mock(com.mercadopago.resources.preference.Preference.class);
    when(pref.getId()).thenReturn("pref-abc");
    when(pref.getInitPoint()).thenReturn("https://mp/init");
    when(pref.getSandboxInitPoint()).thenReturn("https://mp/sandbox");
    // capturar el request:
    ArgumentCaptor<PreferenceRequest> prefReqCap = ArgumentCaptor.forClass(PreferenceRequest.class);
    when(mpPreferenceGateway.create(prefReqCap.capture())).thenReturn(pref);

    var resp = mercadoPagoService.createCheckout(new CheckoutRequest(serviceId), usr);

    assertEquals("pref-abc", resp.getPreferenceId());
    var sent = prefReqCap.getValue();
    assertTrue(sent.getExternalReference().startsWith("svc-" + serviceId + "-"));
    assertEquals("http://localhost:3000/pagos/exito", sent.getBackUrls().getSuccess());
    
    verify(servicesRepository).findById(serviceId);
    verify(paymentsRepository).save(any(Payments.class));
  }

  @Test
  void confirmFromWebhook_approved_ok() throws Exception {
  
    Long paymentId = 1L;
    String externalReference = "svc-10-xyz";
    String mpStatus = "approved";
    String statusDetail = "accredited";

    // mock Payment de solo getters
    var payment = mock(com.mercadopago.resources.payment.Payment.class);
    when(payment.getStatus()).thenReturn(mpStatus);
    when(payment.getExternalReference()).thenReturn(externalReference);
    when(mpPaymentGateway.get(paymentId)).thenReturn(payment);
    
    Payments pay = new Payments();
    when(paymentsRepository.findByExternalReference(externalReference))
      .thenReturn(Optional.of(pay));

    boolean result = mercadoPagoService.confirmFromWebhook(paymentId, externalReference, mpStatus, statusDetail);

    assertTrue(result);
    assertEquals("approved", pay.getState());
    assertEquals("accredited", pay.getMpStatusDetail());
    verify(paymentsRepository).findByExternalReference(externalReference);

  }

  @Test
  void postPaymentSuccess_ok() throws Exception {
    // Preparar datos
    Partner partner = new Partner();
    partner.setName("Partner 1");
    partner.setId(3);
    partner.setLat(1.0000);
    partner.setLon(1.0000);
    Services svc = new Services();
    svc.setId(1);
    svc.setName("Curso");
    svc.setServiceType("COURSE");
    svc.setCourseType("A1");
    svc.setPartner(partner);
    svc.setPrice(500000);
    
    Payments pay = new Payments();
    pay.setId(5);
    pay.setExternalReference("svc-10-xyz");
    pay.setState("approved");
    pay.setAmount(120000);
    pay.setServiceId(svc.getId());
    pay.setReleaseDate(LocalDate.now());
    pay.setMpPaymentId(9L);
    Usr usr = new Usr();
    usr.setId(2);
    usr.setName("Nico");
    usr.setEmail("nico@exa.co");
  
    
    String urlMapa = "https://www.google.com/maps?q=" + partner.getLat() + "," + partner.getLon();

    CoursesData crs = new CoursesData();
    crs.setId(2);

    when(partnerRepository.findById(partner.getId()))
      .thenReturn(Optional.of(partner));
    when(coursesDataRepository.idByCourseType(svc.getCourseType())).thenReturn(crs.getId());
    when(coursesDataService.enroll(usr.getId(), crs.getId()))
      .thenReturn("Cliente inscrito a curso exitosamente");
    
  mercadoPagoService.postPaymentSuccess(svc, usr,pay);

    verify(emailService).enviarCorreo(usr.getEmail(), "Confirmación de pago - Curso", "Hola, " + usr.getName() + ".\n"
                        + "¡Tu pago fue confirmado! 🎉\n"
                        + "Detalles del pago:\n"
                        + "• Servicio: Curso" + "\n"
                        + "• Categoría del curso:" + svc.getCourseType() + "\n"
                        + "• Monto: " + svc.getPrice() + "\n"
                        + "• Fecha: " + pay.getReleaseDate() + "\n"
                        + "• Partner: " + partner.getName() + " ubicado en " + urlMapa + "\n"
                        + "• Ref. externa: " + pay.getExternalReference() + "\n"
                        + "• Pago MP ID: " + pay.getMpPaymentId() + "\n"
                        + "Ya fuiste inscrito al curso exitosamente.\n"
                        + "👉Acércate a nuestra sucursal.\n"
                        + "Si no reconoces este pago, contáctanos de inmediato respondiendo a este correo.\n"
                        + "Saludos,\n"
                        + "El equipo de SmartTraffic.");


    }
}

