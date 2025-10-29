package CIA.app.config;

import org.springframework.context.annotation.Configuration;

import com.mercadopago.MercadoPagoConfig;

import CIA.app.components.MercadoPagoProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MercadoPagoConfigBean {
    private final MercadoPagoProperties props;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(props.getAccessToken());
    }
}
