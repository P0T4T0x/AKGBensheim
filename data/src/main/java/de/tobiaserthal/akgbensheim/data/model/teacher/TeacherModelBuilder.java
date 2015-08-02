package de.tobiaserthal.akgbensheim.data.model.teacher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.tobiaserthal.akgbensheim.data.model.base.BaseModelBuilder;

public interface TeacherModelBuilder<T extends TeacherModelBuilder<?>> extends BaseModelBuilder<T> {
    /**
     * The teacher's first name.
     * Cannot be {@code null}.
     */
    T putFirstName(@NonNull String firstName);

    /**
     * The teacher's last name.
     * Cannot be {@code null}.
     */
    T putLastName(@NonNull String lastName);

    /**
     * The teacher's shorthand, which is unique.
     * Cannot be {@code null}.
     */
    T putShorthand(@NonNull String shorthand);

    /**
     * A comma seperated list of subject this teacher teaches.
     * Can be {@code null}.
     */
    T putSubjects(@Nullable String subjects);

    /**
     * The teacher's email adress.
     * Can be {@code null}.
     */
    T putEmail(@Nullable String email);
}
