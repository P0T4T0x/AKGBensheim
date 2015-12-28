package de.tobiaserthal.akgbensheim.backend.rest.model.teacher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.tobiaserthal.akgbensheim.backend.model.teacher.TeacherModel;
import de.tobiaserthal.akgbensheim.backend.rest.model.base.BaseResponse;

public class TeacherResponse extends BaseResponse<TeacherResponse.Entry> {
    public TeacherResponse(Integer code, String message, List<Entry> data) {
        super(code, message, data);
    }

    public class Entry implements TeacherModel {

        @Expose @SerializedName(TeacherKeys.KEY_ID)         private Long id;
        @Expose @SerializedName(TeacherKeys.KEY_FIRSTNAME)  private String firstName;
        @Expose @SerializedName(TeacherKeys.KEY_LASTNAME)   private String lastName;
        @Expose @SerializedName(TeacherKeys.KEY_SHORTHAND)  private String shorthand;
        @Expose @SerializedName(TeacherKeys.KEY_SUBJECTS)   private String subjects;
        @Expose @SerializedName(TeacherKeys.KEY_EMAIL)      private String email;

        public Entry(Long id, String firstName, String lastName, String shorthand, String subjects, String email) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.shorthand = shorthand;
            this.subjects = subjects;
            this.email = email;
        }

        @NonNull
        @Override
        public String getFirstName() {
            return firstName;
        }

        @NonNull
        @Override
        public String getLastName() {
            return lastName;
        }

        @NonNull
        @Override
        public String getShorthand() {
            return shorthand;
        }

        @Nullable
        @Override
        public String getSubjects() {
            return subjects;
        }

        @Nullable
        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public long getId() {
            return id;
        }
    }
}


