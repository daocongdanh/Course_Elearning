package group.project.cursusonlinecoursemanagement.shared.config.payment;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaypalConfig {

    @Value("${paypal.client-id}")
    private String clientId;
    @Value("${paypal.secret-key}")
    private String secretKey;
    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> paypalSdkConfig = new HashMap<>();
        paypalSdkConfig.put("mode", mode);
        return paypalSdkConfig;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(clientId, secretKey, paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() {
        APIContext context = new APIContext(clientId, secretKey, mode);
        context.setConfigurationMap(paypalSdkConfig());
        return context;
    }

}
