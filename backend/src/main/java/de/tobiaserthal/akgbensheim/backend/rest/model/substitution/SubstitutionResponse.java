package de.tobiaserthal.akgbensheim.backend.rest.model.substitution;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import de.tobiaserthal.akgbensheim.backend.model.substitution.SubstitutionModel;
import de.tobiaserthal.akgbensheim.backend.rest.model.base.BaseResponse;

public class SubstitutionResponse extends BaseResponse<SubstitutionResponse.Entry> {
    public SubstitutionResponse(Integer code, String message, List<Entry> data) {
        super(code, message, data);
    }

    public static class Entry implements SubstitutionModel {
        @Expose @SerializedName(SubstitutionKeys.KEY_ID)            private Long id;
        @Expose @SerializedName(SubstitutionKeys.KEY_FORMKEY)       private String formKey;
        @Expose @SerializedName(SubstitutionKeys.KEY_SUBSTDATE)     private Date substDate;
        @Expose @SerializedName(SubstitutionKeys.KEY_PERIOD)        private String period;
        @Expose @SerializedName(SubstitutionKeys.KEY_TYPE)          private String type;
        @Expose @SerializedName(SubstitutionKeys.KEY_LESSON)        private String lesson;
        @Expose @SerializedName(SubstitutionKeys.KEY_LESSONSUBST)   private String lessonSubst;
        @Expose @SerializedName(SubstitutionKeys.KEY_ROOM)          private String room;
        @Expose @SerializedName(SubstitutionKeys.KEY_ROOMSUBST)     private String roomSubst;
        @Expose @SerializedName(SubstitutionKeys.KEY_ANNOTATION)    private String annotation;

        public Entry(Long id, String formKey, Date substDate, String period, String type,
                                    String lesson, String lessonSubst, String room, String roomSubst, String annotation) {
            this.id = id;
            this.formKey = formKey;
            this.substDate = substDate;
            this.period = period;
            this.type = type;
            this.lesson = lesson;
            this.lessonSubst = lessonSubst;
            this.room = room;
            this.roomSubst = roomSubst;
            this.annotation = annotation;
        }

        @NonNull
        @Override
        public String getFormKey() {
            return formKey;
        }

        @NonNull
        @Override
        public Date getSubstDate() {
            return substDate;
        }

        @NonNull
        @Override
        public String getPeriod() {
            return period;
        }

        @NonNull
        @Override
        public String getType() {
            return type;
        }

        @NonNull
        @Override
        public String getLesson() {
            return lesson;
        }

        @NonNull
        @Override
        public String getLessonSubst() {
            return lessonSubst;
        }

        @NonNull
        @Override
        public String getRoom() {
            return room;
        }

        @NonNull
        @Override
        public String getRoomSubst() {
            return roomSubst;
        }

        @Nullable
        @Override
        public String getAnnotation() {
            return annotation;
        }

        @Override
        public long getId() {
            return id;
        }
    }

}
