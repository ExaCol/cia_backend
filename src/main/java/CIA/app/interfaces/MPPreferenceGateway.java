package CIA.app.interfaces;

import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

public interface MPPreferenceGateway {
    com.mercadopago.resources.preference.Preference create(PreferenceRequest req)
      throws MPException, MPApiException;
}
