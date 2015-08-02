package de.tobiaserthal.akgbensheim.data.rest.api;

import com.google.gson.GsonBuilder;

import java.util.List;

import de.tobiaserthal.akgbensheim.data.rest.api.async.ApiEndpoint;
import de.tobiaserthal.akgbensheim.data.rest.api.sync.SyncApiEndpoint;
import de.tobiaserthal.akgbensheim.data.rest.model.base.BaseKeys;
import de.tobiaserthal.akgbensheim.data.rest.model.base.BaseResponse;
import de.tobiaserthal.akgbensheim.data.rest.model.event.EventResponse;
import de.tobiaserthal.akgbensheim.data.rest.model.news.NewsResponse;
import de.tobiaserthal.akgbensheim.data.rest.model.substitution.SubstitutionResponse;
import de.tobiaserthal.akgbensheim.data.rest.model.teacher.TeacherResponse;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class RestServer {
    private static final GsonConverter SQL_DATE_PARSABLE = new GsonConverter(new GsonBuilder().setDateFormat("yyyy-MM-dd").create());

    public static ApiEndpoint getAsynchronousAdapter() {
        RestAdapter adapter = new RestAdapter.Builder().setConverter(SQL_DATE_PARSABLE).setEndpoint(BaseKeys.API_BASE).build();
        return adapter.create(ApiEndpoint.class);
    }

    public static SyncApiEndpoint getSynchronousAdapter() {
        RestAdapter adapter = new RestAdapter.Builder().setConverter(SQL_DATE_PARSABLE).setEndpoint(BaseKeys.API_BASE).build();
        return adapter.create(SyncApiEndpoint.class);
    }

    public static List<SubstitutionResponse> getSubstitutions() throws ApiError {
        BaseResponse<SubstitutionResponse> response = getSynchronousAdapter().getSubstitutions();
        ApiError.check(response);

        return response.getData();
    }

    public static List<EventResponse> getEvents() throws ApiError {
        BaseResponse<EventResponse> response = getSynchronousAdapter().getEvents();
        ApiError.check(response);

        return response.getData();
    }

    public static List<NewsResponse> getNews(int start) throws ApiError {
        BaseResponse<NewsResponse> response = getSynchronousAdapter().getNews(start);
        ApiError.check(response);

        return response.getData();
    }

    public static List<TeacherResponse> getTeachers() throws ApiError {
        BaseResponse<TeacherResponse> response = getSynchronousAdapter().getTeachers();
        ApiError.check(response);

        return response.getData();
    }
}