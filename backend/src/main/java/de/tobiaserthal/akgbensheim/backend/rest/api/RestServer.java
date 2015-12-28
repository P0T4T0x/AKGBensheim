package de.tobiaserthal.akgbensheim.backend.rest.api;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import de.tobiaserthal.akgbensheim.backend.rest.model.base.BaseKeys;
import de.tobiaserthal.akgbensheim.backend.rest.model.event.EventResponse;
import de.tobiaserthal.akgbensheim.backend.rest.model.news.NewsResponse;
import de.tobiaserthal.akgbensheim.backend.rest.model.substitution.SubstitutionResponse;
import de.tobiaserthal.akgbensheim.backend.rest.model.teacher.TeacherResponse;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class RestServer {

    private Retrofit retrofit;
    private ApiEndpoint apiEndpoint;
    private static RestServer instance;

    private RestServer() {
        GsonConverterFactory sqlDateConverter
                = GsonConverterFactory.create(new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd")
                                .create());

        retrofit = new Retrofit.Builder()
                .baseUrl(BaseKeys.API_BASE)
                .addConverterFactory(sqlDateConverter)
                .build();

        apiEndpoint = retrofit.create(ApiEndpoint.class);
    }

    public static RestServer getInstance() {
        if(instance == null) {
            instance = new RestServer();
        }

        return instance;
    }

    public ApiEndpoint getApiEndpoint() {
        return apiEndpoint;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public List<SubstitutionResponse.Entry> getSubstitutions() throws ApiError, IOException {
        Call<SubstitutionResponse> call = getApiEndpoint().getSubstitutions();
        SubstitutionResponse response = call.execute().body();

        ApiError.check(response);
        return response.getData();
    }

    public List<EventResponse.Entry> getEvents() throws ApiError, IOException {
        Call<EventResponse> call = getApiEndpoint().getEvents();
        EventResponse response = call.execute().body();

        ApiError.check(response);
        return response.getData();
    }

    public List<NewsResponse.Entry> getNews(int start, int count) throws ApiError, IOException {
        Call<NewsResponse> call = getApiEndpoint().getNews(start, count);
        NewsResponse response = call.execute().body();

        ApiError.check(response);
        return response.getData();
    }

    public List<TeacherResponse.Entry> getTeachers() throws ApiError, IOException {
        Call<TeacherResponse> call = getApiEndpoint().getTeachers();
        TeacherResponse response = call.execute().body();

        ApiError.check(response);
        return response.getData();
    }
}