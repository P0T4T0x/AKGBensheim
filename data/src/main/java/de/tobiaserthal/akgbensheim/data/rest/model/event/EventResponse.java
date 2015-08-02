package de.tobiaserthal.akgbensheim.data.rest.model.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import de.tobiaserthal.akgbensheim.data.model.event.EventModel;

public class EventResponse implements EventModel {
    @Expose @SerializedName(EventKeys.KEY_ID)           private Long id;
    @Expose @SerializedName(EventKeys.KEY_TITLE)        private String title;
    @Expose @SerializedName(EventKeys.KEY_DATE)         private Date eventDate;
    @Expose @SerializedName(EventKeys.KEY_DATESTRING)   private String dateString;
    @Expose @SerializedName(EventKeys.KEY_DESCRIPTION)  private String description;

    public EventResponse(Long _id, String title, Date eventDate, String dateString, String description) {
        this.id = _id;
        this.title = title;
        this.eventDate = eventDate;
        this.dateString = dateString;
        this.description = description;
    }

    @NonNull
    @Override
    public String getTitle() {
        return title;
    }

    @NonNull
    @Override
    public Date getEventDate() {
        return eventDate;
    }

    @Nullable
    @Override
    public String getDateString() {
        return dateString;
    }

    @Nullable
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public long getId() {
        return id;
    }
}
