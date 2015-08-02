package de.tobiaserthal.akgbensheim.ui.widget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.tobiaserthal.akgbensheim.data.Log;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DatePickerFragment";

    private DateListener listener;

    public static DatePickerFragment newInstance(Date date) {
        DatePickerFragment fragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putLong("date", date.getTime());

        fragment.setArguments(args);
        return fragment;
    }

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        if(getArguments() != null) {
            calendar.setTimeInMillis(getArguments().getLong("date"));
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public DatePickerFragment setDateListener(DateListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.d(TAG, "onDateSet: %d.%d.%d", dayOfMonth, monthOfYear, year);
        if(listener != null) {
            Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            listener.onDateSelected(calendar.getTime());
        }
    }

    public interface DateListener {
        void onDateSelected(Date date);
    }
}
