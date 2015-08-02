package de.tobiaserthal.akgbensheim.data.model.homework;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import de.tobiaserthal.akgbensheim.data.model.base.BaseModelBuilder;

public interface HomeworkModelBuilder<T extends HomeworkModelBuilder<?>> extends BaseModelBuilder<T> {
    /**
     * Set title to display. Usually contains the subject.
     * Cannot be {@code null}.
     */
    T putTitle(@NonNull String title);

    /**
     * The date until the work has to be done.
     * Cannot be {@code null}.
     */
    T putTodoDate(@NonNull Date todoDate);

    /**
     * The notes that describe what to do.
     * Can be {@code null}.
     */
    T putNotes(@Nullable String notes);

    /**
     * Whether the work has been done.
     */
    T putDone(boolean done);
}
