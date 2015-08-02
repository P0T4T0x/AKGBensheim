package de.tobiaserthal.akgbensheim.data.rest.model.teacher;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import de.tobiaserthal.akgbensheim.data.model.teacher.TeacherModel;

/**
 * Created by tobiaserthal on 07.06.15.
 */
public class TeacherResponse implements TeacherModel {
    
    @Expose @SerializedName(TeacherKeys.KEY_ID)         private Long id;
    @Expose @SerializedName(TeacherKeys.KEY_FIRSTNAME)  private String firstName;
    @Expose @SerializedName(TeacherKeys.KEY_LASTNAME)   private String lastName;
    @Expose @SerializedName(TeacherKeys.KEY_SHORTHAND)  private String shorthand;
    @Expose @SerializedName(TeacherKeys.KEY_SUBJECTS)   private String subjects;
    @Expose @SerializedName(TeacherKeys.KEY_EMAIL)      private String email;

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
