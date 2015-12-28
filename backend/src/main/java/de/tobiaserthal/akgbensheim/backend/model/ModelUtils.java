package de.tobiaserthal.akgbensheim.backend.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.IntDef;
import android.support.v4.app.ShareCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.tobiaserthal.akgbensheim.backend.R;
import de.tobiaserthal.akgbensheim.backend.model.event.EventModel;
import de.tobiaserthal.akgbensheim.backend.model.homework.HomeworkModel;
import de.tobiaserthal.akgbensheim.backend.model.news.NewsModel;
import de.tobiaserthal.akgbensheim.backend.model.substitution.SubstitutionModel;
import de.tobiaserthal.akgbensheim.backend.model.teacher.TeacherModel;
import de.tobiaserthal.akgbensheim.backend.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.backend.provider.homework.HomeworkContentValues;
import de.tobiaserthal.akgbensheim.backend.rest.model.foodplan.FoodPlanKeys;

public class ModelUtils {
    public static final int ALL = 0x1F;
    public static final int NONE = 0x0;
    public static final int NEWS = 0x2;
    public static final int EVENTS = 0x1;
    public static final int TEACHERS = 0x8;
    public static final int HOMEWORK = 0x10;
    public static final int SUBSTITUTIONS = 0x4;

    @IntDef({ALL, NONE, NEWS, EVENTS, TEACHERS, HOMEWORK, SUBSTITUTIONS})
    @Retention(RetentionPolicy.SOURCE)
    @interface ModelType {}

    private static final SimpleDateFormat COMPARE_DAY = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    public static long getMergedId(@ModelType int type, long id) {
        return ((id << 8) | type);
    }

    @ModelType
    public static int getTypeFromMergedId(long mergedId) {
        //noinspection ResourceType
        return (int) (mergedId & 0xFF);
    }

    public static long getIdFromMergedId(long mergedId) {
        return mergedId >> 8;
    }


    public static boolean equal(EventModel model1, EventModel model2) {
        return model1.getId() == model2.getId()
                && equal(model1.getEventDate(), model2.getEventDate())
                && equal(model1.getTitle(), model2.getTitle())
                && equal(model1.getDateString(), model2.getDateString())
                && equal(model1.getDescription(), model2.getDescription());
    }

    public static boolean equal(HomeworkModel model1, HomeworkModel model2) {
        return model1.getId() == model2.getId()
                && model1.getDone() == model2.getDone()
                && equal(model1.getTodoDate(), model2.getTodoDate())
                && equal(model1.getTitle(), model2.getTitle())
                && equal(model1.getNotes(), model2.getNotes());
    }

    public static boolean equal(NewsModel model1, NewsModel model2) {
        return model1.getId() == model2.getId()
                && model1.getBookmarked() == model2.getBookmarked()
                && equal(model1.getTitle(), model2.getTitle())
                && equal(model1.getArticle(), model2.getArticle())
                && equal(model1.getArticleUrl(), model2.getArticleUrl())
                && equal(model1.getImageUrl(), model2.getImageUrl())
                && equal(model1.getImageDesc(), model2.getImageDesc());
    }

    public static boolean equal(SubstitutionModel model1, SubstitutionModel model2) {
        return model1.getId() == model2.getId()
                && equal(model1.getSubstDate(), model2.getSubstDate())
                && equal(model1.getFormKey(), model2.getFormKey())
                && equal(model1.getPeriod(), model2.getPeriod())
                && equal(model1.getType(), model2.getType())
                && equal(model1.getLesson(), model2.getLesson())
                && equal(model1.getLessonSubst(), model2.getLessonSubst())
                && equal(model1.getRoom(), model2.getRoom())
                && equal(model1.getRoomSubst(), model2.getRoomSubst());
    }

    public static boolean equal(TeacherModel model1, TeacherModel model2) {
        return model1.getId() == model2.getId()
                && equal(model1.getFirstName(), model2.getFirstName())
                && equal(model1.getLastName(), model2.getLastName())
                && equal(model1.getShorthand(), model2.getShorthand())
                && equal(model1.getSubjects(), model2.getSubjects())
                && equal(model1.getEmail(), model2.getEmail());
    }

    public static boolean matchesUserSettings(SubstitutionModel model) {
        int phase = PreferenceProvider.getInstance().getSubstPhase();
        String form = PreferenceProvider.getInstance().getSubstForm();
        String[] filter = PreferenceProvider.getInstance().getSubstSubjects();

        if(phase < PreferenceProvider.getSubstPhaseSek2()) {
            return model.getFormKey().startsWith(String.format("K%02d", phase))
                    && model.getFormKey().contains(form)
                    && arrayContains(filter, model.getLessonSubst());
        } else {
            return model.getFormKey().startsWith(String.format("K%02d", phase))
                    && arrayContains(filter, model.getLessonSubst());
        }
    }

    public static boolean arrayContains(String[] array, String query) {
        if(array == null) {
            return false;
        }

        for(int i = 0; i < array.length; i ++) {
            if(equal(array[i], query)) {
                return true;
            }
        }

        return false;
    }

    public static boolean equal(Date obj1, Date obj2) {
        return COMPARE_DAY.format(obj1).equals(COMPARE_DAY.format(obj2));
    }

    public static boolean equal(String obj1, String obj2) {
        return (obj1 == obj2)
                || ((obj1 != null) && obj1.equals(obj2))
                || ((obj1 == null) && obj2.isEmpty())
                || ((obj2 == null) && obj1.isEmpty());
    }

    public static String defaultFormat(Date obj) {
        return SimpleDateFormat.getDateInstance(
                DateFormat.DEFAULT, Locale.getDefault()
        ).format(obj);
    }

    public static String summarize(EventModel model) {
        return defaultFormat(model.getEventDate()) + ": " + model.getTitle();
    }

    public static String summarize(HomeworkModel model) {
        return "";
    }

    public static String summarize(NewsModel model) {
        return "";
    }

    public static String summarize(SubstitutionModel model) {
        return model.getType() + ": " + model.getLesson();
    }

    public static String summarize(TeacherModel model) {
        return "";
    }

    public static void startShareIntent(Activity activity, SubstitutionModel model) {
        if (model == null)
            return;

        final Resources res = activity.getResources();
        final Intent share = ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setSubject(summarize(model))
                .setText(defaultFormat(model.getSubstDate()) + res.getString(
                        R.string.share_subst_text,
                        model.getPeriod(),
                        model.getLessonSubst(),
                        model.getRoomSubst(),
                        model.getLesson()
                ))
                .setChooserTitle(R.string.share_action_chooser)
                .createChooserIntent();

        activity.startActivity(share);
    }

    public static void startShareIntent(Activity activity, NewsModel model) {
        if(model == null)
            return;

        final Resources res = activity.getResources();
        final Intent share = ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setSubject(model.getTitle())
                .setText(res.getString(R.string.share_news_text,
                        model.getSnippet(),
                        model.getArticleUrl()))
                .setChooserTitle(R.string.share_action_chooser)
                .createChooserIntent();

        activity.startActivity(share);
    }

    public static void startShareIntent(Activity activity, EventModel model) {
        if(model == null) {
            return;
        }

        final Resources res = activity.getResources();
        final Intent share = ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setSubject(model.getTitle())
                .setText(res.getString(R.string.share_events_text,
                                model.getDateString(),
                                model.getTitle(),
                                model.getDescription()))
                .setChooserTitle(R.string.share_action_chooser)
                .createChooserIntent();

        activity.startActivity(share);
    }

    public static void startCalendarIntent(Activity activity, SubstitutionModel model) {
        if(model == null)
            return;

        final Resources res = activity.getResources();
        final Intent calendar;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            calendar = new Intent(Intent.ACTION_EDIT);
            calendar.setType("vnd.android.cursor.item/event");
        } else {
            calendar = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI);
        }

        calendar.putExtra("title", summarize(model));
        calendar.putExtra("eventLocation", res.getString(R.string.address));
        calendar.putExtra("description", res.getString(
                R.string.share_subst_calendar,
                model.getPeriod(),
                model.getLessonSubst(),
                model.getRoomSubst(),
                model.getLesson()
            )
        );

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(model.getSubstDate());

        calendar.putExtra("allDay", true);
        calendar.putExtra("beginTime", cal.getTimeInMillis());
        calendar.putExtra("endTime", cal.getTimeInMillis());

        activity.startActivity(calendar);
    }

    public static void startCalendarIntent(Activity activity, EventModel model) {
        if(model == null) {
            return;
        }

        final Resources res = activity.getResources();
        final Intent calendar;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            calendar = new Intent(Intent.ACTION_EDIT);
            calendar.setType("vnd.android.cursor.item/event");
        } else {
            calendar = new Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI);
        }

        calendar.putExtra("title", model.getTitle());
        calendar.putExtra("eventLocation", res.getString(R.string.address));
        calendar.putExtra("description", model.getDateString() + ", " + model.getDescription());

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(model.getEventDate());

        calendar.putExtra("allDay", true);
        calendar.putExtra("beginTime", cal.getTimeInMillis());
        calendar.putExtra("endTime", cal.getTimeInMillis());

        activity.startActivity(calendar);
    }

    public static void startMailIntent(Activity activity, TeacherModel model) {
        if(model == null)
            return;

        final Intent intent = ShareCompat.IntentBuilder.from(activity)
                .setType("message/rfc822")
                .setEmailTo(new String[]{model.getEmail()})
                .setChooserTitle(R.string.share_action_chooser_email)
                .createChooserIntent();

        activity.startActivity(intent);
    }

    public static void startFoodPlanIntent(Activity activity) {
        final Intent share = ShareCompat.IntentBuilder.from(activity)
                .setStream(FoodPlanKeys.getDefaultCacheUri(activity))
                .setChooserTitle(R.string.share_action_chooser)
                .setType("application/pdf")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activity.startActivity(share);
    }

    public static void startContactsIntent(Activity activity, TeacherModel model) {
        if(model == null)
            return;

        final Resources res = activity.getResources();
        final Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, model.getFirstName() + " " + model.getLastName());
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, model.getEmail());
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        intent.putExtra(ContactsContract.Intents.Insert.COMPANY, res.getString(R.string.detail_teacher_company));
        intent.putExtra(ContactsContract.Intents.Insert.NOTES, res.getString(R.string.detail_teacher_shorthand, model.getShorthand()));
        intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, res.getString(R.string.detail_teacher_subjects, model.getSubjects()));
        intent.putExtra("finishActivityOnSaveCompleted", true);

        activity.startActivity(intent);
    }

    public static void newDummyHomework(Context context) {
        String title = context.getResources().getString(R.string.homework_dummy_title);
        String notes = context.getResources().getString(R.string.homework_dummy_notes);

        Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
        calendar.add(GregorianCalendar.DATE, 1);

        new HomeworkContentValues()
                .putTitle(title)
                .putNotes(notes)
                .putDone(false)
                .putTodoDate(calendar.getTime())
                .insert(context.getContentResolver());
    }
}
