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

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import services.api.WebPayApi;
import utils.CronSchedule;

@Singleton
public class Module extends AbstractModule {

    protected void configure() {
        bind(OkHttpClient.class).toInstance(new OkHttpClient());
        bind(StartupActions.class);
        bind(CronSchedule.class);
//        bind(WebPayApi.class).toProvider(WebApiProvider.class);
    }

    @Provides
    private WebPayApi getRetrofitApi() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .build();
        return retrofit.create(WebPayApi.class);
    }

    public class WebApiProvider implements Provider<WebPayApi> {

        @Override
        public WebPayApi get() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .build();
            return retrofit.create(WebPayApi.class);
        }
    }
}
