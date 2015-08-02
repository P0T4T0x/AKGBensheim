package de.tobiaserthal.akgbensheim.data.model.substitution;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import de.tobiaserthal.akgbensheim.data.model.base.BaseModelBuilder;

public interface SubstitutionModelBuilder<T extends SubstitutionModelBuilder<?>> extends BaseModelBuilder<T> {
    /**
     * The identifier of the form affected by the substitution.
     * Cannot be {@code null}.
     */
    T putFormKey(@NonNull String formKey);

    /**
     * The date the substitution takes place.
     * Cannot be {@code null}.
     */
    T putSubstDate(@NonNull Date substDate);

    /**
     * The peroid affected by the substitution.
     * Cannot be {@code null}.
     */
    T putPeriod(@NonNull String period);

    /**
     * The type of the substitution.
     * Cannot be {@code null}.
     */
    T putType(@NonNull String type);

    /**
     * The affected lesson.
     * Cannot be {@code null}.
     */
    T putLesson(@NonNull String lesson);

    /**
     * The lesson going to take place instead of "lesson".
     * Cannot be {@code null}.
     */
    T putLessonSubst(@NonNull String lessonSubst);

    /**
     * The room for the affected lesson.
     * Cannot be {@code null}.
     */
    T putRoom(@NonNull String room);

    /**
     * The room the lesson is going to take place.
     * Cannot be {@code null}.
     */
    T putRoomSubst(@NonNull String roomSubst);

    /**
     * Annotations for additional infos about this substitution.
     * Can be {@code null}.
     */
    T putAnnotation(@Nullable String annotation);
}
