package Adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.IOException;
import java.io.Reader;

public class GsonPResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonPResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {


        String response = value.string();

        if (response.indexOf('{') == 0) { // this is a json
            return adapter.fromJson(response);
        }
        String formatToJson = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"));
        return adapter.fromJson(formatToJson);


    }
}