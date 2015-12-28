package de.tobiaserthal.akgbensheim.event.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.provider.event.EventCursor;
import de.tobiaserthal.akgbensheim.base.adapter.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.base.adapter.BaseViewHolder;
import de.tobiaserthal.akgbensheim.base.adapter.SectionCursorAdapter;

public class EventAdapter extends SectionCursorAdapter<EventCursor, Date,
        EventAdapter.ItemViewHolder, EventAdapter.SectionViewHolder> {

    private final SimpleDateFormat FORMAT_DAY = new SimpleDateFormat("dd", Locale.getDefault());
    private final SimpleDateFormat FORMAT_MONTH = new SimpleDateFormat("MMM", Locale.getDefault());
    private final SimpleDateFormat FORMAT_YEAR = new SimpleDateFormat("yyyy", Locale.getDefault());

    private AdapterClickHandler callbacks;

    public EventAdapter(Context context, EventCursor cursor) {
        super(context, cursor, 0, new SectionCursorAdapter.Comparator<Date>() {
            @Override
            public boolean equal(Date obj1, Date obj2) {
                if(obj1 == null) {
                    return obj2 == null;
                } else {
                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();

                    cal1.setTime(obj1);
                    cal2.setTime(obj2);

                    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                            && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
                }
            }
        });
    }

    public void setOnClickListener(AdapterClickHandler clickListener) {
        this.callbacks = clickListener;
    }

    @Override
    protected Date getSectionFromCursor(EventCursor cursor) throws IllegalStateException {
        return cursor.getEventDate();
    }

    @Override
    protected SectionViewHolder onCreateSectionViewHolder(ViewGroup parent) {
        return new SectionViewHolder(
                LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.events_list_section, parent, false)
        );
    }

    @Override
    protected ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.events_list_item, parent, false)
        );
    }

    class ItemViewHolder extends BaseViewHolder<EventCursor>
            implements View.OnClickListener {

        TextView txtDay;
        TextView txtTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            txtDay = (TextView) itemView.findViewById(R.id.icnDate);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        }

        @Override
        public void bind(EventCursor cursor) {
            txtDay.setText(FORMAT_DAY.format(cursor.getEventDate()));
            txtTitle.setText(cursor.getTitle());
        }

        @Override
        public void onClick(View v) {
            if(callbacks != null)
                callbacks.onClick(v, getCursorPositionWithoutSections(getAdapterPosition()), getItemId());
        }
    }

    class SectionViewHolder extends BaseViewHolder<Date> {
        TextView txtMonth;
        TextView txtYear;

        public SectionViewHolder(View itemView) {
            super(itemView);

            txtMonth = (TextView) itemView.findViewById(android.R.id.text1);
            txtYear = (TextView) itemView.findViewById(android.R.id.text2);
        }

        @Override
        public void bind(Date month) {
            txtMonth.setText(FORMAT_MONTH.format(month));
            txtYear.setText(FORMAT_YEAR.format(month));
        }
    }
}
