package de.tobiaserthal.akgbensheim.backend.model.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import de.tobiaserthal.akgbensheim.backend.model.base.BaseModelBuilder;

public interface EventModelBuilder<T extends EventModelBuilder<?>> extends BaseModelBuilder<T> {
    /**
     * The title of the event.
     * Cannot be {@code null}.
     */
    T putTitle(@NonNull String title);

    /**
     * The date the event is going to take place.
     * Cannot be {@code null}.
     */
    T putEventDate(@NonNull Date date);

    /**
     * The date description for the event.
     * Can be {@code null}.
     */
    T putDateString(@Nullable String dateString);

    /**
     * The description for the event.
     * Can be {@code null}.
     */
    T putDescription(@Nullable String description);
}
