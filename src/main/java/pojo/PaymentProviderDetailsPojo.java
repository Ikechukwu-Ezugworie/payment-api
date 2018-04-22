package dto;

import com.bw.payment.entity.PaymentProviderDetails;
import com.bw.payment.enumeration.PaymentProviderConstant;

public class PaymentProviderDetailsPojo {
    private Long id;
    private String name;
    private String merchantId;
    private String apiKey;
    private String providerUrl;
    private String serviceUsername;
    private String servicePassword;
}
