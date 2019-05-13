package services;

import com.google.inject.Inject;
import dao.BaseDao;
import ninja.utils.NinjaProperties;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import services.api.EndSystemApi;
import utils.Constants;
import utils.PaymentUtil;

public class EndSystemService {
    @Inject
    NinjaProperties ninjaProperties;
    @Inject
    private BaseDao baseDao;

    public EndSystemApi getApiCaller() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseDao.getSettingsValue(Constants.END_SYSTEM_BASE_URL, "http://localhost:9090/v1/", true))
                .addConverterFactory(GsonConverterFactory.create())
                .client(PaymentUtil.getOkHttpClient(ninjaProperties))
                .build();
        return retrofit.create(EndSystemApi.class);

    }
}
