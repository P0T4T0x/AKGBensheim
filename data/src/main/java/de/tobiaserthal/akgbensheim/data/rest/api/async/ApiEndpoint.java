package de.tobiaserthal.akgbensheim.data.rest.api.async;

import de.tobiaserthal.akgbensheim.data.rest.model.event.EventKeys;
import de.tobiaserthal.akgbensheim.data.rest.model.event.EventResponse;
import de.tobiaserthal.akgbensheim.data.rest.model.news.NewsKeys;
import de.tobiaserthal.akgbensheim.data.rest.model.news.NewsResponse;
import de.tobiaserthal.akgbensheim.data.rest.model.substitution.SubstitutionKeys;
import de.tobiaserthal.akgbensheim.data.rest.model.substitution.SubstitutionResponse;
import de.tobiaserthal.akgbensheim.data.rest.model.teacher.TeacherKeys;
import de.tobiaserthal.akgbensheim.data.rest.model.teacher.TeacherResponse;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ApiEndpoint {
    @GET(EventKeys.DOMAIN)
    void getEvents(ApiCallback<EventResponse> response);

    @GET(NewsKeys.DOMAIN)
    void getNews(@Query(NewsKeys.ARG_START) int start, @Query(NewsKeys.ARG_COUNT) int count, ApiCallback<NewsResponse> response);

    @GET(SubstitutionKeys.DOMAIN)
    void getSubstitutions(ApiCallback<SubstitutionResponse> response);

    @GET(TeacherKeys.DOMAIN)
    void getTeachers(ApiCallback<TeacherResponse> response);
}
