package de.ruben.changelog.changelog;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChangeLogFetcher {

    OkHttpClient client = new OkHttpClient();

    private final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    ObjectMapper mapper = new ObjectMapper();

    public List<ChangeLog> fetchChangeLogs(){
        Request request = new Request.Builder()
                .url("http://localhost:8081/changelog/getAll")
                .get()
                .build();

        try(Response response = client.newCall(request).execute()){
            return mapper.readValue(response.body().string(), mapper.getTypeFactory().constructCollectionType(ArrayList.class, ChangeLog.class));
        }catch (IOException exception){
            System.out.println(exception.getStackTrace());
        }

        return null;

    }

}
