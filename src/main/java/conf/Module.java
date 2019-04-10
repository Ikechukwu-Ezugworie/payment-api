/**
 * Copyright (C) 2012-2018 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package conf;

import Adapters.GsonPConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ninja.utils.NinjaProperties;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import services.api.EndSystemApi;
import services.api.RemittaApi;
import services.api.WebPayApi;
import utils.CronSchedule;
import utils.PaymentUtil;

@Singleton
public class Module extends AbstractModule {
    private NinjaProperties ninjaProperties;
//
//
    @Inject
    public Module(NinjaProperties ninjaProperties) {
        super();
        this.ninjaProperties = ninjaProperties;
    }
    protected void configure() {
        bind(OkHttpClient.class).toInstance(PaymentUtil.getOkHttpClient(ninjaProperties));
        bind(StartupActions.class);
        bind(CronSchedule.class);
    }

    @Provides
    private WebPayApi getInterswitchBaseRefrofitApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ninjaProperties.getWithDefault("web.pay.base.url", "https://sandbox.interswitchng.com"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(PaymentUtil.getOkHttpClient(ninjaProperties))
                .build();
        return retrofit.create(WebPayApi.class);
    }

    @Provides
    private EndSystemApi getEndSystemApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .client(PaymentUtil.getOkHttpClient(ninjaProperties))
                .build();
        return retrofit.create(EndSystemApi.class);
    }

    @Provides
    private RemittaApi getRemitterBaseRetrofitApi() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ninjaProperties.getWithDefault("remitta.base.url", "https://remitademo.net"))
                .addConverterFactory(new GsonPConverterFactory(new Gson()))
                .client(PaymentUtil.getOkHttpClient(ninjaProperties))
                .build();
        return retrofit.create(RemittaApi.class);
    }
}
