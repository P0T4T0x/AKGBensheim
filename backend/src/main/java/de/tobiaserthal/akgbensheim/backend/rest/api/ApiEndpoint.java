package de.tobiaserthal.akgbensheim.backend.rest.api;

import de.tobiaserthal.akgbensheim.backend.rest.model.event.EventKeys;
import de.tobiaserthal.akgbensheim.backend.rest.model.event.EventResponse;
import de.tobiaserthal.akgbensheim.backend.rest.model.news.NewsKeys;
import de.tobiaserthal.akgbensheim.backend.rest.model.news.NewsResponse;
import de.tobiaserthal.akgbensheim.backend.rest.model.substitution.SubstitutionKeys;
import de.tobiaserthal.akgbensheim.backend.rest.model.substitution.SubstitutionResponse;
import de.tobiaserthal.akgbensheim.backend.rest.model.teacher.TeacherKeys;
import de.tobiaserthal.akgbensheim.backend.rest.model.teacher.TeacherResponse;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * A java interface for the Retrofit API that defines the REST server interface methods
 * and allows Retrofit to access and parse the required set of data
 */
public interface ApiEndpoint {
    @GET(EventKeys.DOMAIN)
    Call<EventResponse> getEvents();

    @GET(NewsKeys.DOMAIN)
    Call<NewsResponse> getNews(@Query(NewsKeys.ARG_START) int start, @Query(NewsKeys.ARG_COUNT) int count);

    @GET(SubstitutionKeys.DOMAIN)
    Call<SubstitutionResponse> getSubstitutions();

    @GET(TeacherKeys.DOMAIN)
    Call<TeacherResponse> getTeachers();
}
