package CIA.app.gateways;


import org.springframework.stereotype.Component;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import CIA.app.interfaces.MPPaymentGateway;


@Component
class MPPaymentGatewayImpl implements MPPaymentGateway {
  private final PaymentClient client = new PaymentClient();
  @Override public Payment get(Long id) throws MPException, MPApiException {
    return client.get(id);
  }
}
