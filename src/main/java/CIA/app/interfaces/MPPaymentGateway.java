package CIA.app.interfaces;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

public interface MPPaymentGateway {
    com.mercadopago.resources.payment.Payment get(Long paymentId)
      throws MPException, MPApiException;
}
