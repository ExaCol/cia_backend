package CIA.app.gateways;

import org.springframework.stereotype.Component;

import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

import CIA.app.interfaces.MPPreferenceGateway;

@Component
class MPPreferenceGatewayImpl implements MPPreferenceGateway {
  private final PreferenceClient client = new PreferenceClient();
  @Override public Preference create(PreferenceRequest req) throws MPException, MPApiException {
    return client.create(req);
  }
}


