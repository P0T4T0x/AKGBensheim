package de.tobiaserthal.akgbensheim.data.rest.api.sync;

import de.tobiaserthal.akgbensheim.data.rest.model.base.BaseResponse;
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

public interface SyncApiEndpoint {
    @GET(EventKeys.DOMAIN)
    BaseResponse<EventResponse> getEvents();

    @GET(NewsKeys.DOMAIN)
    BaseResponse<NewsResponse> getNews(@Query(NewsKeys.ARG_START) int start);

    @GET(SubstitutionKeys.DOMAIN)
    BaseResponse<SubstitutionResponse> getSubstitutions();

    @GET(TeacherKeys.DOMAIN)
    BaseResponse<TeacherResponse> getTeachers();
}
