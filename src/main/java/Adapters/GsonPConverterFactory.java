package Adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public final class GsonPConverterFactory extends Converter.Factory {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        Gson gson;

        public GsonPConverterFactory(Gson gson) {
            if (gson == null) throw new NullPointerException("gson == null");
            this.gson = gson;
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                                Retrofit retrofit) {

            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            return new GsonPResponseBodyConverter<>(gson, adapter);
        }

        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                              Annotation[] parameterAnnotations,
                                                              Annotation[] methodAnnotations,
                                                              Retrofit retrofit) {

            TypeAdapter<?> adapter = this.gson.getAdapter(TypeToken.get(type));
            return new JsonRequestBodyConverter<>(gson,adapter);
        }
    }

